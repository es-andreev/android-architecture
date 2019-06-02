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
import com.ea.viewlifecycle.BackStackNavigator
import com.ea.viewlifecycle.lifecycleOwner
import com.example.android.architecture.blueprints.todoapp.addedittask.AddEditTaskView
import com.example.android.architecture.blueprints.todoapp.statistics.StatisticsView
import com.example.android.architecture.blueprints.todoapp.taskdetail.TaskDetailView
import com.example.android.architecture.blueprints.todoapp.tasks.TasksView

class MainActivity : AppCompatActivity(), MainNavigator {

    private lateinit var drawerDispatcher: ViewGroup
    private lateinit var drawerLayout: DrawerLayout
    override lateinit var navigator: BackStackNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_act)

        drawerDispatcher = findViewById(R.id.drawer_dispatcher)
        navigator = BackStackNavigator(drawerDispatcher, savedInstanceState)

        if (savedInstanceState == null) {
            navigator.navigateForward(TasksView(this))

            when {
                intent.getBooleanExtra(openStatistics, false) -> {
                    navigator.navigateForward(StatisticsView(this))
                }
                intent.hasExtra(TaskDetailView.ARGUMENT_TASK_ID) -> {
                    val taskId = intent.getStringExtra(TaskDetailView.ARGUMENT_TASK_ID)
                    navigator.navigateForward(TaskDetailView.newInstance(this, taskId))
                }
                intent.hasExtra(AddEditTaskView.ARGUMENT_EDIT_TASK_ID) -> {
                    val taskId = intent.getStringExtra(AddEditTaskView.ARGUMENT_EDIT_TASK_ID)
                    navigator.navigateForward(AddEditTaskView.newInstance(this, taskId))
                }
            }
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
                    navigator.navigateBackTo(TasksView::class.java.canonicalName)
                }
                R.id.statistics_navigation_menu_item -> {
                    navigator.navigateForward(StatisticsView(this))
                }
            }
            // Close the navigation drawer when an item is selected.
            menuItem.isChecked = true
            drawerLayout.closeDrawers()
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        inflateMenu(drawerDispatcher, menu)
        return true
    }

    private fun inflateMenu(viewGroup: ViewGroup, menu: Menu) {
        for (i in 0 until viewGroup.childCount) {
            val view = viewGroup.getChildAt(i)
            if (view.lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED) &&
                    view is MenuHandler && view.hasOptionsMenu) {
                view.onCreateOptionsMenu(menu, menuInflater)
            } else {
                invalidateOptionsMenu()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                android.R.id.home -> {
                    if (navigator.backStackItemsCount == 0 ||
                            drawerDispatcher.getChildAt(0).getTag(R.id.menu_enabled) == true ||
                            !navigator.navigateBack()) {
                        drawerLayout.openDrawer(GravityCompat.START)
                    }
                    true
                }
                else -> {
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
        if (!navigator.navigateBack()) {
            super.onBackPressed()
        }
    }

    companion object {
        const val openStatistics = "openStatistics"
    }
}