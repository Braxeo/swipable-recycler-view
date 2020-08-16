package com.brandonkitt.demo

import android.content.Context
import android.text.InputType
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import java.util.*

object Helper {

    fun getTextInput(context: Context, title: String, onResult: (result: String) -> Unit) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        val input = EditText(context)
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        builder.setView(input)
        builder.setPositiveButton("OK") { _, _ -> onResult(input.text.toString()) }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    fun getNextColor(currentColor: Int): Int{
        return when(currentColor){
            android.R.color.holo_blue_light -> android.R.color.holo_green_light
            android.R.color.holo_green_light -> android.R.color.holo_orange_light
            android.R.color.holo_orange_light -> android.R.color.holo_purple
            android.R.color.holo_purple -> android.R.color.holo_red_light
            else -> android.R.color.holo_blue_light
        }
    }

}