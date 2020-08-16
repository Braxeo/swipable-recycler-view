package com.brandonkitt.demo

import android.view.View
import androidx.core.content.ContextCompat
import com.swiperecyclerview.SwipeItem
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_employee.view.*
import kotlinx.android.synthetic.main.item_employee_left.view.*

class EmployeeSwipe(
    var name: String,
    var color: Int,
    private val actionListener: ActionListener?

) : SwipeItem() {
    override fun getLeftLayout(): Int? = R.layout.item_employee_left
    override fun getRightLayout(): Int? = R.layout.item_employee_right
    override fun getCentreLayout(): Int = R.layout.item_employee

    override fun bindLeft(viewHolder: ViewHolder, leftView: View?, position: Int) {
        leftView?.rename_textView?.setOnClickListener { actionListener?.onRenameClicked(this@EmployeeSwipe) }
        leftView?.background_textView?.setOnClickListener { actionListener?.onBackgroundClicked(this@EmployeeSwipe) }
    }

    override fun bindRight(viewHolder: ViewHolder, rightView: View?, position: Int) {
        rightView?.setOnClickListener { actionListener?.onDeleteClicked(this@EmployeeSwipe) }
    }

    override fun bindCentre(viewHolder: ViewHolder, centreView: View, position: Int) {
        centreView.name_textView.text = name
        centreView.setBackgroundColor(ContextCompat.getColor(centreView.context, color))
    }

    interface ActionListener {
        fun onDeleteClicked(item: EmployeeSwipe)
        fun onRenameClicked(item: EmployeeSwipe)
        fun onBackgroundClicked(item: EmployeeSwipe)
    }
}