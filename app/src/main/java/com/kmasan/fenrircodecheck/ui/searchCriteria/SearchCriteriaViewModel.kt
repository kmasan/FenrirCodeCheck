package com.kmasan.fenrircodecheck.ui.searchCriteria

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kmasan.fenrircodecheck.model.GPSLogger
import com.kmasan.fenrircodecheck.model.GourmetSearchParameter
import com.kmasan.fenrircodecheck.model.SearchCriteriaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchCriteriaViewModel(private val repository: SearchCriteriaRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(false)
    val uiState: StateFlow<Boolean> = _uiState.asStateFlow()

    private val _parameter = MutableLiveData<GourmetSearchParameter>()
    val parameter: LiveData<GourmetSearchParameter> = _parameter

    fun resultFragmentExpand(boolean: Boolean) {
        _uiState.update { boolean }
    }

    fun searchShop(range: Int){
        viewModelScope.launch(Dispatchers.IO){
            val location = repository.getLocation() ?: return@launch
            Log.d(this@SearchCriteriaViewModel.javaClass.name, "$location")

            _parameter.postValue(
                GourmetSearchParameter(
//                    35.171126,
//                    136.909612,
                    location.latitude,
                    location.longitude,
                    range
                ))
        }
    }

    fun startGPSLogger() = repository.startGPS()

    fun stopGPSLogger() = repository.stopGPS()

    fun setLastLocation() =
        viewModelScope.launch(Dispatchers.IO){
            repository.setLastLocation()
        }

    companion object{
        fun factory(activity: Activity): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>
            ): T {
                return SearchCriteriaViewModel(
                    SearchCriteriaRepository(GPSLogger(activity))
                ) as T
            }
        }
    }
}