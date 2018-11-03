package com.example.android.architecture.blueprints.todoapp

import android.view.View

interface MainNavigator {

    fun navigateForward(view: View)

    fun navigateForwardWithMenu(view: View)
}