package com.brandonkitt.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var noSwipeSection: Section
    private lateinit var swipeSection: Section
    private lateinit var bindableSwipeSection: Section
    private lateinit var cardSection: Section

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val section = Section()
        noSwipeSection = Section()
        swipeSection = Section()
        bindableSwipeSection = Section()
        cardSection = Section()
        section.addAll(listOf(cardSection, noSwipeSection, swipeSection, bindableSwipeSection))

        val noSwipeItems = mutableListOf(
            EmployeeNoSwipe(
                name = "James",
                color = android.R.color.holo_blue_light
            ),
            EmployeeNoSwipe(
                name = "Barry",
                color = android.R.color.holo_blue_light
            ),
            EmployeeNoSwipe(
                name = "Sarah",
                color = android.R.color.holo_orange_light
            ),
            EmployeeNoSwipe(
                name = "James",
                color = android.R.color.holo_blue_light
            ),
            EmployeeNoSwipe(
                name = "Barry",
                color = android.R.color.holo_blue_light
            ),
            EmployeeNoSwipe(
                name = "Sarah",
                color = android.R.color.holo_orange_light
            )
        )

        val swipeItems = mutableListOf(
            EmployeeSwipe(
                name = "Tim",
                color = android.R.color.holo_orange_light,
                actionListener = swipeActionListener
            ),
            EmployeeSwipe(
                name = "Tim",
                color = android.R.color.holo_orange_light,
                actionListener = swipeActionListener
            ),
            EmployeeSwipe(
                name = "Tim",
                color = android.R.color.holo_orange_light,
                actionListener = swipeActionListener
            ),
            EmployeeSwipe(
                name = "Tim",
                color = android.R.color.holo_orange_light,
                actionListener = swipeActionListener
            )
        )

        val bindableSwipeItems = mutableListOf(
            EmployeeDataBindingSwipe(
                name = "Tim",
                color = android.R.color.holo_orange_light,
                actionListener = bindableSwipeActionListener
            ),
            EmployeeDataBindingSwipe(
                name = "Tim",
                color = android.R.color.holo_orange_light,
                actionListener = bindableSwipeActionListener
            ),
            EmployeeDataBindingSwipe(
                name = "Tim",
                color = android.R.color.holo_orange_light,
                actionListener = bindableSwipeActionListener
            ),
            EmployeeDataBindingSwipe(
                name = "Tim",
                color = android.R.color.holo_orange_light,
                actionListener = bindableSwipeActionListener
            )
        )

        val cardItems = mutableListOf(
            EmployeeSwipeInCardView(
                name = "Tom",
                color = android.R.color.holo_red_light,
                actionListener = cardActionListener
            ),
            EmployeeSwipeInCardView(
                name = "James",
                color = android.R.color.holo_blue_light,
                actionListener = cardActionListener
            ),
            EmployeeSwipeInCardView(
                name = "Timothy",
                color = android.R.color.holo_purple,
                actionListener = cardActionListener
            ),
            EmployeeSwipeInCardView(
                name = "Tom",
                color = android.R.color.holo_red_light,
                actionListener = cardActionListener
            ),
            EmployeeSwipeInCardView(
                name = "James",
                color = android.R.color.holo_blue_light,
                actionListener = cardActionListener
            ),
            EmployeeSwipeInCardView(
                name = "Timothy",
                color = android.R.color.holo_purple,
                actionListener = cardActionListener
            ),
            EmployeeSwipeInCardView(
                name = "Tom",
                color = android.R.color.holo_red_light,
                actionListener = cardActionListener
            ),
            EmployeeSwipeInCardView(
                name = "James",
                color = android.R.color.holo_blue_light,
                actionListener = cardActionListener
            ),
            EmployeeSwipeInCardView(
                name = "Timothy",
                color = android.R.color.holo_purple,
                actionListener = cardActionListener
            )
        )

        noSwipeSection.addAll(noSwipeItems)
        swipeSection.addAll(swipeItems)
        bindableSwipeSection.addAll(bindableSwipeItems)
        cardSection.addAll(cardItems)

        swipe_recycler_view.setAdapter(GroupAdapter<ViewHolder>().apply { add(section) })
    }

    private val swipeActionListener = object : EmployeeSwipe.ActionListener {
        override fun onDeleteClicked(item: EmployeeSwipe) {
            swipeSection.remove(item)
        }

        override fun onRenameClicked(item: EmployeeSwipe) {
            Helper.getTextInput(this@MainActivity, "Rename ${item.name}"){ newName ->
                item.name = newName
                item.notifyChanged("Name")
            }
        }

        override fun onBackgroundClicked(item: EmployeeSwipe) {
            item.color = Helper.getNextColor(item.color)
            item.notifyChanged("Color")
        }
    }

    private val bindableSwipeActionListener = object : EmployeeDataBindingSwipe.ActionListener {
        override fun onDeleteClicked(item: EmployeeDataBindingSwipe) {
            swipeSection.remove(item)
        }

        override fun onRenameClicked(item: EmployeeDataBindingSwipe) {
            Helper.getTextInput(this@MainActivity, "Rename ${item.name}"){ newName ->
                item.name = newName
                item.notifyChanged("Name")
            }
        }

        override fun onBackgroundClicked(item: EmployeeDataBindingSwipe) {
            item.color = Helper.getNextColor(item.color)
            item.notifyChanged("Color")
        }
    }

    private val cardActionListener = object : EmployeeSwipeInCardView.ActionListener {
        override fun onDeleteClicked(item: EmployeeSwipeInCardView) {
            item.showDelete = false
            item.notifyChanged()
            //cardSection.remove(item)
        }

        override fun onRenameClicked(item: EmployeeSwipeInCardView) {
            item.showRename = false
            item.notifyChanged()
            //Helper.getTextInput(this@MainActivity, "Rename ${item.name}"){ newName ->
            //    item.name = newName
            //    item.notifyChanged("Name")
            //}
        }

        override fun onBackgroundClicked(item: EmployeeSwipeInCardView) {
            item.showRecolor = false
            item.notifyChanged()
            //item.color = Helper.getNextColor(item.color)
            //item.notifyChanged("Color")
        }
    }
}
