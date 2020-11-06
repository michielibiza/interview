package nl.michiel.photogrid.ui.photogrid

import android.util.Range
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import nl.michiel.photogrid.data.Photo

class PhotoGridViewModel : ViewModel() {
    private val _photos: MutableLiveData<List<Photo>> = MutableLiveData()
    val photos: LiveData<List<Photo>> = _photos

    init {
        _photos.value = (0 until 100).map { i ->
            Photo("blabla$i")
        }
    }
}