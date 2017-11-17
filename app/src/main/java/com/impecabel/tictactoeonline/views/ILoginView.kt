package com.impecabel.tictactoeonline.views

import com.google.firebase.auth.FirebaseUser
import com.impecabel.tictactoeonline.data.model.User

/**
 * Created by x00881 on 14/11/2017.
 */
interface ILoginView {
    fun showSnackBar(message: String)
    fun authSuccessful(user: User)
    fun authError()
    fun showLoading(show: Boolean)
    fun showInsertUsername(user: User)
    fun showExistUsername(user: User, username: String)
    fun showLoginActivity()
}