package com.parrosz.storyu

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.ExperimentalPagingApi
import com.parrosz.storyu.data.repository.StoryRepository
import com.parrosz.storyu.data.repository.UserRepository
import com.parrosz.storyu.di.Injection

class StoryListViewModel(private val storyRepository: StoryRepository, private val userRepository: UserRepository) :
    ViewModel() {
    @ExperimentalPagingApi
    fun getStories(token: String) = storyRepository.getStories(token)

    fun getToken(): LiveData<String> {
        return userRepository.getToken()
    }

    class StoryListViewModelFactory private constructor(
        private val storyRepository: StoryRepository,
        private val userRepository: UserRepository
    ) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StoryListViewModel::class.java)) {
                return StoryListViewModel(storyRepository, userRepository) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

        companion object {
            @Volatile
            private var instance: StoryListViewModelFactory? = null

            fun getInstance(
                context: Context
            ): StoryListViewModelFactory = instance ?: synchronized(this) {
                instance ?: StoryListViewModelFactory(
                    Injection.provideStoryRepository(context),
                    Injection.provideUserRepository(context)
                )
            }.also { instance = it }
        }
    }
}