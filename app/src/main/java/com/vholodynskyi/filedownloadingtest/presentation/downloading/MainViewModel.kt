package com.vholodynskyi.filedownloadingtest.presentation.downloading

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vholodynskyi.filedownloadingtest.domain.use_case.DownloadFileUseCase
import com.vholodynskyi.filedownloadingtest.common.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val downloadFileUseCase: DownloadFileUseCase
) : ViewModel() {

    var percentage = MutableStateFlow(0.0)

    private val _state = mutableStateOf(DownloadingState())
    val state: State<DownloadingState> = _state

    private val fileUrl = "https://speed.hetzner.de/100MB.bin" // DON'T FORGET TO SET YOUR URL

    private val testJob: Job

    init {
        testJob = downloadFiles()
    }

    private fun downloadFiles(): Job {
        percentage = downloadFileUseCase.percentage

        return downloadFileUseCase(fileUrl)
            .onEach { result ->
                _state.value = when (result) {
                    is Resource.Error -> DownloadingState(error = result.message ?: "")
                    is Resource.Loading -> DownloadingState(isLoading = true)
                    is Resource.Success -> DownloadingState(file = result.data ?: "")
                }
            }.onCompletion {
                if (it is CancellationException) {
                    _state.value = DownloadingState(error = "Downloading is cancelled")
                }
            }.launchIn(viewModelScope)
    }

    fun cancelDownloading() {
        if (testJob.isActive) {
            testJob.cancel()
        }
    }
}