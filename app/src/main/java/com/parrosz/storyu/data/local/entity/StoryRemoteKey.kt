package com.parrosz.storyu.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "story_remote_keys")
data class StoryRemoteKey(
    @PrimaryKey(autoGenerate = false)
    @field:ColumnInfo(name = "id")
    val id: String,

    @field:ColumnInfo(name = "prevPage")
    val prevPage: Int?,

    @field:ColumnInfo(name = "nextPage")
    val nextPage: Int?
)