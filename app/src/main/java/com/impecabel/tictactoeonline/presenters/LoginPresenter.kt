package com.impecabel.tictactoeonline.presenters

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserInfo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.impecabel.tictactoeonline.data.model.User
import com.impecabel.tictactoeonline.data.source.remote.FirebaseUserService
import com.impecabel.tictactoeonline.data.source.remote.UserService
import com.impecabel.tictactoeonline.views.ILoginView
import com.impecabel.tictactoeonline.views.LoginActivity

class LoginPresenter(private val activity: LoginActivity, private val firebaseUserService: FirebaseUserService,
                     private val userService: UserService, private val iLoginView: ILoginView) {

    fun firebaseAuthAnonymous() {
        iLoginView.showLoading(true)
        firebaseUserService.getAuthWitAnonymous()
                .addOnCompleteListener { task ->
                    iLoginView.showLoading(false)
                    if (!task.isSuccessful) {
                        iLoginView.authError()
                        iLoginView.showSnackBar("Firebase authentication failed, please check your internet connection")
                    } else {
                       // val user =  task.result.user
                        processLogin(task.result.user, task.result.user.providerData[0])
                    }
                }
    }

    fun loginWithGoogle(): Intent {
        return firebaseUserService.getUserWithGoogle(activity)
    }

    fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        iLoginView.showLoading(true)

        firebaseUserService.getAuthWithGoogle(activity, acct)
                .addOnCompleteListener { task ->
                    iLoginView.showLoading(false)
                    if (!task.isSuccessful) {
                        iLoginView.authError()
                        iLoginView.showSnackBar("Firebase authentication failed, please check your internet connection")
                    } else {
                        processLogin(task.result.user, task.result.user.providerData[1])
                    }
                }
    }

    fun signOut() {
        firebaseUserService.logOut(activity)
    }

    fun checkAuth() {
       // iLoginView.showLoading(true)
        val user = firebaseUserService.getCurrentUser()

        if (user == null) {
            iLoginView.showLoginActivity()
        } else {
            processLogin(user)
        }

    }

    private fun processLogin(firebaseUser: FirebaseUser, userInfo: UserInfo) {
        val user = User(firebaseUser, userInfo)
        userService.getUser(user.uid).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val remoteUser = dataSnapshot.getValue<User>(User::class.java)
                        if (remoteUser == null || remoteUser!!.username == null) {
                            iLoginView.showInsertUsername(user)
                        } else {
                            iLoginView.authSuccessful(user)
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        iLoginView.authError()
                    }
                }
        )
    }

    private fun processLogin(user: FirebaseUser) {
        userService.getUser(user.uid).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val user = dataSnapshot.getValue(User::class.java)

                        if (user?.username == null) {
                            iLoginView.showLoginActivity()
                        } else {
                            iLoginView.authSuccessful(user)
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        iLoginView.showLoginActivity()
                    }
                }
        )
    }

    fun createUser(user: User, username: String) {
        activity.showLoading(true)
        userService.getUserByUsername(username).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val exists = dataSnapshot.exists()
                        if (!exists) {
                            iLoginView.showLoading(false)
                            user.username = username
                            userService.createUser(user)
                            iLoginView.authSuccessful(user)
                        } else {
                            iLoginView.showLoading(false)
                            activity.showExistUsername(user, username)
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        iLoginView.showLoading(false)
                        iLoginView.showInsertUsername(user)
                    }
                }
        )
    }

}