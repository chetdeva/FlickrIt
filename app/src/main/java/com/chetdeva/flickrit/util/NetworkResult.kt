package com.chetdeva.flickrit.util

sealed class NetworkResult<T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error<T>(val error: String) : NetworkResult<T>()
}