package com.brandonkitt.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var noSwipeSection: Section
    private lateinit var swipeSection: Section
    private lateinit var cardSection: Section

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val section = Section()
        noSwipeSection = Section()
        swipeSection = Section()
        cardSection = Section()
        section.addAll(listOf(noSwipeSection, cardSection, swipeSection))

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
        cardSection.addAll(cardItems)

        swipe_recycler_view.layoutManager = LinearLayoutManager(this)
        swipe_recycler_view.adapter = GroupAdapter<ViewHolder>().apply {
            add(section)
        }
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

    private val cardActionListener = object : EmployeeSwipeInCardView.ActionListener {
        override fun onDeleteClicked(item: EmployeeSwipeInCardView) {
            cardSection.remove(item)
        }

        override fun onRenameClicked(item: EmployeeSwipeInCardView) {
            Helper.getTextInput(this@MainActivity, "Rename ${item.name}"){ newName ->
                item.name = newName
                item.notifyChanged("Name")
            }
        }

        override fun onBackgroundClicked(item: EmployeeSwipeInCardView) {
            item.color = Helper.getNextColor(item.color)
            item.notifyChanged("Color")
        }
    }
}
