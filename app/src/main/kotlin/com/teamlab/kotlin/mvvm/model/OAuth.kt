package com.teamlab.kotlin.mvvm.model

import android.os.Bundle
import com.teamlab.kotlin.mvvm.ext.getOAuthAccessTokenObservable
import com.teamlab.kotlin.mvvm.ext.getOAuthRequestTokenObservable
import rx.mvvm.RxPropertyObservable
import rx.mvvm.value
import twitter4j.Twitter
import twitter4j.auth.RequestToken
import javax.inject.Inject

class OAuth @Inject constructor(private val twitter: Twitter, private val pref: AppPreferences) {

    val stateObservable = RxPropertyObservable.value(State.NORMAL)
    private var state by stateObservable.toProperty()
    val errorObservable = RxPropertyObservable.value(null as Throwable?)
    private var error by errorObservable.toProperty()
    val requestTokenObservable = RxPropertyObservable.value(null as RequestToken?)
    private var requestToken by requestTokenObservable.toProperty()

    fun restore(savedInstanceState: Bundle) {
        val token = savedInstanceState.getString("token") ?: return
        val tokenSecret = savedInstanceState.getString("tokenSecret") ?: return
        requestToken = RequestToken(token, tokenSecret)
    }

    fun save(outState: Bundle) {
        val requestToken = requestToken ?: return
        outState.putString("token", requestToken.token)
        outState.putString("tokenSecret", requestToken.tokenSecret)
    }

    fun getRequestTokenIfEnable() {
        if (requestToken != null) return
        if (state == State.REQUESTING) return
        state = State.REQUESTING
        error = null
        twitter.getOAuthRequestTokenObservable()
                .subscribe({
                    state = State.NORMAL
                    requestToken = it
                }, {
                    state = State.ERROR
                    error = it
                })
    }

    fun getAccessTokenIfEnable(pin: String) {
        val requestToken = requestToken ?: return
        if (state == State.REQUESTING) return
        state = State.REQUESTING
        error = null
        twitter.getOAuthAccessTokenObservable(requestToken, pin)
                .subscribe({
                    state = State.COMPLETED
                    pref.addAccount(it)
                }, {
                    state = State.ERROR
                    error = it
                })
    }
}
