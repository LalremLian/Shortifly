package com.lazydeveloper.shortifly.network.model

import com.google.gson.annotations.SerializedName

class PostListResponse : ArrayList<PostListResponse.PostListResponseElement>(){
    data class PostListResponseElement (
        @SerializedName("userId")
        val userID: Int? = null,
        @SerializedName("id")
        val id: Int? = null,
        @SerializedName("title")
        val title: String? = null,
        @SerializedName("body")
        val body: String? = null
    )
}

