package nl.michiel.photogrid.ui.photogrid

import androidx.lifecycle.ViewModelProvider
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

class PhotoGridFragment : Fragment() {

   private val photoAdapter = GroupAdapter<GroupieViewHolder>()

    private lateinit var viewModel: PhotoGridViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.photo_grid_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        photoGrid.layoutManager = GridLayoutManager(context, 3)
        val gridMargin = resources.getDimensionPixelOffset(R.dimen.gridSpacing)
        photoGrid.addItemDecoration(GridInnerMarginDecoration(gridMargin))
        photoGrid.adapter = photoAdapter
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PhotoGridViewModel::class.java)
        viewModel.photos.observe(viewLifecycleOwner) { photoList ->
            photoAdapter.clear()
            photoAdapter.addAll(photoList.map { PhotoItem(it) })

        }
    }


}