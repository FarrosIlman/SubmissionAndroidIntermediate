package com.parrosz.storyu

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.parrosz.storyu.data.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import androidx.lifecycle.Observer
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRules = MainCoroutineRule()

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var mainViewModel: MainViewModel

    @Before
    fun setUp() {
        mainViewModel = MainViewModel(userRepository)
    }

    @Test
    fun `When Get Token Should Not Null And Should Return String`() {
        val observer = Observer<String> {}

        try {
            val dummyToken = DummyData.generateDummyLoginResponse().loginResult.token

            val expectedToken = MutableLiveData<String>()
            expectedToken.value = dummyToken

            `when`(mainViewModel.checkIfTokenAvailable()).thenReturn(expectedToken)

            val actualToken = mainViewModel.checkIfTokenAvailable().getOrAwaitValue()

            verify(userRepository).getToken()
            assertNotNull(actualToken)
            assertEquals(dummyToken, actualToken)
        } finally {
            mainViewModel.checkIfTokenAvailable().removeObserver(observer)
        }
    }

    @Test
    fun `When logout Should Remove Token`() {
        doNothing().`when`(userRepository).deleteToken()
        mainViewModel.logout()
        verify(userRepository).deleteToken()
    }
}
