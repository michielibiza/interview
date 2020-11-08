package nl.michiel.photogrid.ui.photogrid

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.photo_grid_fragment.photoGrid
import nl.michiel.photogrid.R
import nl.michiel.photogrid.data.PrefetchForRecyclerView
import nl.michiel.photogrid.data.SmartPhotoLoader
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class PhotoGridFragment : Fragment() {

    private val viewModel: PhotoGridViewModel by viewModel()
    private val photoLoader: SmartPhotoLoader by inject()
    private lateinit var gridLayoutManager: GridLayoutManager

    private val photoAdapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.photo_grid_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gridLayoutManager = GridLayoutManager(context, 3)
        photoGrid.layoutManager = gridLayoutManager
        val gridMargin = resources.getDimensionPixelOffset(R.dimen.gridSpacing)
        photoGrid.addItemDecoration(GridInnerMarginDecoration(gridMargin))
        photoGrid.adapter = photoAdapter
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.photos.observe(viewLifecycleOwner) { photoList ->
            photoAdapter.clear()
            photoLoader.preloadStrategy = PrefetchForRecyclerView(gridLayoutManager, photoGrid, photoList)
            photoAdapter.addAll(photoList.map { PhotoItem(it, photoLoader) })
        }
    }

}