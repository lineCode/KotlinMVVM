package com.teamlab.kotlin.mvvm.data.model

import android.content.Context
import com.teamlab.kotlin.mvvm.data.repository.AccountRepository
import com.teamlab.kotlin.mvvm.di.ApplicationScope
import com.teamlab.kotlin.mvvm.rx.pref
import rx.mvvm.Model
import rx.property.RxPropertyObservable
import twitter4j.auth.AccessToken
import javax.inject.Inject

@ApplicationScope
class AppPreferences @Inject constructor(private val context: Context, private val accountRepository: AccountRepository) : Model<Unit>() {
    private val SEPARATOR = ","

    override val id = Unit
    private val pref = context.getSharedPreferences("app", Context.MODE_PRIVATE)

    val accountsObservable = RxPropertyObservable.pref<List<Account>>(
            pref,
            "accounts",
            emptyList(),
            { key, defValue ->
                getString(key, null)?.let {
                    it.split(SEPARATOR).map { accountRepository.of(it.toLong()) }
                } ?: defValue
            },
            { key, value ->
                putString(key, value.map { it.id.toString() }.joinToString(SEPARATOR))
            })
    var accounts by accountsObservable.toProperty()

    fun addAccount(token: AccessToken): Account {
        val account = accountRepository.of(token.userId)
        if (account in accounts) throw RuntimeException("$account は登録済みのアカウントです。")
        account.initialize(token)
        accounts += account
        return account
    }
}
