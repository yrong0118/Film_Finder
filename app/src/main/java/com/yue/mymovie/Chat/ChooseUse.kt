package com.yue.mymovie.Chat

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class ChooseUse (
    val uid : String,
    var username: String,
    val email :String,
    var profileImageUrl: String,
    var selected: Boolean
    ):Parcelable{
        constructor():this("","","","" ,false)
    }