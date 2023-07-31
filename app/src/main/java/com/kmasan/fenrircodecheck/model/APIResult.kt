package com.kmasan.fenrircodecheck.model

sealed class APIResult<out T : Any> {

    data class Success<out T : Any>(val data: T) : APIResult<T>()
    data class Error(val exception: String) : APIResult<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
        }
    }
}
