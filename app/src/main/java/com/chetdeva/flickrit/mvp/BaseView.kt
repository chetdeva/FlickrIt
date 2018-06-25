package com.chetdeva.flickrit.mvp

interface BaseView<T> {
    var isActive: Boolean
    var presenter: T
}
