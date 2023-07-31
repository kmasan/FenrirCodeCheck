package com.kmasan.fenrircodecheck.ui.searchResult

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kmasan.fenrircodecheck.MainApplication
import com.kmasan.fenrircodecheck.R
import com.kmasan.fenrircodecheck.model.GourmetSearchAPI
import com.kmasan.fenrircodecheck.model.SearchResultRepository

class SearchResultViewModelFactory (private val context: Context)  : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchResultViewModel::class.java)) {
            return SearchResultViewModel(
                SearchResultRepository(GourmetSearchAPI(context.getString(R.string.api_token)))
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}