package nl.michiel.photogrid.boilerplate

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import nl.michiel.photogrid.data.MemoryCache
import nl.michiel.photogrid.data.ImageCache
import nl.michiel.photogrid.data.ImageLoader
import nl.michiel.photogrid.data.PhotoRepository
import nl.michiel.photogrid.data.SmartImageLoader
import nl.michiel.photogrid.data.createService
import nl.michiel.photogrid.ui.photogrid.PhotoGridViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single { PhotoRepository(get()) }
    single { createService() }
    single { ImageLoader(get()) }
    single { SmartImageLoader(get(), get(), get()) }

    factory { MemoryCache(90) as ImageCache }
    factory { CoroutineScope(Dispatchers.Default) }

    viewModel { PhotoGridViewModel(get()) }
}