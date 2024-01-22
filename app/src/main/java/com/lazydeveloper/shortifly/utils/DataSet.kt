package com.lazydeveloper.shortifly.utils

import com.lazydeveloper.shortifly.data.models.Channel
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

    val shortListForPlayerPage = arrayListOf(
        VideoResult(
            1,
            "Big Bunny's Tender",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            "https://img.olympics.com/images/image/private/t_s_w1340/t_s_16_9_g_auto/f_auto/primary/ydk9vatpnihwfquy6zq3",
            "1.2k",
            "2k",
            "5k",
            "2:30"
        ),
        VideoResult(
            2,
            "Elephants are the largest",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
            "https://img.olympics.com/images/image/private/t_s_w1340/t_s_16_9_g_auto/f_auto/primary/mqbvuhctavlxze5hn6nw",
            "500",
            "46",
            "25",
            "2:30"
        ),
        VideoResult(
            3,
            "A forest blaze in Greece",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            "https://img.olympics.com/images/image/private/t_16-9_960/f_auto/v1538355600/primary/lob7w8hmqbqxx2exiw7e",
            "7k",
            "2k",
            "839",
            "2:30"
        ),
        VideoResult(
            4,
            "Big Bunny's Tender",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            "https://img.olympics.com/images/image/private/t_s_w1340/t_s_16_9_g_auto/f_auto/primary/nsyxy18hzgc3noh9sm0i",
            "1.2k",
            "2k",
            "5k",
            "2:30"
        ),
        VideoResult(
            5,
            "Elephants are the largest",
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
            "https://img.olympics.com/images/image/private/t_s_w1340/t_s_16_9_g_auto/f_auto/primary/wbi3tsusw9di7afas9t4",
            "500",
            "46",
            "25",
            "2:30"
        ),
    )

    val channelList = arrayListOf(
        Channel(
            1,
            "Bunny's Tender",
            "https://img.olympics.com/images/image/private/t_s_w1340/t_s_16_9_g_auto/f_auto/primary/ydk9vatpnihwfquy6zq3",
        ),
        Channel(
            2,
            "Big Tender",
            "https://wallpapers.com/images/featured/cool-profile-picture-87h46gcobjl5e4xu.webp",
        ),
        Channel(
            3,
            "Big Bunny",
            "https://wallpapers.com/images/high/cool-profile-picture-1ecoo30f26bkr14o.webp",
        ),
        Channel(
            4,
            "Tender Carlson",
            "https://wallpapers.com/images/high/cool-profile-picture-awled9dwo4qq2yv2.webp",
        ),
        Channel(
            5,
            "Pure Cake",
            "https://wallpapers.com/images/high/cool-profile-picture-ug9oc08q7nn527wu.webp",
        ),
        Channel(
            6,
            "Chookies",
            "https://wallpapers.com/images/high/cool-profile-picture-n4gqyfx775vg4lxx.webp",
        ),
        Channel(
            5,
            "Top Hunter",
            "https://www.finetoshine.com/wp-content/uploads/Image-for-Cute-Girl-Hidden-Face-Profile-Picture-Download-for-Fb-2.jpg",
        ),
        Channel(
            6,
            "Gang Bang",
            "https://wallpapers.com/images/high/cool-profile-picture-q9i2gvcrskxefbe9.webp",
        ),
    )
}