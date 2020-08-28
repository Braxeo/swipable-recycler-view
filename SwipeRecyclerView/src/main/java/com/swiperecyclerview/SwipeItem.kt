package com.swiperecyclerview

import android.animation.ObjectAnimator
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import androidx.recyclerview.widget.ItemTouchHelper
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_swipe_base.view.*

/** Created by Brandon Kitt (15/08/2020)  */
abstract class SwipeItem(defaultTranslation: Float? = null) : Item() {

    /**
     * Used to dynamically adjust the translation of the left and right options based on
     * and changes made during the bind methods
     *
     * @see invalidateOptionLengths
     */
    private var translation: Float = defaultTranslation ?: 0f

    /**
     * Used to check if the centreView can
     * overdraw over the size of the closest option
     */
    open fun canOverdraw(): Boolean = true

    /**
     * This is the view that will surround the centre, left and right view's
     *
     * @throws SwipeException If view is not ViewGroup
     * @see containerView
     * @return The id for the containerView
     */
    open fun getContainerLayout(): Int? = null

    /**
     * This is the view that will appear on the left side of the centreView
     * This will only be shown if Left is specified in getSwipeDirs()
     *
     * @see getSwipeDirs
     * @see ItemTouchHelper.LEFT
     *
     * @return The id for the leftLayout
     */
    abstract fun getLeftLayout(): Int?

    /**
     * This is the view that will appear on the right side of the centreView
     * This will only be shown if right is specified in getSwipeDirs()
     *
     * @see getSwipeDirs
     * @see ItemTouchHelper.RIGHT
     *
     * @return The id for the rightLayout
     */
    abstract fun getRightLayout(): Int?

    /**
     * This is the view that will appear on the centre of the baseView
     * This will always be shown
     *
     * @return The id for the centreLayout
     */
    abstract fun getCentreLayout(): Int

    /**
     * Used to update a payload from leftLayout
     * This is only called when a payload is used in notifyChanged(payload: MutableList<Any>)
     */
    open fun bindLeft(leftView: View?, position: Int, payloads: MutableList<Any>) { bindLeft(leftView, position) }

    /**
     * Used to update a payload from rightLayout
     * This is only called when a payload is used in notifyChanged(payload: MutableList<Any>)
     */
    open fun bindRight(rightView: View?, position: Int, payloads: MutableList<Any>) { bindRight(rightView, position) }

    /**
     * Used to update a payload from centreLayout
     * This is only called when a payload is used in notifyChanged(payload: MutableList<Any>)
     */
    open fun bindCentre(centreView: View, position: Int, payloads: MutableList<Any>) { bindCentre(centreView, position) }

    /**
     * Used to update the all the views on the leftLayout
     * This is when no payload is specified in notifyChanged() or no implementation is created for the payload methods
     *
     * @see leftView
     * @see bindLeft
     */
    abstract fun bindLeft(leftView: View?, position: Int)

    /**
     * Used to update the all the views on the rightLayout
     * This is when no payload is specified in notifyChanged() or no implementation is created for the payload methods
     *
     * @see rightView
     * @see bindRight
     */
    abstract fun bindRight(rightView: View?, position: Int)

    /**
     * Used to update the all the views on the centreLayout
     * This is when no payload is specified in notifyChanged() or no implementation is created for the payload methods
     *
     * @see centreView
     * @see bindCentre
     */
    abstract fun bindCentre(centreView: View, position: Int)

    /**
     * Gets the base layout for this viewHolder
     * This can either be the specified containerLayout or the default SwipeItem.item_swipe_base
     *
     * This method should not be overridden
     *
     * @see getContainerLayout
     */
    override fun getLayout(): Int = getContainerLayout() ?: R.layout.item_swipe_base

    /**
     * Used to surround the baseView, centreView, leftView and rightView
     * This is used if the user wants to wrap the viewHolder in something like a cardView with
     * rounded corners or add padding
     *
     * @see getContainerLayout
     */
    private var containerView: View? = null

    /**
     * Used on the left side of the centreView
     * This will wrap to be the same size as the centreView
     *
     * To enable the use of this view, you will need to add ItemTouchListener.LEFT to getSwipeDirs()
     * Overdraw can be disabled with the use of canOverdraw()
     *
     * @see canOverdraw
     * @see ItemTouchHelper.LEFT
     * @see getSwipeDirs
     * @see centreView
     */
    private var leftView: View? = null

    /**
     * Used on the right side of the centreView
     * This will wrap to be the same size as the centreView
     *
     * To enable the use of this view, you will need to add ItemTouchListener.RIGHT to getSwipeDirs()
     * Overdraw can be disabled with the use of canOverdraw()
     *
     * @see canOverdraw
     * @see ItemTouchHelper.RIGHT
     * @see getSwipeDirs
     * @see centreView
     */
    private var rightView: View? = null

