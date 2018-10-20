package com.example.android.architecture.blueprints.todoapp

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem

interface MenuHandler {

    val hasOptionsMenu: Boolean

    fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater)

    fun onOptionsItemSelected(item: MenuItem): Boolean
}