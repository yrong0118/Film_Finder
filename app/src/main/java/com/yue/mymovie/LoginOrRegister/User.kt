package com.yue.mymovie.LoginOrRegister

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(
    val uid : String,
    var username: String,
    var email :String,
    val profileImageUrl: String

):Parcelable{
    constructor():this("","","","")
}
