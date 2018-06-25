package com.chetdeva.flickrit.util.image

/**
 * @author chetansachdeva
 */

interface UrlFactory<E> {
    fun createUrl(entity: E) : String?
}