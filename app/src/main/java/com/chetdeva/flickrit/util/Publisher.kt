package com.chetdeva.flickrit.util

/**
 * @author chetansachdeva
 */

interface Publisher<T> {
    fun publish(model: T)
}