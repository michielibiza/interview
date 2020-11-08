package nl.michiel.photogrid.data

import android.graphics.Bitmap
import android.view.View
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

/**
 * An image loading 'library'
 *
 * An ImageView can use function [load] to efficiently display an image from url.
 * This class takes care of multi-threading, caching and limiting concurrent API calls.
 * Pre-loading can be implemented by setting the [preloadStrategy]
 * (maybe that type should just be called OnIdleListener... 🤷‍)♂️
 *
 */
class SmartPhotoLoader(
    private val photoLoader: PhotoLoader,
    private val cache: PhotoCache,
    private val scope: CoroutineScope,
    private val apiCallLimit: Int = 6,
    var preloadStrategy: PreLoadStrategy = None()
) {

    private val prefetchQueue = LinkedList<FetchJob>()
    private val currentJobs = mutableSetOf<FetchJob>()

    /**
     * Loading from the network happens on [Dispatch.IO], but changing the cache and job queue
     * needs to all happen in the same thread. This context is used for all "orchestration" the needs
     * to happen on the same thread (Fine-grained locking would also have been possible,
     * but it would be less readable and shouldn't give performance improvements since the loading
     * already happens on a different thread) */
    private val loaderContext = newSingleThreadContext("SmartPhotoLoader")

    /**
     * The image at [url] is returned either from cache or the network
     *
     * Uses [View.setTag] with key R.id.url for proper functioning.
     */
    fun load(url: String, imageView: ImageView) {
        // need to launch coroutine, it must be on the main thread because we'll change the UI
        MainScope().launch {
            imageView.setTag(R.id.url, url)
            val bitmap = getAsync(url)
            // before setting the image we check that this view still has the same uel stored in tag
            // when scrolling fast it could be that the image view was recycled before the image was loaded
            if (imageView.getTag(R.id.url) == url) {
                imageView.setImageBitmap(bitmap)
            }
        }
    }

    private suspend fun getAsync(url: String): Bitmap = withContext(loaderContext) {
        val result = cache.get(url)?.let { CompletableDeferred(it) }
            ?: currentJobs.firstOrNull { it.url == url }?.let { it.result }
            ?: prefetchQueue.firstOrNull { it.url == url }?.let { it.result }
            ?: fetchAsync(url)
        result.await()
    }

    /**
     * If a requested image is not in cache or in scheduled jobs, we need to schedule a new job and
     * put it in the queue. After the job is added we call [propagate] so a job is started when possible
     * When a job is finished we also call [propagate] to put the result in cache and start a new job
     */
    private suspend fun fetchAsync(url: String): Deferred<Bitmap> {
        val job = FetchJob(url) {
            withContext(loaderContext) {
                photoLoader.get(url).also { propagate() }
            }
        }
        prefetchQueue.addLast(job)
        propagate()
        return job.result
    }

    /**
     * this function implements the actual loading, it removes finished jobs, stored results in cache,
     * and starts new ones if available
     *
     * If the load queue is empty the PreLoadStrategy is notified so it can take action
     */
    @ExperimentalCoroutinesApi
    private fun propagate() {
        currentJobs.filter { it.result.isCompleted }.forEach { fetchJob ->
                cache.put(fetchJob.url, fetchJob.result.getCompleted())
                currentJobs.remove(fetchJob)
            }

        while (currentJobs.size < apiCallLimit && prefetchQueue.isNotEmpty()) {
            currentJobs.add(prefetchQueue.pop().apply { start(scope) })
        }

        if (prefetchQueue.isEmpty()) {
            scope.launch(Dispatchers.Main) {
                preloadStrategy.onIdle(this@SmartPhotoLoader)
            }
        }
    }

    /**
     * starts pre fetching images for [urls]
     */
    fun prefetch(urls: List<String>) {
        scope.launch {
            // we can just use getAsync, it will not start jobs if the url is cached of is already being loaded
            urls.forEach { async { getAsync(it) } }
        }
    }

    /**
     * A class to keep track of loading jobs. We need a Deferred<Bitmap> [result] *without* starting the download immediately
     */
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