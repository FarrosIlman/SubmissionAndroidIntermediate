package com.parrosz.storyu

import com.parrosz.storyu.data.remote.responses.LoginResponse
import com.parrosz.storyu.data.remote.responses.LoginResult
import com.parrosz.storyu.data.remote.responses.StoryEntity
import java.io.File


object DummyData {
    fun generateDummyStoryEntityList(): List<StoryEntity> {
        val storyList = ArrayList<StoryEntity>()
        for (i in 1..10) {
            val story = StoryEntity(
                "https://pbs.twimg.com/profile_images/1455185376876826625/s1AjSxph_400x400.jpg",
                "2022-04-21T06:41:06.470Z",
                "User $i",
                "Description of post $i",
                100.0 + i*2,
                "story-$i",
                100.0 + i*2
            )

            storyList.add(story)
        }

        return storyList
    }

    fun generateDummyLoginResponse(): LoginResponse {
        return LoginResponse(
            LoginResult("12345"),
            false,
            "success"
        )
    }
}