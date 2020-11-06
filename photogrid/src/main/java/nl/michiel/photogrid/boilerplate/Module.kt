package nl.michiel.photogrid.boilerplate

import nl.michiel.photogrid.data.PhotoLoader
import nl.michiel.photogrid.data.PhotoRepository
import nl.michiel.photogrid.data.RandomUserService
import nl.michiel.photogrid.data.createService
import nl.michiel.photogrid.ui.photogrid.PhotoGridViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single { PhotoRepository(get()) }
    single { createService() }
    single { PhotoLoader(get()) }

    viewModel { PhotoGridViewModel(get()) }
}