package com.example.android.architecture.blueprints.todoapp.taskdetail

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.Observer
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.view.*
import android.widget.CheckBox
import com.ea.viewlifecycle.arguments
import com.ea.viewlifecycle.lifecycleOwner
import com.example.android.architecture.blueprints.todoapp.MenuHandler
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.SingleLiveEvent
import com.example.android.architecture.blueprints.todoapp.addedittask.AddEditTaskView
import com.example.android.architecture.blueprints.todoapp.databinding.TaskdetailViewBinding
import com.example.android.architecture.blueprints.todoapp.util.*

class TaskDetailView : CoordinatorLayout, LifecycleObserver, MenuHandler, TaskDetailNavigator {

    override val hasOptionsMenu = true

    private var viewDataBinding: TaskdetailViewBinding

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    init {
        setBackgroundColor(Color.WHITE)

        lifecycleOwner.lifecycle.addObserver(this)

        val inflater = LayoutInflater.from(context)
        viewDataBinding = TaskdetailViewBinding.inflate(inflater, this, true).apply {
            viewmodel = activity.obtainViewModel(TaskDetailViewModel::class.java)

            listener = object : TaskDetailUserActionsListener {
                override fun onCompleteChanged(v: View) {
                    viewmodel?.setCompleted((v as CheckBox).isChecked)
                }
            }
        }

        AddEditTaskView.taskSavedEvent.observe(lifecycleOwner, Observer {
            taskSavedEvent.call()
            (parent as ViewGroup).navigateBack()
        })

        setupFab()
        setupToolBar()
        viewDataBinding.viewmodel?.let {
            setupSnackbar(lifecycleOwner, it.snackbarMessage, Snackbar.LENGTH_LONG)
            subscribeToNavigationChanges(it)
        }
    }

    private fun subscribeToNavigationChanges(viewModel: TaskDetailViewModel) {
        // The activity observes the navigation commands in the ViewModel
        viewModel.run {
            editTaskCommand.observe(lifecycleOwner,
                    Observer { onStartEditTask() })
            deleteTaskCommand.observe(lifecycleOwner,
                    Observer { onTaskDeleted() })
        }
    }

    private fun setupFab() {
        findViewById<View>(R.id.fab_edit_task).setOnClickListener {
            viewDataBinding.viewmodel?.editTask()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun setupToolBar() {
        findViewById<Toolbar>(R.id.toolbar).apply {
            activity.setSupportActionBar(this)
            activity.supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        viewDataBinding.viewmodel?.start(arguments?.getString(ARGUMENT_TASK_ID))
    }

    override fun onTaskDeleted() {
        // If the task was deleted successfully, go back to the list.
        taskDeletedEvent.call()
        (parent as ViewGroup).navigateBack()
    }

    override fun onStartEditTask() {
        val taskId = arguments?.getString(ARGUMENT_TASK_ID)
        taskId?.apply {
            activity.navigateForward(AddEditTaskView.newInstance(activity, this))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.taskdetail_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_delete -> {
                viewDataBinding.viewmodel?.deleteTask()
                return true
            }
            else -> return false
        }
    }

    companion object {
        private const val ARGUMENT_TASK_ID = "TASK_ID"
        private const val REQUEST_EDIT_TASK = 1

        val taskDeletedEvent = SingleLiveEvent<Unit>()
        val taskSavedEvent = SingleLiveEvent<Unit>()

        fun newInstance(context: Context, taskId: String) = TaskDetailView(context).apply {
            arguments = Bundle().apply {
                putString(ARGUMENT_TASK_ID, taskId)
            }
        }
    }
}