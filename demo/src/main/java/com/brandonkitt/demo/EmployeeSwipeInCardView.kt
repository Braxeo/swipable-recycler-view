package com.brandonkitt.demo

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import com.swiperecyclerview.SwipeItem
import kotlinx.android.synthetic.main.item_employee.view.*
import kotlinx.android.synthetic.main.item_employee_left.view.*

class EmployeeSwipeInCardView(
    var name: String,
    var color: Int,
    private val actionListener: ActionListener?
): SwipeItem() {

    override fun getContainerLayout(): Int? = R.layout.item_employee_base
    override fun getLeftLayout(): Int? = R.layout.item_employee_left
    override fun getRightLayout(): Int? = R.layout.item_employee_right
    override fun getCentreLayout(): Int = R.layout.item_employee

    override fun canOverdraw(): Boolean = false

    override fun getSwipeDirs(): Int = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT

    override fun bindCentre(
        centreView: View,
        position: Int,
        payloads: MutableList<Any>
    ) {
        when {
            payloads.contains("Color") -> {
                centreView.setBackgroundColor(ContextCompat.getColor(centreView.context, color))
            }
            payloads.contains("Name") -> {
                centreView.titleTextView.text = name
            }
        }
    }

    override fun bindLeft(leftView: View?, position: Int) {
        leftView?.rename_textView?.setOnClickListener { actionListener?.onRenameClicked(this@EmployeeSwipeInCardView) }
        leftView?.background_textView?.setOnClickListener { actionListener?.onBackgroundClicked(this@EmployeeSwipeInCardView) }
    }

    override fun bindRight(rightView: View?, position: Int) {
        rightView?.setOnClickListener { actionListener?.onDeleteClicked(this@EmployeeSwipeInCardView) }
    }

    override fun bindCentre(centreView: View, position: Int) {
        centreView.titleTextView.text = name
        centreView.setBackgroundColor(ContextCompat.getColor(centreView.context, color))
    }

    interface ActionListener {
        fun onDeleteClicked(item: EmployeeSwipeInCardView)
        fun onRenameClicked(item: EmployeeSwipeInCardView)
        fun onBackgroundClicked(item: EmployeeSwipeInCardView)
    }
}