package com.swiperecyclerview

import android.animation.ObjectAnimator
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.contains
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_swipe_base.view.*

abstract class SwipeItem: Item() {

    abstract fun getLeftLayout(): Int?
    abstract fun getRightLayout(): Int?
    abstract fun getCentreLayout(): Int
    override fun getLayout(): Int = R.layout.item_swipe_base

    abstract fun bindLeft(viewHolder: ViewHolder, leftView: View?, position: Int)
    abstract fun bindRight(viewHolder: ViewHolder, rightView: View?, position: Int)
    abstract fun bindCentre(viewHolder: ViewHolder, centreView: View, position: Int)

    open fun bindLeft(viewHolder: ViewHolder, leftView: View?, position: Int, payloads: MutableList<Any>) { bindLeft(viewHolder, leftView, position) }
    open fun bindRight(viewHolder: ViewHolder, rightView: View?, position: Int, payloads: MutableList<Any>) { bindRight(viewHolder, rightView, position) }
    open fun bindCentre(viewHolder: ViewHolder, centreView: View, position: Int, payloads: MutableList<Any>) { bindCentre(viewHolder, centreView, position) }

    private var leftView: View? = null
    private var leftBase: View? = null

    private var rightView: View? = null
    private var rightBase: View? = null

    private var centreView: View? = null
    private var centreBase: View? = null

    override fun bind(viewHolder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()){
            bindLeft(viewHolder, leftView, position, payloads)
            centreView?.let { bindCentre(viewHolder, it, position, payloads) }
            bindRight(viewHolder, rightView, position, payloads)
        } else {
            bind(viewHolder, position)
        }
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val inflater = LayoutInflater.from(viewHolder.itemView.context)

        if (centreView == null){
            centreView = inflater.inflate(
                getCentreLayout(),
                viewHolder.itemView as ViewGroup,
                false
            )
        }
        if (leftView == null && getLeftLayout() != null){
            leftView = getLeftLayout()?.let {  layout ->
                inflater.inflate(
                    layout,
                    viewHolder.itemView as ViewGroup,
                    false
                ) }
        }
        if (rightView == null && getLeftLayout() != null){
            rightView = getRightLayout()?.let { layout ->
                inflater.inflate(layout, viewHolder.itemView as ViewGroup, false) }
        }

        (centreView?.parent as? LinearLayout)?.removeView(centreView)
        (viewHolder.itemView.centre_base as LinearLayout).addView(centreView)

        (leftView?.parent as? LinearLayout)?.removeView(leftView)
        leftView?.let { view -> (viewHolder.itemView.left_base as LinearLayout).addView(view) }

        (rightView?.parent as? LinearLayout)?.removeView(rightView)
        rightView?.let { view -> (viewHolder.itemView.right_base as LinearLayout).addView(view) }

        leftBase = viewHolder.itemView.left_base
        centreBase = viewHolder.itemView.centre_base
        rightBase = viewHolder.itemView.right_base

        bindLeft(viewHolder, leftView, position)
        bindCentre(viewHolder, requireNotNull(centreView), position)
        bindRight(viewHolder, rightView, position)
    }

    internal fun updateForSwiping(x: Float) {
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

    internal fun getLeftLength(context: Context): Int {
        return leftBase?.width ?:
               getLeftLayout()?.let {
                   LayoutInflater.from(context).inflate(it, null, false).width
               } ?:
               0
    }

    internal fun getRightLength(context: Context): Int {
        return rightBase?.width ?:
               getRightLayout()?.let {
                   LayoutInflater.from(context).inflate(it, null, false).width
               } ?:
               0
    }
}