package com.chetdeva.flickrit.util.image

/**
 * @author chetansachdeva
 */

interface UrlFactory<E> {
    fun create(entity: E) : String?
}