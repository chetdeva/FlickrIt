package com.chetdeva.flickrit.util.extension

import android.view.View

/**
 * @author chetansachdeva
 */

val View.isVisible: Boolean
    get() = visibility == View.VISIBLE

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}
