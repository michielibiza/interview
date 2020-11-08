package nl.michiel.photogrid.data

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_SETTLING
import timber.log.Timber
import kotlin.math.min
import kotlin.math.max

interface PreLoadStrategy {
    fun onIdle(smartImageLoader: SmartImageLoader)
}

class None : PreLoadStrategy {
    override fun onIdle(smartImageLoader: SmartImageLoader) = Unit
}

class PrefetchForRecyclerView(
    private val manager: LinearLayoutManager,
    recyclerView: RecyclerView,
    private val data: List<Photo>,
    private val preFetchAmount: Int = 15
) : PreLoadStrategy {

    private var lastStart = 0
    private var lastEnd = 0
    private var isFlinging = false

    init {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                isFlinging = newState == SCROLL_STATE_SETTLING
            }
        })
    }

    override fun onIdle(smartImageLoader: SmartImageLoader) {
        // don't preload during fling: it will not help and clog the download queue,
        // making loading slower when the view settles
        if (isFlinging) return

        val start = manager.findFirstVisibleItemPosition()
        val end = manager.findLastVisibleItemPosition()
        // we only pre-load once for a visible area
        if (start != lastStart || end != lastEnd) {
            Timber.i("onIdle visible = ($start, $end)")
            // we preload both before and after the visible area
            // the smartImageLoader makes sure that cached (or scheduled) images are not downloaded again
            smartImageLoader.prefetch(urlRange(start - preFetchAmount, start))
            smartImageLoader.prefetch(urlRange(end, end + preFetchAmount))
            lastStart = start
            lastEnd = end
        }
    }

    private fun urlRange(start: Int, end: Int): List<String> =
        IntRange(max(start, 0), min(end, data.size))
            .map { data[it].url }

}