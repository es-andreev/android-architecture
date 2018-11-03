/*
 * Copyright 2017, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.architecture.blueprints.todoapp.util

/**
 * Extension functions for View and subclasses of View.
 */

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.content.ContextWrapper
import android.databinding.BindingAdapter
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import com.example.android.architecture.blueprints.todoapp.ScrollChildSwipeRefreshLayout
import com.example.android.architecture.blueprints.todoapp.SingleLiveEvent
import com.example.android.architecture.blueprints.todoapp.tasks.TasksViewModel

/**
 * Transforms static java function Snackbar.make() to an extension function on View.
 */
fun View.showSnackbar(snackbarText: String, timeLength: Int) {
    Snackbar.make(this, snackbarText, timeLength).show()
}

/**
 * Triggers a snackbar message when the value contained by snackbarTaskMessageLiveEvent is modified.
 */
fun View.setupSnackbar(lifecycleOwner: LifecycleOwner,
        snackbarMessageLiveEvent: SingleLiveEvent<Int>, timeLength: Int) {
    snackbarMessageLiveEvent.observe(lifecycleOwner, Observer {
        it?.let { showSnackbar(context.getString(it), timeLength) }
    })
}

/**
 * Reloads the data when the pull-to-refresh is triggered.
 *
 * Creates the `android:onRefresh` for a [SwipeRefreshLayout].
 */
@BindingAdapter("android:onRefresh")
fun ScrollChildSwipeRefreshLayout.setSwipeRefreshLayoutOnRefreshListener(
        viewModel: TasksViewModel) {
    setOnRefreshListener { viewModel.loadTasks(true) }
}

internal val View.activity: AppCompatActivity
    get() {
        var c = context
        while (c !is AppCompatActivity && c is ContextWrapper) {
            c = c.baseContext
        }

        return c as? AppCompatActivity
                ?: throw IllegalStateException("Could not find AppCompatActivity for $this.")
    }

fun ViewGroup.navigateBack(): Boolean {
    if (childCount > 0) {
        removeViewAt(childCount - 1)
        return true
    }
    return false
}

fun ViewGroup.navigateForward(view: View) {
    val topView = getChildAt(childCount - 1)
    if (topView == null || topView::class.java != view::class.java) {
        addView(view)
    }
}