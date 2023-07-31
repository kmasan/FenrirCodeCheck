package com.kmasan.fenrircodecheck.ui.searchResult

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kmasan.fenrircodecheck.model.GourmetSearchParameter
import com.kmasan.fenrircodecheck.model.RestaurantData
import com.kmasan.fenrircodecheck.model.SearchResultRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.stream.IntStream

class SearchResultViewModel(private val repository: SearchResultRepository) : ViewModel() {
    private val _shopList = MutableLiveData<List<RestaurantData>>()
    val shopList: LiveData<List<RestaurantData>> = _shopList

    fun searchShop(parameter: GourmetSearchParameter){
        viewModelScope.launch(Dispatchers.IO){
            val result = repository.searchShop(parameter)
            val json = result.jsonArray ?: return@launch
            val shopList = mutableListOf<RestaurantData>()
            for (i in IntStream.range(0, json.length())){
                val jsonObj = json.getJSONObject(i)
                val shopName = jsonObj.getString("name")
                val thumbnail = jsonObj.getString("logo_image")
                val access = jsonObj.getString("access")
                shopList.add(
                    RestaurantData(
                        shopName, thumbnail, access
                    )
                )
            }
            _shopList.postValue(shopList)
        }
    }
}