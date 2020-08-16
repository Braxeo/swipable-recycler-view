package com.brandonkitt.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val section = Section()
        var items = mutableListOf<Item<ViewHolder>>()
        items = mutableListOf(
            EmployeeNoSwipe(
                name = "James",
                color = android.R.color.holo_blue_light
            ),
            EmployeeSwipe(
                name = "Tim",
                color = android.R.color.holo_orange_light,
                actionListener = object : EmployeeSwipe.ActionListener {
                    override fun onDeleteClicked(item: EmployeeSwipe) {
                        items.remove(item)
                        section.notifyChanged()
                    }

                    override fun onRenameClicked(item: EmployeeSwipe) {
                        Helper.getTextInput(this@MainActivity, "Rename ${item.name}"){ newName ->
                            item.name = newName
                            section.notifyChanged()
                        }
                    }

                    override fun onBackgroundClicked(item: EmployeeSwipe) {
                        item.color = Helper.getNextColor(item.color)
                        section.notifyChanged()
                    }
                }
            ),
            EmployeeNoSwipe(
                name = "Sarah",
                color = android.R.color.holo_orange_light
            )
        )
        section.addAll(items)

        swipe_recycler_view.layoutManager = LinearLayoutManager(this)
        swipe_recycler_view.adapter = GroupAdapter<ViewHolder>().apply {
            add(section)
        }
    }
}
