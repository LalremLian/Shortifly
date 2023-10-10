package com.lazydeveloper.shortifly.data.api

import com.lazydeveloper.shortifly.data.models.SearchResponse
import com.lazydeveloper.shortifly.utils.API_KEY
import com.lazydeveloper.shortifly.utils.DEVICE
import com.lazydeveloper.shortifly.utils.ENGINE
import com.lazydeveloper.shortifly.utils.GL
import com.lazydeveloper.shortifly.utils.HL
import com.lazydeveloper.shortifly.utils.POST_LIST
import com.lazydeveloper.shortifly.utils.SEARCH
import retrofit2.http.*


interface ApiInterface {

//    @GET(POST_LIST)
//    suspend fun getPostList(
//        @HeaderMap headers: Map<String, String> = emptyMap()
//    ): PostListResponse

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

