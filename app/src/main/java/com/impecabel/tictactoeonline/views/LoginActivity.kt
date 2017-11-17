package com.impecabel.tictactoeonline.views

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseUser
import com.impecabel.tictactoeonline.R
import com.impecabel.tictactoeonline.data.model.User
import com.impecabel.tictactoeonline.data.source.remote.FirebaseUserService
import com.impecabel.tictactoeonline.data.source.remote.UserService
import com.impecabel.tictactoeonline.presenters.LoginPresenter
import kotlinx.android.synthetic.main.activity_login.*
import android.view.ViewGroup
import android.view.LayoutInflater


class LoginActivity : AppCompatActivity(), View.OnClickListener, ILoginView {

    private val TAG = "LOGIN"
    private val REQUEST_SIGN_GOOGLE = 9001
    private var loginPresenter: LoginPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginPresenter = LoginPresenter(this, FirebaseUserService(), UserService(), this)


    }

    override fun onClick(v: View?) {
        when (v) {
            anonymous_login_button -> loginPresenter?.firebaseAuthAnonymous()
            google_login_button -> {
                val intent = loginPresenter?.loginWithGoogle()
                startActivityForResult(intent, REQUEST_SIGN_GOOGLE)
            }
            sign_out_button -> loginPresenter?.signOut()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        // google
        if (requestCode == REQUEST_SIGN_GOOGLE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            loginPresenter?.firebaseAuthWithGoogle(task.result)

        }
    }

    override fun onStart() {
        super.onStart()
        loginPresenter?.checkAuth()
    }

    override fun showLoginActivity() {
        Log.d(TAG, "NO LOGIN")
        setContentView(R.layout.activity_login)
        anonymous_login_button.setOnClickListener(this)
        google_login_button.setOnClickListener(this)
        sign_out_button.setOnClickListener(this)
    }

    override fun showSnackBar(message: String) {
        Log.d(TAG, message)
    }

    override fun authSuccessful(user: User) {
        Log.d(TAG, "authSuccessful: ${user.name}")
        val intent = Intent(this@LoginActivity, GameListActivity::class.java)
        var bundle = Bundle()
        bundle.putParcelable("user", user)
        intent.putExtra("bundle", bundle)
        startActivity(intent)
        finish()
    }


    override fun authError() {
        Log.d(TAG, "authError")
    }

    override fun showLoading(show: Boolean) {
        login_progress.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showInsertUsername(user: User) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)

        builder.setTitle(getString(R.string.dialog_insert_username_title))
        val viewInflated = LayoutInflater.from(this).inflate(R.layout.text_input_dialog, null, false)
        val etUsername = viewInflated.findViewById<EditText>(R.id.input)
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        etUsername.setText(user.name)
        builder.setView(viewInflated)


        builder.setPositiveButton(getString(R.string.ok), DialogInterface.OnClickListener { dialog, _ ->
            val username = etUsername.text.toString()
            dialog.dismiss()
            loginPresenter?.createUser(user, username)
        })




        builder.show()


    }

    override fun showExistUsername(user: User, username: String) {
        Toast.makeText(this, "Exist username" + username, Toast.LENGTH_LONG).show()
        showInsertUsername(user)
    }

}
