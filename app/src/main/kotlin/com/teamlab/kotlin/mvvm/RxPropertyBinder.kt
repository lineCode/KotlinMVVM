package com.teamlab.kotlin.mvvm

import android.widget.EditText
import com.jakewharton.rxbinding.widget.textChanges
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers

fun <T> Observable<T>.observeOnMainThread() = observeOn(AndroidSchedulers.mainThread())

fun <V, T> V.bindRxProperty(property: ReadOnlyRxProperty<T>, setter: V.(T) -> Unit): Subscription {
    val subscription = property.publishSubject
            .observeOnMainThread()
            .subscribe { setter(it) }
    setter(property.value)
    return subscription
}

fun <V, T> V.bindRxProperty(property: ReadOnlyRxProperty<T>, initializer: V.(T) -> Unit, onNext: V.(T) -> Unit): Subscription {
    val subscription = property.publishSubject
            .observeOnMainThread()
            .subscribe { onNext(it) }
    initializer(property.value)
    return subscription
}

fun RxProperty<CharSequence>.bindEditText(editText: EditText): Subscription {
    editText.setText(value)
    return editText.textChanges().subscribe { value = it }
}
