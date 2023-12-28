package com.parrosz.storyu

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.parrosz.storyu.adapter.StoriesAdapter
import com.parrosz.storyu.data.remote.responses.StoryEntity
import com.parrosz.storyu.data.repository.StoryRepository
import com.parrosz.storyu.data.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryListViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRules = MainCoroutineRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    @Mock
    private lateinit var userRepository: UserRepository
    private lateinit var viewModel: StoryListViewModel

    @Before
    fun setUp() {
        viewModel = StoryListViewModel(storyRepository, userRepository)
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `When Get Stories Should Not Null`() = mainCoroutineRules.runBlockingTest {
        val dummyToken = DummyData.generateDummyLoginResponse().loginResult.token
        val dummyStories = DummyData.generateDummyStoryEntityList()
        val data = PagingData.from(dummyStories)
        val stories = MutableLiveData<PagingData<StoryEntity>>()
        stories.value = data

        Mockito.`when`(storyRepository.getStories(dummyToken)).thenReturn(stories)

        val actualStories = viewModel.getStories(dummyToken).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoriesAdapter.StoryDiffCallback,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = mainCoroutineRules.dispatcher,
            workerDispatcher = mainCoroutineRules.dispatcher
        )

        differ.submitData(actualStories)
        advanceUntilIdle()
        Mockito.verify(storyRepository).getStories(dummyToken)
        assertNotNull(differ.snapshot())
        assertEquals(dummyStories.size, differ.snapshot().size)
        assertEquals(dummyStories[0], differ.snapshot()[0])
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `When Get Stories With No Data Should Return Empty List`() = mainCoroutineRules.runBlockingTest {
        val dummyToken = DummyData.generateDummyLoginResponse().loginResult.token
        val emptyStories = emptyList<StoryEntity>()
        val data = PagingData.from(emptyStories)
        val stories = MutableLiveData<PagingData<StoryEntity>>()
        stories.value = data

        Mockito.`when`(storyRepository.getStories(dummyToken)).thenReturn(stories)

        val actualStories = viewModel.getStories(dummyToken).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoriesAdapter.StoryDiffCallback,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = mainCoroutineRules.dispatcher,
            workerDispatcher = mainCoroutineRules.dispatcher
        )

        differ.submitData(actualStories)
        advanceUntilIdle()

        Mockito.verify(storyRepository).getStories(dummyToken)
        assertNotNull(differ.snapshot())
        assertTrue(differ.snapshot().isEmpty())
    }

    @Test
    fun `When Get Token Should Not Null And Should Return String`() {
        val observer = Observer<String> {}
        try {
            val dummyToken = DummyData.generateDummyLoginResponse().loginResult.token
            val expectedToken = MutableLiveData<String>()
            expectedToken.value = dummyToken
            Mockito.`when`(viewModel.getToken()).thenReturn(expectedToken)

            val actualToken = viewModel.getToken().getOrAwaitValue()
            Mockito.verify(userRepository).getToken()
            assertNotNull(actualToken)
            assertEquals(dummyToken, actualToken)
        } finally {
            viewModel.getToken().removeObserver(observer)
        }
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}
