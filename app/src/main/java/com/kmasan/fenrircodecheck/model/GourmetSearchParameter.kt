package com.kmasan.fenrircodecheck.model

// 検索条件
data class GourmetSearchParameter (
    val lat: Double,
    val lng: Double,
    val range: Int
    )