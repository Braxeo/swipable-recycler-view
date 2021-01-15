package com.swiperecyclerview

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.ViewHolder

/** Created by Brandon Kitt (15/08/2020)  */
class SwipeRecyclerView : RecyclerView {

    /**
     * Standard constructors used for initialization
     */
    constructor(context: Context): super(context)
    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int): super(context, attributeSet, defStyleAttr)

    init {
        // Create our own swipe controller
        val controller = SwipeController(this)

        // Attach to ItemTouchHelper for touch listening
        val helper = ItemTouchHelper(controller)

        // Attach to recyclerView
        helper.attachToRecyclerView(this)

        // Removes flashy animation when adding/removing views
        itemAnimator = null
    }

    /**
     * Updates the translation animations on the centre, left and right views
     * for the viewHolder
     */
    internal fun updateForTranslation(context: Context, viewHolder: ViewHolder, x: Float) {
        // Make sure we're using Groupie
        val adapter = adapter as? GroupAdapter

        // Check that we're using a SwipeItem
        val swipeItem = adapter?.getItem(viewHolder) as? SwipeItem

        swipeItem?.updateForTranslation(x, context)
    }

    /**
     * Gets the width of the option based on the viewHolder
     * (individual view sizes) and the option that is
     * expected to be shown
     *
     * @return The length of the Left or Right view
     */
    internal fun widthForOption(viewHolder: ViewHolder, option: SwipeController.OptionState): Int {

        // Get Groupie Adapter
        val adapter = adapter as? GroupAdapter

        // Get SwipeItem
        // Use the viewHolder to retrieve the item, as viewHolder.adapterPosition can give us
        // placeholder items with null values
        val swipeItem = adapter?.getItem(viewHolder) as? SwipeItem

        /**
         * If this isn't a swipe item, handle as we have no option,
         * otherwise get the length of the left or right layout
         */
        return swipeItem?.let { item ->
            when(option){
                SwipeController.OptionState.GONE -> 0
                SwipeController.OptionState.LEFT_VISIBLE -> item.getLeftLength(context)
                SwipeController.OptionState.RIGHT_VISIBLE -> item.getRightLength(context)
            }
        } ?: 0
    }


    /**
     * Gets the width of the option based on the current
     * translation of the centreView
     *
     * @return The length of the Left or Right view
     */
    internal fun widthFromCentreTranslation(viewHolder: ViewHolder, translation: Float): Int {

        // Get Groupie Adapter
        val adapter = adapter as? GroupAdapter

        // Get SwipeItem
        val swipeItem = adapter?.getItem(viewHolder) as? SwipeItem

        // Gets the Length of the closest option based on translation
        return swipeItem?.let { item ->
            if (translation > 0) item.getLeftLength(context)
            else item.getRightLength(context)
        } ?: 0
    }
}