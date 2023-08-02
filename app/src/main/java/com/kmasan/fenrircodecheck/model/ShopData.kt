package com.kmasan.fenrircodecheck.model

// 店情報
data class ShopData(
    val name: String,
    val url: String,
    val thumbnailURL: String,
    val shopTopPhoto: String,
    val genre: String,
    val access: String,
    val address: String,
    val open: String
)
