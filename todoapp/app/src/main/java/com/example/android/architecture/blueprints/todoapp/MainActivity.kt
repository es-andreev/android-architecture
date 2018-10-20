package com.example.android.architecture.blueprints.todoapp

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.ea.viewlifecycle.attachNavigation
import com.ea.viewlifecycle.lifecycleOwner
import com.example.android.architecture.blueprints.todoapp.tasks.TasksView

class MainActivity : AppCompatActivity() {

    private lateinit var mainDispatcher: ViewGroup
    private lateinit var drawerDispatcher: ViewGroup
    private lateinit var drawerLayout: DrawerLayout

    private val backStackListener = object : ViewGroup.OnHierarchyChangeListener {
        override fun onChildViewRemoved(parent: View, child: View) {
            invalidateOptionsMenu()
        }

        override fun onChildViewAdded(parent: View, child: View) {
            child.lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
                @OnLifecycleEvent(Lifecycle.Event.ON_START)
                fun onStart() {
                    invalidateOptionsMenu()
                    child.lifecycleOwner.lifecycle.removeObserver(this)
                }
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_act)

        findViewById<ViewGroup>(R.id.main_dispatcher).apply {
            mainDispatcher = this
            setOnHierarchyChangeListener(backStackListener)
        }
        findViewById<ViewGroup>(R.id.drawer_dispatcher).apply {
            drawerDispatcher = this
            setOnHierarchyChangeListener(backStackListener)
        }

        if (savedInstanceState == null) {
            mainDispatcher.attachNavigation()
            drawerDispatcher.attachNavigation()
            drawerDispatcher.addView(TasksView(this))
        }

        drawerLayout = (findViewById<DrawerLayout>(R.id.drawer_layout)).apply {
            setStatusBarBackground(R.color.colorPrimaryDark)
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
            if (view.lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED) &&
                    view is MenuHandler && view.onOptionsItemSelected(item)) {
                return true
            }
        }
        return false
    }
}