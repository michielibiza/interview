package nl.michiel.photogrid.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nl.michiel.photogrid.domain.Photo
import nl.michiel.photogrid.domain.PhotoRepository

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
