package com.lazydeveloper.shortifly.network

import com.lazydeveloper.shortifly.model.SearchResponse
import com.lazydeveloper.shortifly.network.model.PostListResponse
import retrofit2.http.*


interface ApiInterface {

    @GET(POST_LIST)
    suspend fun getPostList(
        @HeaderMap headers: Map<String, String> = emptyMap()
    ): PostListResponse

    @GET(SEARCH)
    suspend fun getSearchDataList(
//        @HeaderMap headers: Map<String, String> = emptyMap(),
        @Query("q") query: String,
        @Query("engine") engine: String = ENGINE,
        @Query("device") device: String = DEVICE,
        @Query("hl") hl: String = HL,
        @Query("gl") gl: String = GL,
        @Query("api_key") apiKey: String = API_KEY
    ): SearchResponse

}

