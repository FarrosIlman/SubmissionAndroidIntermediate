package com.parrosz.storyu.posting

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.parrosz.storyu.data.repository.StoryRepository
import com.parrosz.storyu.data.repository.UserRepository
import com.parrosz.storyu.di.Injection
import java.io.File

class PostingViewModel (
    private val storyRepository: StoryRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    fun postStory(
        token: String,
        imageFile: File,
        description: String,
        lat: Float? = null,
        lon: Float? = null
    ) =
        storyRepository.postStory(token, imageFile, description, lat, lon)

    fun getToken(): LiveData<String> {
        return userRepository.getToken()
    }

    class PostingViewModelFactory private constructor(
        private val storyRepository: StoryRepository,
        private val userRepository: UserRepository
    ) :
        ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PostingViewModel::class.java)) {
                return PostingViewModel(storyRepository, userRepository) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

        companion object {
            @Volatile
            private var instance: PostingViewModelFactory? = null

            fun getInstance(
                context: Context
            ): PostingViewModelFactory =
                instance ?: synchronized(this) {
                    instance ?: PostingViewModelFactory(
                        Injection.provideStoryRepository(context),
                        Injection.provideUserRepository(context)
                    )
                }.also { instance = it }
        }
    }
}