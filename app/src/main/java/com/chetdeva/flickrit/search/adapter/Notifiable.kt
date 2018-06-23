package com.chetdeva.flickrit.search.adapter

/**
 * @author chetansachdeva
 */
interface Notifiable<T> {
    fun addAllNotify(list: List<T>)
    fun addNotify(item: T)
    fun removeNotify(position: Int)
    fun clearNotify()
}