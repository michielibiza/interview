package nl.michiel.photogrid.data

import androidx.recyclerview.widget.LinearLayoutManager
import timber.log.Timber
import kotlin.math.min
import kotlin.math.max

interface LoadingStrategy {
    fun onIdle(smartPhotoLoader: SmartPhotoLoader)
}

class Dumb : LoadingStrategy {
    override fun onIdle(smartPhotoLoader: SmartPhotoLoader) = Unit
}

class PrefetchForRecyclerView(
    private val manager: LinearLayoutManager,
    private val data: List<Photo>,
    private val preFetchAmount: Int = 15
) : LoadingStrategy {

    var lastStart = 0
    var lastEnd = 0

    override fun onIdle(smartPhotoLoader: SmartPhotoLoader) {
        val start = manager.findFirstVisibleItemPosition()
        val end = manager.findLastVisibleItemPosition()
        if (start != lastStart || end != lastEnd) {
            Timber.i("onIdle visible = ($start, $end)")
            smartPhotoLoader.prefetch(urlRange(start - preFetchAmount, start))
            smartPhotoLoader.prefetch(urlRange(end, end + preFetchAmount))
            lastStart = start
            lastEnd = end
        }
    }

    private fun urlRange(start: Int, end: Int): List<String> =
        IntRange(max(start, 0), min(end, data.size))
            .map { data[it].url }

}