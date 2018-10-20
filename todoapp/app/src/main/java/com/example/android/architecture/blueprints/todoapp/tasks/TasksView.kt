package com.example.android.architecture.blueprints.todoapp.tasks

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.util.Log
import android.view.*
import com.ea.viewlifecycle.lifecycleOwner
import com.example.android.architecture.blueprints.todoapp.MenuHandler
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.databinding.TasksViewBinding
import com.example.android.architecture.blueprints.todoapp.util.activity
import com.example.android.architecture.blueprints.todoapp.util.obtainViewModel
import com.example.android.architecture.blueprints.todoapp.util.setupSnackbar
import java.util.*

class TasksView : CoordinatorLayout, LifecycleObserver, MenuHandler {

    override val hasOptionsMenu = true

    private var viewDataBinding: TasksViewBinding
    private lateinit var listAdapter: TasksAdapter

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    init {
        val inflater = LayoutInflater.from(context)
        viewDataBinding = TasksViewBinding.inflate(inflater, this, true).apply {
            viewmodel = activity.obtainViewModel(TasksViewModel::class.java)
        }

        lifecycleOwner.lifecycle.addObserver(this)

        viewDataBinding.viewmodel?.let {
            setupSnackbar(lifecycleOwner, it.snackbarMessage, Snackbar.LENGTH_LONG)
        }
        setupToolBar()
        setupFab()
        setupListAdapter()
        setupRefreshLayout()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tasks_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                R.id.menu_clear -> {
                    viewDataBinding.viewmodel?.clearCompletedTasks()
                    true
                }
                R.id.menu_filter -> {
                    showFilteringPopUpMenu()
                    true
                }
                R.id.menu_refresh -> {
                    viewDataBinding.viewmodel?.loadTasks(true)
                    true
                }
                else -> false
            }

    private fun setupToolBar() {
        findViewById<Toolbar>(R.id.toolbar).apply {
            activity.setSupportActionBar(this)
            activity.supportActionBar?.apply {
                setHomeAsUpIndicator(R.drawable.ic_menu)
                setDisplayHomeAsUpEnabled(true)
            }
        }
    }

    private fun showFilteringPopUpMenu() {
        PopupMenu(context, activity.findViewById<View>(R.id.menu_filter)).run {
            menuInflater.inflate(R.menu.filter_tasks, menu)

            setOnMenuItemClickListener {
                viewDataBinding.viewmodel?.run {
                    currentFiltering =
                            when (it.itemId) {
                                R.id.active -> TasksFilterType.ACTIVE_TASKS
                                R.id.completed -> TasksFilterType.COMPLETED_TASKS
                                else -> TasksFilterType.ALL_TASKS
                            }
                    loadTasks(false)
                }
                true
            }
            show()
        }
    }

    private fun setupFab() {
        findViewById<FloatingActionButton>(R.id.fab_add_task).run {
            setImageResource(R.drawable.ic_add)
            setOnClickListener {
                viewDataBinding.viewmodel?.addNewTask()
            }
        }
    }

    private fun setupListAdapter() {
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            listAdapter = TasksAdapter(ArrayList(0), viewModel)
            viewDataBinding.tasksList.adapter = listAdapter
        } else {
            Log.w(TAG, "ViewModel not initialized when attempting to set up adapter.")
        }
    }

    private fun setupRefreshLayout() {
        viewDataBinding.refreshLayout.run {
            setColorSchemeColors(
                    ContextCompat.getColor(activity, R.color.colorPrimary),
                    ContextCompat.getColor(activity, R.color.colorAccent),
                    ContextCompat.getColor(activity, R.color.colorPrimaryDark)
            )
            // Set the scrolling view in the custom SwipeRefreshLayout.
            scrollUpChild = viewDataBinding.tasksList
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        viewDataBinding.viewmodel?.start()
    }

    companion object {
        private const val TAG = "TasksView"
    }
}