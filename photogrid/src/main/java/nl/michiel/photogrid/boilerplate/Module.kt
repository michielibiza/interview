package nl.michiel.photogrid.boilerplate

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import nl.michiel.photogrid.data.MemoryCache
import nl.michiel.photogrid.data.PhotoCache
import nl.michiel.photogrid.data.PhotoLoader
import nl.michiel.photogrid.data.PhotoRepository
import nl.michiel.photogrid.data.SmartPhotoLoader
import nl.michiel.photogrid.data.createService
import nl.michiel.photogrid.ui.photogrid.PhotoGridViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single { PhotoRepository(get()) }
    single { createService() }
    single { PhotoLoader(get()) }
    single { SmartPhotoLoader(get(), get(), get()) }

    factory { MemoryCache(50) as PhotoCache }
    factory { CoroutineScope(Dispatchers.Default) }

    viewModel { PhotoGridViewModel(get()) }
}