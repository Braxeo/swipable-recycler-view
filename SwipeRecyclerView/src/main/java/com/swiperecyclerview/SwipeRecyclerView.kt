package com.swiperecyclerview

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter

class SwipeRecyclerView : RecyclerView {
    constructor(context: Context): super(context)
    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int): super(context, attributeSet, defStyleAttr)

    init {
        val controller = SwipeController(this)
        val helper = ItemTouchHelper(controller)
        helper.attachToRecyclerView(this)
    }

    fun updateForSwiping(viewHolder: ViewHolder, x: Float) {
        val position = viewHolder.adapterPosition
        if (position != -1){
            ((adapter as? GroupAdapter)?.getItem(position) as? SwipeItem)?.updateForSwiping(x)
        }
    }

    fun widthForOption(viewHolder: ViewHolder, option: SwipeController.OptionState): Int {
        return ((adapter as? GroupAdapter)?.getItem(viewHolder.adapterPosition) as? SwipeItem)?.let { item ->
            when(option){
                SwipeController.OptionState.GONE -> 0
                SwipeController.OptionState.LEFT_VISIBLE -> item.getLeftLength(context)
                SwipeController.OptionState.RIGHT_VISIBLE -> item.getRightLength(context)
            }
        } ?: 0
    }

    fun widthFromCentreTranslation(viewHolder: ViewHolder, translation: Float): Int {
        return ((adapter as? GroupAdapter)?.getItem(viewHolder.adapterPosition) as? SwipeItem)?.let { item ->
            if (translation > 0){
                item.getLeftLength(context)
            } else {
                item.getRightLength(context)
            }
        } ?: 0
    }
}