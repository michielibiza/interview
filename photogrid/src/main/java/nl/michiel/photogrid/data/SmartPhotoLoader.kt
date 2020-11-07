package nl.michiel.photogrid.data

import android.graphics.Bitmap
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
import java.util.LinkedList

class SmartPhotoLoader(
    private val photoLoader: PhotoLoader,
    private val cache: PhotoCache,
    private val scope: CoroutineScope,
    private val apiCallLimit: Int = 4,
    var strategy: LoadingStrategy = Dumb()
) {

    private val prefetchQueue = LinkedList<FetchJob>()
    private val currentJobs = mutableSetOf<FetchJob>()
    private val loaderContext = newSingleThreadContext("SmartPhotoLoader")

    fun load(url: String, imageView: ImageView) {
        MainScope().launch {
            imageView.setTag(R.id.url, url)
            val bitmap = getAsync(url)
            if (imageView.getTag(R.id.url) == url) {
                imageView.setImageBitmap(bitmap)
            }
        }
    }

    private suspend fun getAsync(url: String): Bitmap =
        withContext(loaderContext) {
            Timber.d("getAsync $url")
            val result = cache.get(url)?.let {
                Timber.v("  from cache")
                CompletableDeferred(it)
            }
                ?: currentJobs.firstOrNull { it.url == url } ?.let {
                    Timber.v("  in current job")
                    it.result
                }
                ?: prefetchQueue.firstOrNull { it.url == url } ?.let {
                    Timber.v("  in queue")
                    it.result
                }
                ?: fetchAsync(url)
            result.await()
        }

    private suspend fun fetchAsync(url: String): Deferred<Bitmap> {
        Timber.i("fetchAsync $url")
        val job = FetchJob(url) {
            withContext(loaderContext) {
                Timber.v("start fetch $url")
                photoLoader.get(url).also {
                    Timber.v("done fetch $url")
                    propagate()
                }
            }
        }
        prefetchQueue.addLast(job)
        propagate()
        return job.result
    }

    @ExperimentalCoroutinesApi
    private fun propagate() {
        if (currentJobs.size > 0 || prefetchQueue.size > 0) {
            Timber.d("propagate start jobs=${currentJobs.size} queue=${prefetchQueue.size}")
        }
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

        if (prefetchQueue.isEmpty()) {
            scope.launch(Dispatchers.Main) {
                strategy.onIdle(this@SmartPhotoLoader)
            }
        }

        Timber.v("propagate done")
    }

    fun prefetch(urls: List<String>) {
        scope.launch {
            urls.forEach { async { getAsync(it) } }
        }
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