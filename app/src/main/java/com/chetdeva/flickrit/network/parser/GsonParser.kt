package com.chetdeva.flickrit.network.parser

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * @author chetansachdeva
 */

class GsonParser(val gson: Gson) {

    fun <T> parse(json: String): T {
        val type = object : TypeToken<T>() {}.type
        return gson.fromJson<T>(json, type)
    }
}