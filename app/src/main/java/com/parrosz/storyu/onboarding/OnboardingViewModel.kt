package com.parrosz.storyu.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.parrosz.storyu.data.local.datastore.LoginPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OnboardingViewModel (private val loginPreferences: LoginPreferences) : ViewModel() {

    fun setFirstTime(firstTime: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            loginPreferences.setFirstTime(firstTime)
        }
    }

    class OnboardingViewModelFactory private constructor(private val loginPreferences: LoginPreferences) :
        ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(OnboardingViewModel::class.java)) {
                return OnboardingViewModel(loginPreferences) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

        companion object {
            @Volatile
            private var instance: OnboardingViewModelFactory? = null

            fun getInstance(loginPreferences: LoginPreferences): OnboardingViewModelFactory =
                instance ?: synchronized(this) {
                    instance ?: OnboardingViewModelFactory(loginPreferences)
                }.also { instance = it }
        }
    }
}