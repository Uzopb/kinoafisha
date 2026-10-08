package org.example.kinoafisha.core.data.mapper

import org.example.kinoafisha.core.database.entity.AppSettingsEntity
import org.example.kinoafisha.core.domain.model.FeedLayout

fun FeedLayout.toStorage(): String =
    when (this) {
        FeedLayout.List -> AppSettingsEntity.LAYOUT_LIST
        FeedLayout.Grid -> AppSettingsEntity.LAYOUT_GRID
    }

fun String?.toFeedLayout(): FeedLayout =
    when (this) {
        AppSettingsEntity.LAYOUT_GRID -> FeedLayout.Grid
        else -> FeedLayout.List
    }
