package com.parrosz.storyu.data.local.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.parrosz.storyu.data.local.entity.StoryRemoteKey
import com.parrosz.storyu.data.local.room.StoryDatabase
import com.parrosz.storyu.data.remote.responses.StoryEntity
import com.parrosz.storyu.data.remote.retrofit.ApiService
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers


@OptIn(ExperimentalPagingApi::class)
class StoryRemotePaging(
    private val token: String,
    private val apiService: ApiService,
    private val database: StoryDatabase
) : RemoteMediator<Int, StoryEntity>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryEntity>
    ): MediatorResult {
        return try{
            withContext(Dispatchers.IO) {
                val page = when (loadType) {
                    LoadType.REFRESH -> {
                        val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                        remoteKeys?.nextPage?.minus(1) ?: INITIAL_PAGE_INDEX
                    }
                    LoadType.APPEND -> {
                        val remoteKeys = getRemoteKeyForLastItem(state)
                        remoteKeys?.nextPage
                            ?: return@withContext MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    }
                    LoadType.PREPEND -> {
                        val remoteKeys = getRemoteKeyForFirstItem(state)
                        remoteKeys?.prevPage
                            ?: return@withContext MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    }
                }

                val responseData = apiService.getStories(token, page, state.config.pageSize).story
                val endOfPaginationReached = responseData.isEmpty()

                database.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        database.storyRemoteKeyDao().deleteRemoteKeys()
                        database.storyDao().deleteAllStories()
                    }

                    val prevPage = if (page == 1) null else page - 1
                    val nextPage = if (endOfPaginationReached) null else page + 1
                    val keys = responseData.map {
                        StoryRemoteKey(id = it.id, prevPage = prevPage, nextPage = nextPage)
                    }

                    database.storyRemoteKeyDao().addRemoteKeys(keys)
                    database.storyDao().insertStories(responseData)
                }

                MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
            }
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private fun getRemoteKeyForLastItem(state: PagingState<Int, StoryEntity>): StoryRemoteKey? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.storyRemoteKeyDao().getRemoteKeys(data.id)
        }
    }

    private fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryEntity>): StoryRemoteKey? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.storyRemoteKeyDao().getRemoteKeys(data.id)
        }
    }

    private fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StoryEntity>): StoryRemoteKey? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.storyRemoteKeyDao().getRemoteKeys(id)
            }
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}