    /**
     * Used as the centreView between leftView and rightView
     * Both leftView and rightView will wrap to be the same height as this view
     *
     * @see leftView
     * @see rightView
     */
    private var centreView: View? = null

    /**
     * The base view used to contain the left, centre and right views
     * This gets wrapped inside the containerView if one is specified in getContainerLayout()
     *
     * @see getContainerLayout
     * @see leftView
     * @see centreView
     * @see rightView
     */
    private var base: ViewGroup? = null

    /**
     * Used to hold and translate the leftView
     * This view gets added to the baseView
     *
     * @see base
     */
    private var leftBase: View? = null

    /**
     * Used to hold and translate the rightView
     * This view gets added to the baseView
     *
     * @see base
     */
    private var rightBase: View? = null

    /**
     * Used to hold and translate the centreView
     * This view gets added to the baseView
     *
     * @see base
     */
    private var centreBase: View? = null

    /**
     *  Used to attach a layout listener to the left and right
     *  views
     *
     *  This is done so if the user hides or shows any views the
     *  translations adjust accordingly
     */
    private fun attachLayoutListeners(context: Context){
        attachGlobalLayoutListenerForPostUpdating(rightBase, context)
        attachGlobalLayoutListenerForPostUpdating(leftBase, context)
    }

    /**
     * Performs payload binding for all views if a payload
     * is specified, otherwise defaults to standard binding for all views
     */
    override fun bind(viewHolder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()){
            // Perform payload bind for all views
            bindLeft(leftView, position, payloads)
            centreView?.let { bindCentre(it, position, payloads) }
            bindRight(rightView, position, payloads)
            attachLayoutListeners(viewHolder.itemView.context)
        } else {
            // Perform standard bind for all views
            bind(viewHolder, position)
        }
    }

    /**
     * Performs standard binding for all views
     */
    override fun bind(viewHolder: ViewHolder, position: Int) {
        val inflater = LayoutInflater.from(viewHolder.itemView.context)

        // Check if we have a containerView
        if (getContainerLayout() != null){

            // Ensure that it is a ViewGroup
            if (viewHolder.itemView !is ViewGroup){
                throw SwipeException("getContainerLayout() must return a ViewGroup (CardView, LinearLayout, GridLayout)")
            }

            /** Set our viewHolder itemView to containerView
             * @see getLayout
             */
            containerView = viewHolder.itemView
        }

        // Only inflate if null
        if (base == null){

            base = if (getContainerLayout() == null){
                // ItemView if we don't have a containerView
                viewHolder.itemView as ViewGroup
            } else {
                // Inflate the base as the containerView is itemView
                inflater.inflate(
                    R.layout.item_swipe_base,
                    viewHolder.itemView as ViewGroup,
                    false
                ) as ViewGroup
            }
        }

        // Only inflate if null, as we remove and add views later
        if (centreView == null){
            centreView = inflater.inflate(getCentreLayout(), base, false)
        }
        if (leftView == null && getLeftLayout() != null){
            leftView = getLeftLayout()?.let {  layout -> inflater.inflate(layout, base, false) }
        }
        if (rightView == null && getRightLayout() != null){
            rightView = getRightLayout()?.let { layout -> inflater.inflate(layout, base, false) }
        }

        // Remove these views from their old parents
        (base?.parent as? ViewGroup)?.removeView(base)
        (centreView?.parent as? LinearLayout)?.removeView(centreView)
        (leftView?.parent as? LinearLayout)?.removeView(leftView)
        (rightView?.parent as? LinearLayout)?.removeView(rightView)

        // Removes the old views from the viewHolder
        (viewHolder.itemView as ViewGroup).removeAllViews()

        // Add these views to their new parents
        (containerView as? ViewGroup)?.addView(base)
        base?.centre_base?.addView(centreView)
        leftView?.let { view -> base?.left_base?.addView(view) }
        rightView?.let { view -> base?.right_base?.addView(view) }

        // Add references to bases for translation
        leftBase = base?.left_base
        centreBase = base?.centre_base
        rightBase = base?.right_base

        // Call bind methods for all views
        bindLeft(leftView, position)
        bindCentre(requireNotNull(centreView), position)
        bindRight(rightView, position)

        attachLayoutListeners(viewHolder.itemView.context)
    }

    /**
     * Attaches a layout listener to the given view
     *
     * This gets added to all the viewHolders, so we want to
     * ensure that we don't call invalidateOptionLengths() when not required
     *
     * Example - When the user hides a button on a leftView, this will get called
     * for all viewHolders currently visible, we would only want the one that
     * has been modified to update
     */
    private fun attachGlobalLayoutListenerForPostUpdating(view: View?, context: Context){
        view?.viewTreeObserver
            ?.takeIf { it.isAlive }
            ?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {

                    // Only mark as modified if we're translated
                    val modified = when {

                        // This view is showing the leftView
                        translation > 0 -> {
                            // Update to new leftView length
                            translation = getLeftLength(context).toFloat()
                            true
                        }

                        // This view is showing the rightView
                        translation < 0 -> {
                            // Update to new rightView length
                            translation = -getRightLength(context).toFloat()
                            true
                        }

                        // We aren't translated, no need to update
                        else -> false
                    }

                    if (modified){
                        invalidateOptionLengths(context)
                    }

                    view.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                }
            })
    }

    private fun invalidateOptionLengths(context: Context) {
        updateForTranslation(translation, context, 250)
    }

    /**
     * Updates the centre, left and right views based on the given translation
     *
     * If the swipeDirection is not specified in getSwipeDirs() or Overdraw will
     * occur and is not allowed, this will leave early and no translation will
     * be made
     *
     * @see getSwipeDirs
     * @see canOverdraw
     *
     * @param xTranslation The amount the centreView has been translated
     * @param context Used to calculate the closest options width for
     * validation
     */
    internal fun updateForTranslation(xTranslation: Float, context: Context, translationDuration: Long = 0) {

        // Store translation so we can dynamically translate based on left/right view bind() changes
        translation = xTranslation

        if (invalidSwipeDirection() ||
            invalidSwipeOverdraw(context))
        {
            // Leave early if we have an invalid translation
            return
        }

        // Animate the centreView by translation
        centreView?.let { view ->
            ObjectAnimator.ofFloat(view, "translationX", translation).apply {
                duration = translationDuration
                start()
            }
        }

        // Animate the left, right or both views based on translation
        when {
            translation > 0 -> adjustPositionForLeftView()
            translation < 0 -> adjustPositionForRightView()
            translation == 0f -> moveOptionsOutOfView()
        }
    }

    /**
     * Checks if the given translation is invalid for the items overdraw setting
     *
     * @see canOverdraw
     * @return True if the translation is invalid, otherwise false
     */
    private fun invalidSwipeOverdraw(context: Context): Boolean {
        return when {
            translation > 0 -> !canOverdraw() && translation > getLeftLength(context)
            translation < 0 -> !canOverdraw() && -translation > getRightLength(context)
            else -> false
        }
    }

    /**
     * Checks if the given translation is invalid for the items swipe direction setting
     *
     * @see getSwipeDirs
     * @return True if the translation is invalid, otherwise false
     */
    private fun invalidSwipeDirection(): Boolean {
        return when {
            translation > 0 -> swipeDirs and ItemTouchHelper.LEFT == 0
            translation < 0 -> swipeDirs and ItemTouchHelper.RIGHT == 0
            else -> false
        }
    }

    /**
     * Sets both leftView and rightView back to the default position (hidden)
     */
    private fun moveOptionsOutOfView() {
        adjustPositionForLeftView()
        adjustPositionForRightView()
    }

    /**
     * Adjusts the position of the leftView based on the given translation
     */
    private fun adjustPositionForLeftView(translationDuration: Long = 0) {
        leftBase?.let { view ->
            // Only move the leftView as far as its own width
            translation.coerceAtMost(view.width.toFloat()).let { calculatedMaximumTranslation ->
                ObjectAnimator.ofFloat(
                    view,
                    "translationX",
                    calculatedMaximumTranslation
                ).apply {
                    duration = translationDuration
                    start()
                }
            }
        }
    }

    /**
     * Adjusts the position of the rightView based on the given translation
     */
    private fun adjustPositionForRightView(translationDuration: Long = 0) {
        rightBase?.let { view ->
            // Only move the rightView as far as its own width
            translation.coerceAtLeast(-(view.width.toFloat())).let { calculatedMinimumTranslation ->
                ObjectAnimator.ofFloat(
                    view,
                    "translationX",
                    calculatedMinimumTranslation
                ).apply {
                    duration = translationDuration
                    start()
                }
            }
        }
    }

    /**
     * Gets the calculated length of the leftView
     */
    internal fun getLeftLength(context: Context): Int {
        return leftBase?.width ?:
               getLeftLayout()?.let {
                   LayoutInflater
                       .from(context)
                       .inflate(it, null, false)
                       .width
               } ?: 0
    }

    /**
     * Gets the calculated length of the rightView
     */
    internal fun getRightLength(context: Context): Int {
        return rightBase?.width ?:
               getRightLayout()?.let {
                   LayoutInflater
                       .from(context)
                       .inflate(it, null, false)
                       .width
               } ?: 0
    }
}