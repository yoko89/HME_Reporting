package com.neklaway.hme_reporting.utils

sealed class ResourceWithString<T>(val data: T?, val string: String?, val message: String? = null) {
    class Success<T>(data: T, string: String?) : ResourceWithString<T>(data, string)
    class Loading<T>(data: T? = null, string: String? = null) : ResourceWithString<T>(data, string)
    class Error<T>(data: T? = null, message: String, string: String? = null) :
        ResourceWithString<T>(data, string, message)

}
