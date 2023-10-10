package com.lazydeveloper.shortifly.utils

import com.lazydeveloper.shortifly.data.models.Video
import com.lazydeveloper.shortifly.data.models.VideoResult

object DataSet {
    val shortsList = arrayListOf(
        Video(
            "@Calvert",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            "Big Bunny's Tender",
            "Big Bunny's Tender Website Big Bunny- Flash Animation. YUMMY EPISODES",
            "1.2k",
            "2k",
            "5k"
        ),
        Video(
            "@LongLostFriend",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
            "Elephants are the largest",
            "Elephants are the largest living land animals.",
            "500",
            "46",
            "25"
        ),
        Video(
            "@JungleBook45",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            "A forest blaze in Greece",
            "A forest blaze in Greece is \"the largest wildfire ever recorded in the EU.",
            "7k",
            "2k",
            "839"
        ),
    )

    val shortList = arrayListOf(
        VideoResult(
            1,
            "Big Bunny's Tender",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            "https://serpapi.com/searches/6524f72c0574f5576c140df1/images/d51a43776fc0f3fde522a7f42294dde37c23614794605aedbce5b35aaefba8c8.jpeg",
            "1.2k",
            "2k",
            "5k",
            "2:30"
        ),
        VideoResult(
            2,
            "Elephants are the largest",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
            "https://serpapi.com/searches/6524f72c0574f5576c140df1/images/d51a43776fc0f3fde5b69869fc73bcbba2619a393ec30926627fb30acc32e91c.jpeg",
            "500",
            "46",
            "25",
            "2:30"
        ),
        VideoResult(
            3,
            "A forest blaze in Greece",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            "https://serpapi.com/searches/6524f72c0574f5576c140df1/images/d51a43776fc0f3fd2f4146fba4b7b83f51694d9d7695dbed9c7a9b13707f4755.jpeg",
            "7k",
            "2k",
            "839",
            "2:30"
        ),
        VideoResult(
            4,
            "Big Bunny's Tender",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            "https://serpapi.com/searches/6524f72c0574f5576c140df1/images/d51a43776fc0f3fd4b26353e9d1b8e6d24b86020609796dc29b1894018f5a6eb.jpeg",
            "1.2k",
            "2k",
            "5k",
            "2:30"
        ),
        VideoResult(
            5,
            "Elephants are the largest",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
            "https://serpapi.com/searches/6524f72c0574f5576c140df1/images/d51a43776fc0f3fd6bfb5b4a1b59c5ff06551a06c512eed28bf6291891623d8b.jpeg",
            "500",
            "46",
            "25",
            "2:30"
        ),
    )
}