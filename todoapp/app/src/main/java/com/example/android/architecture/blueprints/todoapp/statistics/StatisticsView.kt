package com.example.android.architecture.blueprints.todoapp.statistics

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.graphics.Color
import android.support.design.widget.CoordinatorLayout
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.view.LayoutInflater
import com.ea.viewlifecycle.lifecycleOwner
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.databinding.StatisticsViewBinding
import com.example.android.architecture.blueprints.todoapp.util.activity
import com.example.android.architecture.blueprints.todoapp.util.obtainViewModel

class StatisticsView : CoordinatorLayout, LifecycleObserver {

    private val viewDataBinding: StatisticsViewBinding

    private val statisticsViewModel: StatisticsViewModel

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    init {
        setBackgroundColor(Color.WHITE)

        val inflater = LayoutInflater.from(context)
        viewDataBinding = StatisticsViewBinding.inflate(inflater, this, true)
        statisticsViewModel = activity.obtainViewModel(StatisticsViewModel::class.java)
        viewDataBinding.stats = statisticsViewModel

        lifecycleOwner.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun setupToolBar() {
        findViewById<Toolbar>(R.id.toolbar).apply {
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