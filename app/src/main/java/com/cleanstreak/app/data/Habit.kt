package com.cleanstreak.app.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * A habit the user wants to stay clean from (e.g. "Alcohol", "Smoking", "Doomscrolling").
 */
@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    /** Index into [HabitColor]. */
    val colorIndex: Int,
    /** Ordering position for stable sorting. */
    val position: Int,
    /** When the habit was created, as epoch day. */
    val createdAtEpochDay: Long
)

/**
 * One row = "this habit was clean on this day".
 * Absence of a row means the day was not (yet) marked clean.
 */
@Entity(tableName = "clean_records", primaryKeys = ["habitId", "epochDay"])
data class CleanRecord(
    val habitId: Long,
    /** LocalDate.toEpochDay() of the clean day. */
    val epochDay: Long
)

@Dao
interface HabitDao {

    @Query("SELECT * FROM habits ORDER BY position, id")
    fun observeHabits(): Flow<List<Habit>>

    @Query("SELECT * FROM clean_records")
    fun observeRecords(): Flow<List<CleanRecord>>

    @Insert
    suspend fun insertHabit(habit: Habit): Long

    @Query("SELECT COALESCE(MAX(position), 0) FROM habits")
    suspend fun maxPosition(): Int

    @Delete
    suspend fun deleteHabit(habit: Habit)

    @Query("DELETE FROM clean_records WHERE habitId = :habitId")
    suspend fun deleteRecordsForHabit(habitId: Long)

    /** Deletes a habit and its whole history in one atomic transaction. */
    @Transaction
    suspend fun deleteHabitWithHistory(habitId: Long) {
        deleteRecordsForHabit(habitId)
        deleteHabitById(habitId)
    }

    @Query("DELETE FROM habits WHERE id = :habitId")
    suspend fun deleteHabitById(habitId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: CleanRecord)

    @Query("DELETE FROM clean_records WHERE habitId = :habitId AND epochDay = :epochDay")
    suspend fun deleteRecord(habitId: Long, epochDay: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM clean_records WHERE habitId = :habitId AND epochDay = :epochDay)")
    suspend fun isClean(habitId: Long, epochDay: Long): Boolean
}

@Database(entities = [Habit::class, CleanRecord::class], version = 1, exportSchema = false)
abstract class CleanStreakDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao

    companion object {
        @Volatile
        private var instance: CleanStreakDatabase? = null

        fun get(context: Context): CleanStreakDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    CleanStreakDatabase::class.java,
                    "cleanstreak.db"
                ).build().also { instance = it }
            }
    }
}
