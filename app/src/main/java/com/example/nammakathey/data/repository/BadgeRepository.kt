package com.nammakathey.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.nammakathey.data.db.AppDatabase
import com.nammakathey.data.db.Badge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BadgeRepository(context: Context) {
    private val dao = AppDatabase.getInstance(context).badgeDao()

    val allBadges: LiveData<List<Badge>> = dao.getAllBadges()
    val badgeCount: LiveData<Int> = dao.getBadgeCount()

    suspend fun saveBadge(badge: Badge) = withContext(Dispatchers.IO) {
        dao.insert(badge)
    }

    suspend fun badgeExists(heroId: String): Boolean = withContext(Dispatchers.IO) {
        dao.badgeExists(heroId)
    }
}