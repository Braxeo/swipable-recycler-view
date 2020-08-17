package com.swiperecyclerview

import android.animation.ObjectAnimator
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.ItemTouchHelper
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_swipe_base.view.*

abstract class SwipeItem: Item() {

    abstract fun getLeftLayout(): Int?
    abstract fun getRightLayout(): Int?
    abstract fun getCentreLayout(): Int
    override fun getLayout(): Int = getContainerLayout() ?: R.layout.item_swipe_base

    abstract fun bindLeft(leftView: View?, position: Int)
    abstract fun bindRight(rightView: View?, position: Int)
    abstract fun bindCentre(centreView: View, position: Int)

    open fun getContainerLayout(): Int? = null
    open fun bindLeft(leftView: View?, position: Int, payloads: MutableList<Any>) { bindLeft(leftView, position) }
    open fun bindRight(rightView: View?, position: Int, payloads: MutableList<Any>) { bindRight(rightView, position) }
    open fun bindCentre(centreView: View, position: Int, payloads: MutableList<Any>) { bindCentre(centreView, position) }

    private var containerView: View? = null
    private var base: ViewGroup? = null

    private var leftView: View? = null
    private var leftBase: View? = null

    private var rightView: View? = null
    private var rightBase: View? = null

    private var centreView: View? = null
    private var centreBase: View? = null

    override fun bind(viewHolder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()){
            bindLeft(leftView, position, payloads)
            centreView?.let { bindCentre(it, position, payloads) }
            bindRight(rightView, position, payloads)
        } else {
            bind(viewHolder, position)
        }
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {

        val inflater = LayoutInflater.from(viewHolder.itemView.context)

        if (getContainerLayout() != null){
            if (viewHolder.itemView !is ViewGroup){
                throw SwipeException("getContainerLayout() must return a ViewGroup (CardView, LinearLayout, GridLayout)")
            }
            containerView = viewHolder.itemView
        }

        if (base == null){
            base = if (getContainerLayout() == null){
                viewHolder.itemView as ViewGroup
            } else {
                inflater.inflate(
                    R.layout.item_swipe_base,
                    viewHolder.itemView as ViewGroup,
                    false
                ) as ViewGroup
            }
        }

        if (centreView == null){
            centreView = inflater.inflate(
                getCentreLayout(),
                base,
                false
            )
        }
        if (leftView == null && getLeftLayout() != null){
            leftView = getLeftLayout()?.let {  layout ->
                inflater.inflate(
                    layout,
                    base,
                    false
                ) }
        }
        if (rightView == null && getRightLayout() != null){
            rightView = getRightLayout()?.let { layout ->
                inflater.inflate(layout, base, false) }
        }

        (base?.parent as? ViewGroup)?.removeView(base)
        (containerView as? ViewGroup)?.addView(base)

        (centreView?.parent as? LinearLayout)?.removeView(centreView)
        base?.centre_base?.addView(centreView)

        (leftView?.parent as? LinearLayout)?.removeView(leftView)
        leftView?.let { view -> base?.left_base?.addView(view) }

        (rightView?.parent as? LinearLayout)?.removeView(rightView)
        rightView?.let { view -> base?.right_base?.addView(view) }

        leftBase = base?.left_base
        centreBase = base?.centre_base
        rightBase = base?.right_base

        bindLeft(leftView, position)
        bindCentre(requireNotNull(centreView), position)
        bindRight(rightView, position)
    }

    internal fun updateForSwiping(x: Float) {
        when {
            x > 0 -> if (swipeDirs and ItemTouchHelper.LEFT == 0) return
            x < 0 -> if (swipeDirs and ItemTouchHelper.RIGHT == 0) return
        }

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
        if (swipeDirs and ItemTouchHelper.LEFT == 0) return

        leftBase?.let { view ->
            val translation = x.coerceAtMost(view.width.toFloat())
            ObjectAnimator.ofFloat(view, "translationX", translation).apply {
                duration = 0
                start()
            }
        }

    }

    private fun adjustPositionForRight(x: Float) {
        if (swipeDirs and ItemTouchHelper.RIGHT == 0) return

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