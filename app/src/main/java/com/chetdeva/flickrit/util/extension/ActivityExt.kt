package com.chetdeva.flickrit.util.extension

import android.app.Activity
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast


/**
 * @author chetansachdeva
 */

fun Activity.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Activity.showToast(@StringRes messageResId: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, messageResId, duration).show()
}

fun Activity.hideKeyboard() {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
}

fun Activity.showKeyboard() {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
}

inline fun View.showSnackbar(message: String, length: Int = Snackbar.LENGTH_SHORT) {
    val snack = Snackbar.make(this, message, length)
    snack.show()
}