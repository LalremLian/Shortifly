package com.lazydeveloper.shortifly.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.lazydeveloper.shortifly.coroutine.Resource
import com.lazydeveloper.shortifly.model.SearchResponse
import com.lazydeveloper.shortifly.repo.AppRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@HiltViewModel
class HomeFragViewModel @Inject constructor(
    private val appRepo: AppRepo
): ViewModel() {
    //--------------------------------------------------LiveData
    fun getPostList() = liveData {
        try {
            emit(Resource.Loading)
            val posts = appRepo.getSearchList()
            emit(Resource.Success(posts))
        } catch (exp: Exception) {
            emit(Resource.Error(exp.message))
        }
    }

    //--------------------------------------------------Flow
    fun getFlowData(): Flow<Resource<SearchResponse>> = flow {
        val headers = mapOf(
            "Authorization" to "Bearer YOUR_ACCESS_TOKEN",
            "Custom-Header" to "Custom-Value"
        )
        try {
            emit(Resource.Loading)
            val posts = appRepo.getSearchList()
            emit(Resource.Success(posts))
        } catch (exp: Exception) {
            emit(Resource.Error(exp.message))
        }
    }.flowOn(Dispatchers.IO)
}