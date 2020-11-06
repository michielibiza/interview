package nl.michiel.photogrid.data

class PhotoRepository(private val service: RandomUserService) {
    suspend fun loadPhotoList(): List<Photo> =
            service.loadUserList().results.map { Photo(it.picture.large) }
}

