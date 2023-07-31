package com.kmasan.fenrircodecheck.ui.searchCriteria

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kmasan.fenrircodecheck.model.GPSLogger
import com.kmasan.fenrircodecheck.model.SearchCriteriaRepository

class SearchCriteriaViewModelFactory (private val activity: Activity): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchCriteriaViewModel::class.java)) {
            return SearchCriteriaViewModel(SearchCriteriaRepository(
                GPSLogger(activity)
            )) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}