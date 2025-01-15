package com.comedy.suggester.ui.common

import android.content.Context
import android.widget.Toast

fun showText(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}