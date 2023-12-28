package com.parrosz.storyu

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.parrosz.storyu.data.repository.UserRepository
import com.parrosz.storyu.di.Injection

class MainViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    fun checkIfTokenAvailable(): LiveData<String> {
        return userRepository.getToken()
    }

    fun logout() {
        userRepository.deleteToken()
    }

    class MainViewModelFactory private constructor(
        private val userRepository: UserRepository
    ) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(userRepository) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

        companion object {
            @Volatile
            private var instance: MainViewModelFactory? = null

            fun getInstance(
                context: Context
            ): MainViewModelFactory = instance ?: synchronized(this) {
                instance ?: MainViewModelFactory(
                    Injection.provideUserRepository(context)
                )
            }.also { instance = it }
        }
    }
}