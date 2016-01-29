package com.teamlab.kotlin.mvvm.di

import android.app.Activity
import android.content.Context
import android.support.v4.app.Fragment
import com.teamlab.kotlin.mvvm.MyApplication
import com.teamlab.kotlin.mvvm.view.AccountActivity
import com.teamlab.kotlin.mvvm.view.AccountsFragment
import com.teamlab.kotlin.mvvm.view.AddAccountDialogFragment
import com.teamlab.kotlin.mvvm.view.MainActivity
import dagger.Component

@Component(modules = arrayOf(ApplicationModule::class))
@ApplicationScope
interface ApplicationComponent {

    fun inject(mainActivity: MainActivity)
    fun inject(accountActivity: AccountActivity)
    fun inject(accountsFragment: AccountsFragment)
    fun inject(addAccountDialogFragment: AddAccountDialogFragment)

    companion object {
        fun from(context: Context) = (context.applicationContext as MyApplication).component
        fun from(activity: Activity) = from(activity as Context)
        fun from(fragment: Fragment) = from(fragment.activity)
    }
}
