package com.apkipa.myscreenrefreshdroid

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri

data class RefreshEntry(
    val id: Int,
    val title: String,
    val description: String,
    val maxRefreshRate: Double,
)

fun GetDefaultRefreshEntryList(): List<RefreshEntry> {
    return listOf(
        RefreshEntry(
            id = 1,
            title = "60Hz",
            description = "标准刷新率",
            maxRefreshRate = 60.000004,
        ),
        RefreshEntry(
            id = 2,
            title = "120Hz",
            description = "高刷新率",
            maxRefreshRate = 120.00001,
        ),
        RefreshEntry(
            id = 3,
            title = "智能 120Hz",
            description = "智能切换",
            maxRefreshRate = 120.0,
        ),
        RefreshEntry(
            id = 4,
            title = "(旧版)全局 120Hz",
            description = "强制高刷",
            maxRefreshRate = 59.0,
        ),
        RefreshEntry(
            id = 5,
            title = "全局 120Hz",
            description = "强制高刷",
            maxRefreshRate = 1.0,
        ),
    )
}

fun RefreshEntry.ApplySetting(context: Context) {
    val resolver = context.contentResolver
    val contentValues = ContentValues(2)
    contentValues.put("name", "peak_refresh_rate")
    contentValues.put("value", maxRefreshRate.toString())
    resolver.insert(
        Uri.parse("content://settings/system"),
        contentValues,
    )
}
