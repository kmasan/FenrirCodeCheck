package com.kmasan.fenrircodecheck.ui.searchResult

import android.content.Context
import androidx.compose.runtime.mutableStateOf
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
import okhttp3.OkHttpClient
import java.util.stream.IntStream

class SearchResultViewModel(private val repository: SearchResultRepository) : ViewModel() {
    // 検索結果
    private val _shopList = MutableLiveData<List<ShopData>>()
    val shopList: LiveData<List<ShopData>> = _shopList

    // 表示する店詳細の情報
    private val _selectShopData = MutableLiveData<ShopData>()
    val selectShopData: LiveData<ShopData> = _selectShopData

    // 検索条件
    lateinit var apiParameter: GourmetSearchParameter
    fun setSelectShopData(data: ShopData) = _selectShopData.postValue(data)

    // 詳細画面を表示しているか
    val expandDetail = mutableStateOf(false)

    // 現在の検索結果のページ数
    private var page = 1

    // 条件から検索
    fun searchShop(parameter: GourmetSearchParameter){
        viewModelScope.launch(Dispatchers.IO){
            val result = repository.searchShop(parameter, LIST_COUNT)
            val json = result.jsonArray ?: return@launch
            val shopList = mutableListOf<ShopData>()

            // 情報を整理して検索結果に保存
            for (i in IntStream.range(0, json.length())){
                val jsonObj = json.getJSONObject(i)
                val shopName = jsonObj.getString("name")
                val url = jsonObj.getJSONObject("urls").getString("pc")
                val thumbnail = jsonObj.getString("logo_image")
                val shopTop = jsonObj
                    .getJSONObject("photo")
                    .getJSONObject("mobile")
                    .getString("l")
                val genre = jsonObj.getJSONObject("genre").getString("name")
                val access = jsonObj.getString("mobile_access")
                val address = jsonObj.getString("address")
                val open = jsonObj.getString("open")
                shopList.add(
                    ShopData(
                        shopName, url, thumbnail, shopTop, genre, access, address, open
                    )
                )
            }
            _shopList.postValue(shopList)
        }
    }

    // 追加の検索結果の取得
    private var addSearchShopRunning = false
    fun addSearchShop(parameter: GourmetSearchParameter){
        // 検索中の場合は終了
        if(addSearchShopRunning) return

        addSearchShopRunning = true
        viewModelScope.launch(Dispatchers.IO){
            page++
            val result = repository.addSearchShop(parameter, page, LIST_COUNT)
            val json = result.jsonArray ?: return@launch
            val shopList = mutableListOf<ShopData>()

            // 現状の検索結果を追加
            _shopList.value?.let { shopList.addAll(it) }

            // 追加の検索結果を追加
            for (i in IntStream.range(0, json.length())){
                val jsonObj = json.getJSONObject(i)
                val shopName = jsonObj.getString("name")
                val url = jsonObj.getJSONObject("urls").getString("pc")
                val thumbnail = jsonObj.getString("logo_image")
                val shopTop = jsonObj
                    .getJSONObject("photo")
                    .getJSONObject("mobile")
                    .getString("l")
                val genreObj = jsonObj.getJSONObject("genre")
                val genre = "${genreObj.getString("name")}: ${genreObj.getString("catch")}"
                val access = jsonObj.getString("mobile_access")
                val address = jsonObj.getString("address")
                val open = jsonObj.getString("open")
                shopList.add(
                    ShopData(
                        shopName, url, thumbnail, shopTop, genre, access, address, open
                    )
                )
            }
            _shopList.postValue(shopList)
            addSearchShopRunning = false
        }
    }

    companion object{
        // １ページ分の検索結果数
        const val LIST_COUNT = 100

        fun factory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>
            ): T {
                return SearchResultViewModel(
                    SearchResultRepository(GourmetSearchAPI(OkHttpClient(), context.getString(R.string.api_token)))
                ) as T
            }
        }
    }
}