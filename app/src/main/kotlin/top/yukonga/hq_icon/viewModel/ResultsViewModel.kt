package top.yukonga.hq_icon.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import top.yukonga.hq_icon.data.Response

class ResultsViewModel : ViewModel() {
    private val _results = MutableStateFlow<List<Response.Result>>(emptyList())
    val results: StateFlow<List<Response.Result>> = _results

    private val _corner = MutableStateFlow("")
    val corner: StateFlow<String> = _corner

    private val _resolution = MutableStateFlow("")
    val resolution: StateFlow<String> = _resolution

    fun updateResults(newResults: List<Response.Result>) {
        viewModelScope.launch {
            _results.value = newResults
        }
    }

    fun updateCorner(newCorner: String) {
        viewModelScope.launch {
            _corner.value = newCorner
        }
    }

    fun updateResolution(newResolution: String) {
        viewModelScope.launch {
            _resolution.value = newResolution
        }
    }
}