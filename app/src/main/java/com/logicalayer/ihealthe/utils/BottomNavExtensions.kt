package com.logicalayer.ihealthe.utils

import android.app.Activity
import android.content.Intent
import com.logicalayer.ihealthe.Activity.AboutActivity
import com.logicalayer.ihealthe.Activity.MainActivity
import com.logicalayer.ihealthe.Activity.RandomRecipesActivity
import com.logicalayer.ihealthe.Activity.SearchActivity
import com.logicalayer.ihealthe.Activity.ShoppingActivity
import com.logicalayer.ihealthe.R
import com.google.android.material.bottomnavigation.BottomNavigationView

fun BottomNavigationView.setupNavigation(currentItemId: Int, activity: Activity) {
    selectedItemId = currentItemId

    val destinations = mapOf(
        R.id.nav_home to MainActivity::class.java,
        R.id.nav_search to SearchActivity::class.java,
        R.id.nav_random to RandomRecipesActivity::class.java,
        R.id.nav_about to AboutActivity::class.java,
        R.id.nav_shopping to ShoppingActivity::class.java
    )

    setOnItemSelectedListener { item ->
        val destination = destinations[item.itemId]
        if (destination != null && activity::class.java != destination) {
            val intent = Intent(activity, destination).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            activity.startActivity(intent)
        }
        true
    }
}
