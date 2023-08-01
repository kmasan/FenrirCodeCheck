package com.kmasan.fenrircodecheck.ui.searchResult

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kmasan.fenrircodecheck.R
import com.kmasan.fenrircodecheck.model.GourmetSearchAPI
import com.kmasan.fenrircodecheck.model.GourmetSearchParameter
import com.kmasan.fenrircodecheck.model.ShopData
import com.kmasan.fenrircodecheck.model.SearchResultRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.stream.IntStream

class SearchResultViewModel(private val repository: SearchResultRepository) : ViewModel() {
    private val _shopList = MutableLiveData<List<ShopData>>()
    val shopList: LiveData<List<ShopData>> = _shopList

    private val _selectShopData = MutableLiveData<ShopData>()
    val selectShopData: LiveData<ShopData> = _selectShopData

    private var page = 1

    fun searchShop(parameter: GourmetSearchParameter){
        viewModelScope.launch(Dispatchers.IO){
            val result = repository.searchShop(parameter, LIST_COUNT)
            val json = result.jsonArray ?: return@launch
            val shopList = mutableListOf<ShopData>()
            for (i in IntStream.range(0, json.length())){
                val jsonObj = json.getJSONObject(i)
                val shopName = jsonObj.getString("name")
                val thumbnail = jsonObj.getString("logo_image")
                val shopTop = jsonObj
                    .getJSONObject("photo")
                    .getJSONObject("mobile")
                    .getString("l")
                val access = jsonObj.getString("mobile_access")
                val address = jsonObj.getString("address")
                val open = jsonObj.getString("open")
                shopList.add(
                    ShopData(
                        shopName, thumbnail, shopTop, access, address, open
                    )
                )
            }
            _shopList.postValue(shopList)
        }
    }

    fun addSearchShop(parameter: GourmetSearchParameter){
        viewModelScope.launch(Dispatchers.IO){
            page++
            val result = repository.addSearchShop(parameter, page, LIST_COUNT)
            val json = result.jsonArray ?: return@launch
            val shopList = mutableListOf<ShopData>()
            _shopList.value?.let { shopList.addAll(it) }
            for (i in IntStream.range(0, json.length())){
                val jsonObj = json.getJSONObject(i)
                val shopName = jsonObj.getString("name")
                val thumbnail = jsonObj.getString("logo_image")
                val shopTop = jsonObj
                    .getJSONObject("photo")
                    .getJSONObject("mobile")
                    .getString("l")
                val access = jsonObj.getString("mobile_access")
                val address = jsonObj.getString("address")
                val open = jsonObj.getString("open")
                shopList.add(
                    ShopData(
                        shopName, thumbnail, shopTop, access, address, open
                    )
                )
            }
            _shopList.postValue(shopList)
        }
    }

    fun setSelectShopData(data: ShopData) = _selectShopData.postValue(data)

    companion object{
        const val LIST_COUNT = 100
        fun factory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>
            ): T {
                return SearchResultViewModel(
                    SearchResultRepository(GourmetSearchAPI(context.getString(R.string.api_token)))
                ) as T
            }
        }
    }
}