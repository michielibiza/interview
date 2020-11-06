package nl.michiel.photogrid.ui.photogrid

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nl.michiel.photogrid.data.Photo
import nl.michiel.photogrid.data.PhotoRepository
import timber.log.Timber

class PhotoGridViewModel(
    private val repository: PhotoRepository
) : ViewModel() {
    private val _photos: MutableLiveData<List<Photo>> = MutableLiveData()
    val photos: LiveData<List<Photo>> = _photos

    init {
        viewModelScope.launch {
            _photos.value = repository.loadPhotoList()
        }
    }
}