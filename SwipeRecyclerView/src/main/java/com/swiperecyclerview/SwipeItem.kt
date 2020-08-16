package com.swiperecyclerview

import android.animation.ObjectAnimator
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_swipe_base.view.*

abstract class SwipeItem: Item() {

    abstract fun getLeftLayout(): Int?
    abstract fun getRightLayout(): Int?
    abstract fun getCentreLayout(): Int
    abstract fun bindLeft(viewHolder: ViewHolder, leftView: View?, position: Int)
    abstract fun bindRight(viewHolder: ViewHolder, rightView: View?, position: Int)
    abstract fun bindCentre(viewHolder: ViewHolder, centreView: View, position: Int)

    private var leftView: View? = null
    private var leftBase: View? = null

    private var rightView: View? = null
    private var rightBase: View? = null

    private var centreView: View? = null
    private var centreBase: View? = null

    override fun notifyChanged() {
        super.notifyChanged()
    }

    override fun notifyChanged(payload: Any?) {
        super.notifyChanged(payload)
    }

    override fun getLayout(): Int = R.layout.item_swipe_base

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val inflater = LayoutInflater.from(viewHolder.itemView.context)

        if (centreView == null){
            centreView = inflater.inflate(getCentreLayout(), viewHolder.itemView as ViewGroup, false)
            (viewHolder.itemView.centre_base as LinearLayout).addView(centreView)
        }

        if (leftView == null && getLeftLayout() != null){
            leftView = getLeftLayout()?.let { inflater.inflate(it, viewHolder.itemView as ViewGroup, false) }
            leftView?.let { view -> (viewHolder.itemView.left_base as LinearLayout).addView(view) }
        }

        if (rightView == null && getLeftLayout() != null){
            rightView = getRightLayout()?.let { inflater.inflate(it, viewHolder.itemView as ViewGroup, false) }
            rightView?.let { view -> (viewHolder.itemView.right_base as LinearLayout).addView(view) }
        }

        leftBase = viewHolder.itemView.left_base
        centreBase = viewHolder.itemView.centre_base
        rightBase = viewHolder.itemView.right_base

        bindLeft(viewHolder, leftView, position)
        bindCentre(viewHolder, requireNotNull(centreView), position)
        bindRight(viewHolder, rightView, position)
    }

    fun updateForSwiping(x: Float) {
        centreView?.let { view ->
            ObjectAnimator.ofFloat(view, "translationX", x).apply {
                duration = 0
                start()
            }
        }

        when {
            x > 0 -> adjustPositionForLeft(x)
            x < 0 -> adjustPositionForRight(x)
            x == 0f -> removeOptions()
        }
    }

    private fun removeOptions(){
        adjustPositionForLeft(0f)
        adjustPositionForRight(0f)
    }

    private fun adjustPositionForLeft(x: Float) {
        leftBase?.let { view ->
            val translation = x.coerceAtMost(view.width.toFloat())
            ObjectAnimator.ofFloat(view, "translationX", translation).apply {
                duration = 0
                start()
            }
        }

    }

    private fun adjustPositionForRight(x: Float) {
        rightBase?.let { view ->
            val translation = x.coerceAtLeast(-(view.width.toFloat()))
            ObjectAnimator.ofFloat(view, "translationX", translation).apply {
                duration = 0
                start()
            }
        }

    }

    fun getLeftLength(context: Context): Int {
        return leftBase?.width ?:
               getLeftLayout()?.let {
                   LayoutInflater.from(context).inflate(it, null, false).width
               } ?:
               0
    }

    fun getRightLength(context: Context): Int {
        return rightBase?.width ?:
               getRightLayout()?.let {
                   LayoutInflater.from(context).inflate(it, null, false).width
               } ?:
               0
    }
}