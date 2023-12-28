package com.parrosz.storyu

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.parrosz.storyu.data.repository.StoryRepository
import com.parrosz.storyu.data.repository.UserRepository
import com.parrosz.storyu.di.Injection

class StoryMapsViewModel(
    private val storyRepository: StoryRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    fun getStories(token: String) = storyRepository.getStoriesWithLocation(token)

    fun getToken(): LiveData<String> {
        return userRepository.getToken()
    }

    class StoryMapViewModelFactory private constructor(
        private val storyRepository: StoryRepository,
        private val userRepository: UserRepository
    ) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StoryMapsViewModel::class.java)) {
                return StoryMapsViewModel(storyRepository, userRepository) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

        companion object {
            @Volatile
            private var instance: StoryMapViewModelFactory? = null

            fun getInstance(
                context: Context
            ): StoryMapViewModelFactory = instance ?: synchronized(this) {
                instance ?: StoryMapViewModelFactory(
                    Injection.provideStoryRepository(context),
                    Injection.provideUserRepository(context)
                )
            }.also { instance = it }
        }
    }
}