package com.example.android.architecture.blueprints.todoapp

import android.arch.lifecycle.Lifecycle
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import com.ea.viewlifecycle.attachNavigation
import com.ea.viewlifecycle.lifecycleOwner
import com.example.android.architecture.blueprints.todoapp.statistics.StatisticsView
import com.example.android.architecture.blueprints.todoapp.tasks.TasksView
import com.example.android.architecture.blueprints.todoapp.util.navigateBack

class MainActivity : AppCompatActivity() {

    private lateinit var mainDispatcher: ViewGroup
    private lateinit var drawerDispatcher: ViewGroup
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_act)

        mainDispatcher = findViewById(R.id.main_dispatcher)
        drawerDispatcher = findViewById(R.id.drawer_dispatcher)

        if (savedInstanceState == null) {
            mainDispatcher.attachNavigation()
            drawerDispatcher.attachNavigation()
            drawerDispatcher.addView(TasksView(this))
        }

        drawerLayout = (findViewById<DrawerLayout>(R.id.drawer_layout)).apply {
            setStatusBarBackground(R.color.colorPrimaryDark)
        }
        setupDrawerContent(findViewById(R.id.nav_view))
    }

    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.list_navigation_menu_item -> {
                    // remove all views except the first one
                    drawerDispatcher.removeViews(1, drawerDispatcher.childCount - 1)
                }
                R.id.statistics_navigation_menu_item -> {
                    if (drawerDispatcher.getChildAt(drawerDispatcher.childCount - 1) !is StatisticsView) {
                        drawerDispatcher.addView(StatisticsView(this))
                    }
                }
            }
            // Close the navigation drawer when an item is selected.
            menuItem.isChecked = true
            drawerLayout.closeDrawers()
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        inflateMenu(mainDispatcher, menu)
        inflateMenu(drawerDispatcher, menu)
        return true
    }

    private fun inflateMenu(viewGroup: ViewGroup, menu: Menu) {
        for (i in 0 until viewGroup.childCount) {
            val view = viewGroup.getChildAt(i)
            if (view.lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED) &&
                    view is MenuHandler && view.hasOptionsMenu) {
                view.onCreateOptionsMenu(menu, menuInflater)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                android.R.id.home -> {
                    // Open the navigation drawer when the home icon is selected from the toolbar.
                    drawerLayout.openDrawer(GravityCompat.START)
                    true
                }
                else -> {
                    dispatchMenuItemSelected(mainDispatcher, item) ||
                            dispatchMenuItemSelected(drawerDispatcher, item) ||
                            super.onOptionsItemSelected(item)
                }
            }

    private fun dispatchMenuItemSelected(viewGroup: ViewGroup, item: MenuItem): Boolean {
        for (i in 0 until viewGroup.childCount) {
            val view = viewGroup.getChildAt(i)
            if (view.lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED) &&
                    view is MenuHandler && view.onOptionsItemSelected(item)) {
                return true
            }
        }
        return false
    }

    override fun onBackPressed() {
        if (!mainDispatcher.navigateBack() && !drawerDispatcher.navigateBack()) {
            super.onBackPressed()
        }
    }
}