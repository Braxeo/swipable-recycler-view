package com.swiperecyclerview

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.view.MotionEvent
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE
import androidx.recyclerview.widget.RecyclerView
import com.swiperecyclerview.SwipeController.OptionState.*

/** Created by Brandon Kitt (15/08/2020)  */
@SuppressLint("ClickableViewAccessibility")
class SwipeController(private val recyclerView: SwipeRecyclerView) : ItemTouchHelper.Callback() {

    enum class OptionState {
        GONE,
        LEFT_VISIBLE,
        RIGHT_VISIBLE
    }

    private var swipeBack: Boolean = false
    private var buttonShowedState = GONE

    /**
     * Enables both the Left and Right movement flags for
     * recyclerView as we want to receive both callback
     * and handle them ourselves with our own translations
     */
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        // We want both left and right callbacks, no drag callbacks
        return makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
    }

    /**
     * This is removed as we aren't handling reordering
     */
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    /**
     * TODO - Should this be blank? Should we overwirse this?
     */
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        // Store the viewHolder position, we use this to reference
        // the correct adapter item later, not viewHolder.adapterPosition as
        // this gets outdated throughout the onTouchListeners
        // resulting in -1
        val position = viewHolder.adapterPosition
        val context= viewHolder.itemView.context
        var x = dX

        if (actionState == ACTION_STATE_SWIPE) {

            // If we're swiping and are showing an option
            if (buttonShowedState != GONE) {

                // Get width for the currently displayed item
                val width = this.recyclerView.widthForOption(viewHolder, buttonShowedState)

                when(buttonShowedState){
                    LEFT_VISIBLE -> x = dX.coerceAtLeast(width.toFloat())
                    RIGHT_VISIBLE -> x = dX.coerceAtMost(-width.toFloat())
                    else -> Unit
                }

                this.recyclerView.updateForTranslation(context, position, x)
            }
            else {
                setTouchListener(recyclerView, position, x)
            }
        }

        // Make sure that if we are not longer showing anything we still want to update
        // in-case we previously were showing an option
        if (buttonShowedState == GONE) {
            this.recyclerView.updateForTranslation(context, position, x)
        }
    }

    private fun setTouchListener(
        recyclerView: RecyclerView,
        position: Int,
        dX: Float
    ) {
        recyclerView.setOnTouchListener { _, motionEvent ->

            // Update flag for if we've stopped/let go of the screen
            swipeBack = listOf(MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP).contains(motionEvent.action)

            if (swipeBack) {

                // Get width based on current translation,
                // as we don't have an option to choose from yet
                val width = this.recyclerView.widthFromCentreTranslation(position, dX)

                when {
                    (-dX > width && dX < 0) -> buttonShowedState = RIGHT_VISIBLE
                    (dX > width) -> buttonShowedState = LEFT_VISIBLE
                }

                // Update if we're past a button threshold
                if (buttonShowedState != GONE) {
                    setTouchDownListener(recyclerView, position)
                }
            }

            false
        }
    }

    private fun setTouchDownListener(
        recyclerView: RecyclerView,
        position: Int
    ) {
        recyclerView.setOnTouchListener { _, event ->
            // Update if action down and move
            // Updated to used ACTION_MOVE to be more reactive and friendly to user-interaction
            if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                setTouchUpListener(recyclerView, position)
            }
            false
        }
    }

    private fun setTouchUpListener(
        recyclerView: RecyclerView,
        position: Int
    ) {
        recyclerView.setOnTouchListener { _, event ->

            // Update if action up
            if (event.action == MotionEvent.ACTION_UP) {

                // Revert translations back to default
                this.recyclerView.updateForTranslation(recyclerView.context, position, 0f)

                // Remove onTouchListeners
                recyclerView.setOnTouchListener { _, _ -> false }

                // Reset to defaults
                swipeBack = false
                buttonShowedState = GONE
            }
            false
        }
    }

    /**
     * Override to handle the position we want to
     * end back in dependant on if we're in the state of SwipeBack
     *
     * If we're currently in SwipeBack, then we want to move all
     * the way back to the default position, we can skip all other calculations
     *
     * We update the SwipeBack flag in case of async operations
     */
    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {

        // Check if we're currently translating back to our original positions
        if (swipeBack) {

            // Update based on ButtonState
            swipeBack = buttonShowedState != GONE

            // Go back to default translation
            return 0
        }

        // Otherwise call super
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }
}