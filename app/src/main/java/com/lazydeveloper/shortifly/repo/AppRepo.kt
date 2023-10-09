package com.lazydeveloper.shortifly.repo

import com.lazydeveloper.shortifly.network.ApiInterface
import javax.inject.Inject

class AppRepo @Inject constructor(
    private val apiHitter: ApiInterface
) {
    suspend fun getPostList() = apiHitter.getPostList()

    suspend fun getSearchList() = apiHitter.getSearchDataList("football")
}