package org.example.kinoafisha.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Singleton-строка настроек (layout ленты и т.п.).
 * [layout] — строка без знания domain: `"list"` | `"grid"`.
 */
@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: Int = ID,
    val layout: String = LAYOUT_LIST,
) {
    companion object {
        const val ID = 0
        const val LAYOUT_LIST = "list"
        const val LAYOUT_GRID = "grid"
    }
}
