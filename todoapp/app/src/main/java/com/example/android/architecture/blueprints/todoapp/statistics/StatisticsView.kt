package com.example.android.architecture.blueprints.todoapp.statistics

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.databinding.StatisticsViewBinding
import com.example.android.architecture.blueprints.todoapp.util.activity
import com.example.android.architecture.blueprints.todoapp.util.obtainViewModel
import com.viewlifecycle.lifecycleOwner

class StatisticsView : CoordinatorLayout, LifecycleObserver {

    private val viewDataBinding: StatisticsViewBinding

    private val statisticsViewModel: StatisticsViewModel

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    init {
        setBackgroundColor(Color.WHITE)
        setTag(R.id.menu_enabled, true)

        val inflater = LayoutInflater.from(context)
        viewDataBinding = StatisticsViewBinding.inflate(inflater, this, true)
        statisticsViewModel = activity.obtainViewModel(StatisticsViewModel::class.java)
        viewDataBinding.stats = statisticsViewModel

        lifecycleOwner.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun setupToolBar() {
        findViewById<Toolbar>(R.id.toolbarStatistics).apply {
            activity.setSupportActionBar(this)
            activity.supportActionBar?.apply {
                setTitle(R.string.statistics_title)
                setHomeAsUpIndicator(R.drawable.ic_menu)
                setDisplayHomeAsUpEnabled(true)
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        statisticsViewModel.start()
    }
}