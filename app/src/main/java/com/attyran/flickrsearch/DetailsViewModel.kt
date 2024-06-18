package com.attyran.flickrsearch

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.attyran.flickrsearch.network.BackendService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val backendService: BackendService
) : ViewModel() {

    var list = arrayListOf<Int>()
    val listFlow = MutableStateFlow(list)
    val map = hashMapOf<Int, Int>()
    var lastVisible = 0

    init {
        for (i in 0 until 200) {
            list.add(0)
        }
        viewModelScope.launch {
            count().collect {
                val newList = list.map { it + 1 }.toMutableList()
                list = newList as ArrayList<Int>
                listFlow.value = newList
            }
        }
    }

    fun count() = flow {
        var counter = 0
        while (true) {
            emit(counter++)
            delay(1000)
        }
    }

    fun updateLastVisible(new: Int) {
        lastVisible = new
        for (i in 0 until lastVisible) {
            map[i] = map.getOrDefault(i, 0) + 1
        }
    }

    fun getFavorites() {
        viewModelScope.launch {
            try {
                val result = backendService.getFavorites()
                Log.d("test1", "test1")
            } catch (e: Exception) {
                Log.e("TEST", "error")
            }
        }
    }
}