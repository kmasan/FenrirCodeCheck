package com.kmasan.fenrircodecheck.model

import org.json.JSONArray

data class JsonArrayResult (
    val success: Boolean,
    val jsonArray: JSONArray? = null,
    val error: String? = null
        )