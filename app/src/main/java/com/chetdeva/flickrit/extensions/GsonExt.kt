package com.chetdeva.flickrit.extensions

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * @author chetansachdeva
 */

inline fun <reified T> Gson.fromJson(json: String): T {
    return fromJson<T>(json, object : TypeToken<T>() {}.type)
}