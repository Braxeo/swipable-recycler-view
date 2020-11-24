package com.swiperecyclerview

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import java.lang.reflect.Type

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

    @JvmName(name = "setSwipeAdapter")
    fun setAdapter(adapter: Adapter<com.xwray.groupie.ViewHolder>?){
        super.setAdapter(adapter)
    }

    @Suppress("UNCHECKED_CAST")
    @Deprecated(
        level = DeprecationLevel.ERROR,
        message = "Specific ViewHolder type required, use setAdapter(adapter: Adapter<com.xwray.groupie.ViewHolder>)",
        replaceWith = ReplaceWith(
            "setAdapter(adapter as Adapter<com.xwray.groupie.ViewHolder>)",
            "androidx.recyclerview.widget.RecyclerView.Adapter"
        )
    )
    override fun setAdapter(adapter: Adapter<*>?) {
        setAdapter(adapter as Adapter<com.xwray.groupie.ViewHolder>)
    }

    /**
     * Updates the translation animations on the centre, left and right views
     * for the viewHolder
     */
    internal fun updateForTranslation(context: Context, position: Int, x: Float) {
        // Make sure that the adapter isn't in a weird state
        if (position != -1){

            // Make sure we're using Groupie
            val adapter = adapter as? GroupAdapter

            // Check that we're using a SwipeItem
            val swipeItem = adapter?.getItem(position) as? SwipeItem

            swipeItem?.updateForTranslation(x, context)
        }
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
        val swipeItem = adapter?.getItem(viewHolder.adapterPosition) as? SwipeItem

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
    internal fun widthFromCentreTranslation(position: Int, translation: Float): Int {

        // Get Groupie Adapter
        val adapter = adapter as? GroupAdapter

        // Get SwipeItem
        return if (position != -1){
            val swipeItem = adapter?.getItem(position) as? SwipeItem

            // Gets the Length of the closest option based on translation
            swipeItem?.let { item ->
                if (translation > 0) item.getLeftLength(context)
                else item.getRightLength(context)
            } ?: 0
        } else 0
    }
}