package com.nhom1.studyschedule.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.nhom1.studyschedule.data.entity.ScheduleItem
import com.nhom1.studyschedule.data.entity.StudySchedule
import kotlinx.coroutines.flow.Flow

private const val ITEM_SELECT =
    "SELECT s.id, s.studentId, s.subjectId, s.studyDate, s.startTime, s.endTime, s.location, s.status, " +
        "sub.subjectCode AS subjectCode, sub.subjectName AS subjectName " +
        "FROM StudySchedule s INNER JOIN Subject sub ON sub.id = s.subjectId "

@Dao
interface StudyScheduleDao {
    @Insert
    suspend fun insert(schedule: StudySchedule): Long

    @Update
    suspend fun update(schedule: StudySchedule)

    @Delete
    suspend fun delete(schedule: StudySchedule)

    @Query("DELETE FROM StudySchedule WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM StudySchedule WHERE id = :id")
    suspend fun getById(id: Long): StudySchedule?

    @Query("UPDATE StudySchedule SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)

    /** List + search screen. An empty [query] returns every schedule of the student. */
    @Query(
        ITEM_SELECT +
            "WHERE s.studentId = :studentId AND (" +
            "sub.subjectName LIKE '%' || :query || '%' OR sub.subjectCode LIKE '%' || :query || '%' " +
            "OR s.location LIKE '%' || :query || '%') " +
            "ORDER BY s.studyDate, s.startTime"
    )
    fun search(studentId: Long, query: String): Flow<List<ScheduleItem>>

    /** Calendar screen: day = (d, d), week/month = (firstDay, lastDay), both "yyyy-MM-dd" inclusive. */
    @Query(
        ITEM_SELECT +
            "WHERE s.studentId = :studentId AND s.studyDate BETWEEN :fromDate AND :toDate " +
            "ORDER BY s.studyDate, s.startTime"
    )
    fun observeBetween(studentId: Long, fromDate: String, toDate: String): Flow<List<ScheduleItem>>

    /**
     * Conflict validation: counts non-cancelled sessions of the student on [date] that overlap
     * [startTime, endTime). Touching sessions (one ends exactly when the next starts) are allowed.
     * Pass the schedule's own id as [excludeId] when editing; use 0 when creating.
     */
    @Query(
        "SELECT COUNT(*) FROM StudySchedule WHERE studentId = :studentId AND studyDate = :date " +
            "AND status != 'CANCELLED' AND id != :excludeId " +
            "AND startTime < :endTime AND endTime > :startTime"
    )
    suspend fun countConflicts(
        studentId: Long,
        date: String,
        startTime: String,
        endTime: String,
        excludeId: Long = 0
    ): Int

    /** Same rule as [countConflicts], but returns the first clashing session to show the user. */
    @Query(
        ITEM_SELECT +
            "WHERE s.studentId = :studentId AND s.studyDate = :date " +
            "AND s.status != 'CANCELLED' AND s.id != :excludeId " +
            "AND s.startTime < :endTime AND s.endTime > :startTime " +
            "ORDER BY s.startTime LIMIT 1"
    )
    suspend fun findConflict(
        studentId: Long,
        date: String,
        startTime: String,
        endTime: String,
        excludeId: Long = 0
    ): ScheduleItem?

    /** Used by the notification worker: non-cancelled sessions from [fromDate] onwards. */
    @Query(
        ITEM_SELECT +
            "WHERE s.studentId = :studentId AND s.status != 'CANCELLED' AND s.studyDate >= :fromDate " +
            "ORDER BY s.studyDate, s.startTime"
    )
    suspend fun getUpcoming(studentId: Long, fromDate: String): List<ScheduleItem>
}
