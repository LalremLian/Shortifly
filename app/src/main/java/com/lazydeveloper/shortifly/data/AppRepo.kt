package com.lazydeveloper.shortifly.data

import com.lazydeveloper.shortifly.data.api.ApiInterface
import javax.inject.Inject

class AppRepo @Inject constructor(
    private val apiHitter: ApiInterface
) {
//    suspend fun getPostList() = apiHitter.getPostList()

    suspend fun getSearchList() = apiHitter.getSearchDataList("football")
}