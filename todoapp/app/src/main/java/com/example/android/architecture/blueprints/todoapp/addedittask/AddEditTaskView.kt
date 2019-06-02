package com.example.android.architecture.blueprints.todoapp.addedittask

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.Observer
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.view.LayoutInflater
import com.ea.viewlifecycle.arguments
import com.ea.viewlifecycle.lifecycleOwner
import com.ea.viewlifecycle.viewModelProvider
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.SingleLiveEvent
import com.example.android.architecture.blueprints.todoapp.ViewModelFactory
import com.example.android.architecture.blueprints.todoapp.databinding.AddtaskViewBinding
import com.example.android.architecture.blueprints.todoapp.util.activity
import com.example.android.architecture.blueprints.todoapp.util.navigator
import com.example.android.architecture.blueprints.todoapp.util.setupSnackbar

class AddEditTaskView : CoordinatorLayout, LifecycleObserver, AddEditTaskNavigator {

    private var viewDataBinding: AddtaskViewBinding

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    init {
        setBackgroundColor(Color.WHITE)

        lifecycleOwner.lifecycle.addObserver(this)

        val inflater = LayoutInflater.from(context)
        viewDataBinding = AddtaskViewBinding.inflate(inflater, this, true)

        setupFab()
        setupToolBar()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun onCreate() {
        viewDataBinding.viewmodel = viewModelProvider(ViewModelFactory.getInstance(activity.application))
                .get(AddEditTaskViewModel::class.java)
                .apply {
                    setupSnackbar(lifecycleOwner, snackbarMessage, Snackbar.LENGTH_LONG)
                }

        subscribeToNavigationChanges()
        loadData()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun setupToolBar() {
        findViewById<Toolbar>(R.id.toolbarAddEditTask).apply {
            activity.setSupportActionBar(this)
            activity.supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)

                setTitle(
                        if (this@AddEditTaskView.arguments?.getString(ARGUMENT_EDIT_TASK_ID) != null)
                            R.string.edit_task
                        else
                            R.string.add_task
                )
            }
        }
    }

    private fun setupFab() {
        findViewById<FloatingActionButton>(R.id.fab_edit_task_done).apply {
            setImageResource(R.drawable.ic_done)
            setOnClickListener { viewDataBinding.viewmodel?.saveTask() }
        }
    }

    private fun loadData() {
        // Add or edit an existing task?
        viewDataBinding.viewmodel?.start(arguments?.getString(ARGUMENT_EDIT_TASK_ID))
    }

    private fun subscribeToNavigationChanges() {
        // The activity observes the navigation events in the ViewModel
        viewDataBinding.viewmodel?.taskUpdatedEvent?.observe(lifecycleOwner, Observer {
            onTaskSaved()
        })
    }

    override fun onTaskSaved() {
        taskSavedEvent.postValue(null)
        activity.navigator.navigateBack()
    }

    companion object {
        const val ARGUMENT_EDIT_TASK_ID = "EDIT_TASK_ID"
        const val REQUEST_CODE = 1

        val taskSavedEvent = SingleLiveEvent<Unit>()

        fun newInstance(context: Context, taskId: String? = null) = AddEditTaskView(context).apply {
            id = R.id.add_edit_task_view
            arguments = Bundle().apply {
                putString(ARGUMENT_EDIT_TASK_ID, taskId)
            }
        }
    }
}