package com.impecabel.tictactoeonline.data.source.remote



import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.impecabel.tictactoeonline.data.model.User


class UserService {
    private val databaseRef: DatabaseReference  = FirebaseDatabase.getInstance().reference

    fun createUser(user: User) {
        if (user.photo_url == null) {
            user.photo_url="NOT"
        }
        databaseRef.child("users").child(user.uid).setValue(user)
        databaseRef.child("usernames").child(user.username).setValue(user)

    }

    fun getUser(userUid: String): DatabaseReference {
        return databaseRef.child("users").child(userUid)
    }

    fun getUserByUsername(username: String): DatabaseReference {
        return databaseRef.child("usernames").child(username)
    }

    fun updateUser(user: User) {

    }

    fun deleteUser(key: String) {

    }
}
