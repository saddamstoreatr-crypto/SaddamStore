package com.sdstore.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel

// Note: Repository aur Store classes banane ke baad isko update kareinge.
abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {
    // protected val repository: YourRepository
    // protected val store: YourStore

    init {
        // initialize repository and store
    }
}