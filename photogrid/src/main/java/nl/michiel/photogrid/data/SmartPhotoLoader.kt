package nl.michiel.photogrid.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext
import nl.michiel.photogrid.R
import timber.log.Timber
import java.net.URL
import java.util.LinkedList
import java.util.Queue

class SmartPhotoLoader(
    private val photoLoader: PhotoLoader,
    private val cache: PhotoCache,
    private val scope: CoroutineScope,
    private val apiCallLimit: Int = 4,
    var strategy: LoadingStrategy = Dumb()
) {
    private val prefetchQueue = LinkedList<FetchJob>()
    private val currentJobs = mutableSetOf<FetchJob>()
    private val counterContext = newSingleThreadContext("SmartPhotoLoader")

    fun load(url: String, imageView: ImageView) {
        MainScope().launch {
            imageView.setTag(R.id.url, url)
            val bitmap = getAsync(url)
            if (imageView.getTag(R.id.url) == url) {
                imageView.setImageBitmap(bitmap)
            } else {
                Timber.d("ignored out-of-date result")
            }
        }
    }

    private suspend fun getAsync(url: String): Bitmap =
        withContext(counterContext) {
            Timber.d("getAsync $url")
            val result = cache.get(url)
                ?: currentJobs.firstOrNull { it.url == url } ?.let { it.result }
                ?: prefetchQueue.firstOrNull { it.url == url } ?.let { it.result }
                ?: fetchAsync(url)
            result.await()
                .also { propagate() }
        }

    private suspend fun fetchAsync(url: String): Deferred<Bitmap> {
        Timber.d("fetchAsync $url")
        val job = FetchJob(url) {
            Timber.d("start fetch $url")
            photoLoader.get(url).also {
                Timber.d("done fetch $url")
            }
        }
        prefetchQueue.addLast(job)
        propagate()
        return job.result
    }

    @ExperimentalCoroutinesApi
    private fun propagate() {
        Timber.d("propagate start")
        currentJobs
            .filter { it.result.isCompleted }
            .forEach { fetchJob ->
                Timber.d("  job done")
                cache.put(fetchJob.url, fetchJob.result.getCompleted())
                currentJobs.remove(fetchJob)
            }

        while (currentJobs.size < apiCallLimit && prefetchQueue.isNotEmpty()) {
            Timber.d("  adding new job")
            currentJobs.add(prefetchQueue.pop().apply { start(scope) })
        }
        Timber.d("propagate done")
    }

    data class FetchJob(val url: String, val job: suspend () -> Bitmap) {
        val result = CompletableDeferred<Bitmap>()

        fun start(scope: CoroutineScope) {
            Timber.d(" ** starting job $url")
            scope.launch {
                result.complete(job())
                Timber.d(" ** job done  $url")
            }
        }
    }
}