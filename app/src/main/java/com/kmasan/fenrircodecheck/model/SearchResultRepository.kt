package com.kmasan.fenrircodecheck.model

import android.util.Log
import org.json.JSONObject

class SearchResultRepository(
    private val api: GourmetSearchAPI
) {
    fun searchShop(parameter: GourmetSearchParameter, count: Int): JsonArrayResult{
        return when(val result = api.search(parameter, count=count)){
            is APIResult.Success<String> ->{
                val jsonObj = JSONObject(result.data)
                val results = jsonObj.getJSONObject("results")
                val shop = results.getJSONArray("shop")
                Log.d(this.javaClass.name, "searchShop: $shop")
                JsonArrayResult(true, jsonArray = shop)
            }
            is APIResult.Error->{
                JsonArrayResult(false)
            }
        }
    }

    fun addSearchShop(parameter: GourmetSearchParameter, page: Int, count: Int): JsonArrayResult{
        return when(val result = api.search(parameter, page = page, count=count)){
            is APIResult.Success<String> ->{
                val jsonObj = JSONObject(result.data)
                val results = jsonObj.getJSONObject("results")
                val shop = results.getJSONArray("shop")
                Log.d(this.javaClass.name, "searchShop: $shop")
                JsonArrayResult(true, jsonArray = shop)
            }
            is APIResult.Error->{
                JsonArrayResult(false)
            }
        }
    }
}