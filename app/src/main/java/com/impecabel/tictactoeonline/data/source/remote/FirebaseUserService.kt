package com.impecabel.tictactoeonline.data.source.remote

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.impecabel.tictactoeonline.R


class FirebaseUserService {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    // for google
    private var googleSignInClient: GoogleSignInClient? = null


    fun getUserWithGoogle(activity: AppCompatActivity): Intent {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(activity, gso)

        return googleSignInClient?.signInIntent ?: Intent()
    }

    fun getAuthWithGoogle(activity: AppCompatActivity, acct: GoogleSignInAccount): Task<AuthResult> {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        return firebaseAuth.signInWithCredential(credential)
    }

    fun getAuthWitAnonymous(): Task<AuthResult> {
        return firebaseAuth.signInAnonymously()
    }

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    fun logOut(activity: AppCompatActivity) {
        firebaseAuth.signOut()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(activity, gso)
        googleSignInClient?.signOut()
    }

}
