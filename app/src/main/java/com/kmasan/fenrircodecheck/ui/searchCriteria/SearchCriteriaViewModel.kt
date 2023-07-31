package com.kmasan.fenrircodecheck.ui.searchCriteria

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kmasan.fenrircodecheck.model.GourmetSearchParameter
import com.kmasan.fenrircodecheck.model.SearchCriteriaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchCriteriaViewModel(private val repository: SearchCriteriaRepository) : ViewModel() {
    private val _parameter = MutableLiveData<GourmetSearchParameter>()
    val parameter: LiveData<GourmetSearchParameter> = _parameter

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
}