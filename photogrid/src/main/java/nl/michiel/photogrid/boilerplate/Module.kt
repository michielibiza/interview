package nl.michiel.photogrid.boilerplate

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import nl.michiel.photogrid.domain.MemoryCache
import nl.michiel.photogrid.domain.ImageCache
import nl.michiel.photogrid.domain.ImageLoader
import nl.michiel.photogrid.domain.PhotoRepository
import nl.michiel.photogrid.domain.SmartImageLoader
import nl.michiel.photogrid.domain.createService
import nl.michiel.photogrid.viewmodel.PhotoGridViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single { PhotoRepository(get()) }
    single { createService() }
    single { ImageLoader(get()) }
    single { SmartImageLoader(get(), get(), get()) }

    factory<ImageCache> { MemoryCache(90) }
    factory { CoroutineScope(Dispatchers.Default) }

    viewModel { PhotoGridViewModel(get()) }
}
