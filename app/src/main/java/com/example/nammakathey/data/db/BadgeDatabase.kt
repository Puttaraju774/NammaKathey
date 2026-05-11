package com.nammakathey.data.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(tableName = "badges")
data class Badge(
    @PrimaryKey val heroId: String,
    val heroName: String,
    val heroCategory: String = "",
    val districtName: String = "",
    val earnedAt: Long = System.currentTimeMillis()
)

@Dao
interface BadgeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(badge: Badge)

    @Query("SELECT * FROM badges ORDER BY earnedAt DESC")
    fun getAllBadges(): LiveData<List<Badge>>

    @Query("SELECT COUNT(*) FROM badges")
    fun getBadgeCount(): LiveData<Int>

    @Query("SELECT EXISTS(SELECT 1 FROM badges WHERE heroId = :heroId)")
    suspend fun badgeExists(heroId: String): Boolean

    @Query("DELETE FROM badges")
    suspend fun deleteAll()
}

@Database(entities = [Badge::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun badgeDao(): BadgeDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun getInstance(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nammakathey_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}