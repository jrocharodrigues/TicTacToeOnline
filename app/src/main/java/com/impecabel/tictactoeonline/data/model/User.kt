package com.impecabel.tictactoeonline.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserInfo


class User constructor(var uid: String, var username: String?, var email: String?, var provider: String?, var photo_url: String?, var name: String?): Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    constructor(uid: String) : this(uid, null, null, null, null, null)
    constructor() : this("", null, null, null, null, null)

    constructor(firebaseUser: FirebaseUser, provider: UserInfo) : this(firebaseUser.uid) {
        this.provider = provider.providerId
        if (provider.providerId == "google.com") {
            email = firebaseUser.email
            name = provider.displayName
            photo_url = provider.photoUrl!!.toString()
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeString(username)
        parcel.writeString(email)
        parcel.writeString(provider)
        parcel.writeString(photo_url)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}