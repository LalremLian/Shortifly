package com.lazydeveloper.shortifly.data.models

data class SearchResponse(
    val search_metadata: SearchMetadata,
    val search_parameters: SearchParameters,
    val search_information: SearchInformation,
    val video_results: List<VideoResult>
)

data class SearchMetadata(
    val id: String,
    val status: String,
    val json_endpoint: String,
    val created_at: String,
    val processed_at: String,
    val google_videos_url: String,
    val raw_html_file: String,
    val total_time_taken: Double
)

data class SearchParameters(
    val engine: String,
    val q: String,
    val google_domain: String,
    val hl: String,
    val gl: String,
    val device: String
)

data class SearchInformation(
    val query_displayed: String,
    val spelling_fix: String,
    // Add other properties here
)

data class VideoResult(
    val position: Int,
    val title: String,
    val link: String,
    val thumbnail: String,
    val date: String,
    val source: String,
    val author: String,
    val duration: String
)
