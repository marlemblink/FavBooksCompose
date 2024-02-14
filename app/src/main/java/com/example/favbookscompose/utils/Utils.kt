package com.example.favbookscompose.utils

import android.icu.text.DateFormat
import com.google.firebase.Timestamp

fun formatDate(timeStamp: Timestamp): String {
    return DateFormat.getDateInstance()
        .format(timeStamp.toDate())
        .toString().split(",")[0]
}