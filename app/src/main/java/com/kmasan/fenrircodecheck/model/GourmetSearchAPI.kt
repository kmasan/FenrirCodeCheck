package com.kmasan.fenrircodecheck.model

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

// グルメサーチAPI
class GourmetSearchAPI(private val client: OkHttpClient, private val apiToken: String) {
    companion object{
        // APIのベースURL
        const val apiBaseURL = "http://webservice.recruit.co.jp/hotpepper/gourmet/v1/"
    }

    // 検索条件からAPIを実行
    fun search(searchParameter: GourmetSearchParameter, page: Int = 1, count: Int = 10): APIResult<String>{
        // 検索タグの整理
        val lat = searchParameter.lat
        val lng = searchParameter.lng
        val range = searchParameter.range
        // リクエストの作成
        val request = Request.Builder()
            .url("$apiBaseURL/?key=$apiToken&format=json&lat=$lat&lng=$lng&range=$range&start=${1+count*(page-1)}&count=$count")
            .build()

        // リクエストを実行
        try {
            val result = client.newCall(request)
            result.execute().let {
                val resString = it.body!!.string()
                Log.d(this.javaClass.name, "success: $resString")
                val json = JSONObject(resString)
                if(json.has("error")){
                    val errorMessage = json.getJSONObject("error").getString("message")
                    return APIResult.Error(errorMessage)
                }

                return APIResult.Success(resString)
            }
        }catch (e: IOException){
            Log.d(this.javaClass.name, "error: $e")
            return APIResult.Error(e.toString())
        }
    }
}