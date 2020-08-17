package com.swiperecyclerview

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.view.MotionEvent
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE
import androidx.recyclerview.widget.RecyclerView

@SuppressLint("ClickableViewAccessibility")
class SwipeController(private val recyclerView: SwipeRecyclerView) : ItemTouchHelper.Callback() {

    enum class OptionState {
        GONE,
        LEFT_VISIBLE,
        RIGHT_VISIBLE
    }

    private var swipeBack: Boolean = false
    private var buttonShowedState = OptionState.GONE

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

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
        var x = dX
        if (actionState == ACTION_STATE_SWIPE) {
            if (buttonShowedState != OptionState.GONE) {
                val width = this.recyclerView.widthForOption(viewHolder, buttonShowedState)
                if (buttonShowedState == OptionState.LEFT_VISIBLE) x = dX.coerceAtLeast(width.toFloat())
                if (buttonShowedState == OptionState.RIGHT_VISIBLE) x = dX.coerceAtMost(-width.toFloat())
                this.recyclerView.updateForSwiping(viewHolder, x)
            }
            else {
                setTouchListener(recyclerView, viewHolder, x)
            }
        }

        if (buttonShowedState == OptionState.GONE) {
            this.recyclerView.updateForSwiping(viewHolder, x)
        }
    }

    private fun setTouchListener(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float
    ) {
        recyclerView.setOnTouchListener { _, motionEvent ->
            swipeBack = listOf(MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP).contains(motionEvent.action)

            if (swipeBack) {
                val width = this.recyclerView.widthFromCentreTranslation(viewHolder, dX)
                when {
                    (-dX > width && dX < 0) -> buttonShowedState = OptionState.RIGHT_VISIBLE
                    (dX > width) -> buttonShowedState = OptionState.LEFT_VISIBLE
                }

                if (buttonShowedState != OptionState.GONE) {
                    setTouchDownListener(
                        recyclerView,
                        viewHolder
                    )
                }
            }

            false
        }
    }

    private fun setTouchDownListener(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ) {
        recyclerView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                setTouchUpListener(
                    recyclerView,
                    viewHolder
                )
            }
            false
        }
    }

    private fun setTouchUpListener(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ) {
        recyclerView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                this.recyclerView.updateForSwiping(viewHolder, 0f)
                recyclerView.setOnTouchListener { _, _ -> false }
                swipeBack = false
                buttonShowedState = OptionState.GONE
            }
            false
        }
    }

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        if (swipeBack) {
            swipeBack = buttonShowedState != OptionState.GONE
            return 0
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }
}