package com.kmasan.fenrircodecheck.ui.searchCriteria

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kmasan.fenrircodecheck.model.GPSLogger
import com.kmasan.fenrircodecheck.model.GourmetSearchParameter
import com.kmasan.fenrircodecheck.model.SearchCriteriaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchCriteriaViewModel(private val repository: SearchCriteriaRepository) : ViewModel() {
    // 検索条件
    private val _parameter = MutableLiveData<GourmetSearchParameter>()
    val parameter: LiveData<GourmetSearchParameter> = _parameter

    // 検索結果画面を表示させるか
    val displayFragment = mutableStateOf(false)
    // 現在の検索範囲の設定値
    val selectRange = mutableStateOf("1000m")

    // 現在地を取得して検索条件をまとめる
    fun searchShop(range: Int){
        viewModelScope.launch(Dispatchers.IO){
            val location = repository.getLocation() ?: return@launch
            Log.d(this@SearchCriteriaViewModel.javaClass.name, "$location")

            _parameter.postValue(
                GourmetSearchParameter(
                    location.latitude,
                    location.longitude,
                    range
                ))
        }
    }

    // GPS取得の開始
    fun startGPSLogger() =
        viewModelScope.launch{
            repository.startGPS()
        }

    // GPS取得の停止
    fun stopGPSLogger() = repository.stopGPS()

    // 最後に取得された現在地をセット
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