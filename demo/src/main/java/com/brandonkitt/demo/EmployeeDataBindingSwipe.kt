package com.brandonkitt.demo

import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import com.brandonkitt.demo.databinding.*
import com.swiperecyclerview.BindableSwipeItem

class EmployeeDataBindingSwipe(
    var name: String,
    var color: Int,
    private val actionListener: ActionListener?
) : BindableSwipeItem<ItemBindingEmployeeLeftBinding, ItemBindingEmployeeBinding, ItemBindingEmployeeRightBinding>() {
    override fun getLeftLayout(): Int = R.layout.item_binding_employee_left
    override fun getRightLayout(): Int = R.layout.item_binding_employee_right
    override fun getCentreLayout(): Int = R.layout.item_binding_employee

    override fun getSwipeDirs(): Int = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT

    override fun bindCentre(
        centreView: ItemBindingEmployeeBinding,
        position: Int,
        payloads: MutableList<Any>
    ) {
        when {
            payloads.contains("Color") -> centreView.backgroundColor = color
            payloads.contains("Name") -> centreView.title = name
        }
    }

    override fun bindLeft(leftView: ItemBindingEmployeeLeftBinding?, position: Int) {
        leftView?.renameOnClick = View.OnClickListener { actionListener?.onRenameClicked(this@EmployeeDataBindingSwipe) }
        leftView?.backgroundOnClick = View.OnClickListener { actionListener?.onBackgroundClicked(this@EmployeeDataBindingSwipe) }
    }

    override fun bindRight(rightView: ItemBindingEmployeeRightBinding?, position: Int) {
        rightView?.deleteOnClick = View.OnClickListener { actionListener?.onDeleteClicked(this@EmployeeDataBindingSwipe) }
    }

    override fun bindCentre(centreView: ItemBindingEmployeeBinding, position: Int) {
        centreView.title = name
        centreView.backgroundColor = color
    }

    interface ActionListener {
        fun onDeleteClicked(item: EmployeeDataBindingSwipe)
        fun onRenameClicked(item: EmployeeDataBindingSwipe)
        fun onBackgroundClicked(item: EmployeeDataBindingSwipe)
    }
}