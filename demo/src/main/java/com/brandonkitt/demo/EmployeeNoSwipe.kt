package com.brandonkitt.demo

import androidx.core.content.ContextCompat
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_employee.view.*

class EmployeeNoSwipe(
    val name: String,
    val color: Int
) : Item() {
    override fun getLayout(): Int = R.layout.item_employee
    override fun bind(viewHolder: ViewHolder, position: Int) {
        with(viewHolder){
            this.itemView.titleTextView.text = name
            this.itemView.setBackgroundColor(ContextCompat.getColor(this.itemView.context, color))
        }
    }
}