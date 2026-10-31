package com.nhom1.studyschedule.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nhom1.studyschedule.data.dao.AccountDao
import com.nhom1.studyschedule.data.dao.StudentDao
import com.nhom1.studyschedule.data.dao.StudyScheduleDao
import com.nhom1.studyschedule.data.dao.SubjectDao
import com.nhom1.studyschedule.data.entity.Account
import com.nhom1.studyschedule.data.entity.Role
import com.nhom1.studyschedule.data.entity.Student
import com.nhom1.studyschedule.data.entity.StudySchedule
import com.nhom1.studyschedule.data.entity.Subject
import com.nhom1.studyschedule.util.PasswordHasher

@Database(
    entities = [Account::class, Student::class, Subject::class, StudySchedule::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun studentDao(): StudentDao
    abstract fun subjectDao(): SubjectDao
    abstract fun studyScheduleDao(): StudyScheduleDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "study_schedule.db"
                ).addCallback(SeedCallback).build().also { instance = it }
            }
    }

    /** Seeds demo data on first creation: 1 admin, 1 student, 3 subjects. */
    private object SeedCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "INSERT INTO Account (username, password, role) VALUES (?, ?, ?)",
                arrayOf("admin", PasswordHasher.hash("admin123"), Role.ADMIN)
            )
            db.execSQL(
                "INSERT INTO Account (username, password, role) VALUES (?, ?, ?)",
                arrayOf("sv01", PasswordHasher.hash("123456"), Role.STUDENT)
            )
            db.execSQL(
                "INSERT INTO Student (studentCode, fullname, email, phone, accountId) " +
                    "VALUES ('SV01', 'Nguyễn Văn A', 'sv01@example.com', '0900000001', " +
                    "(SELECT id FROM Account WHERE username = 'sv01'))"
            )
            db.execSQL(
                "INSERT INTO Subject (subjectCode, subjectName, description, credits) VALUES " +
                    "('MOB101', 'Lập trình di động', 'Android native với Kotlin', 3), " +
                    "('DB101', 'Cơ sở dữ liệu', 'SQL và thiết kế CSDL', 3), " +
                    "('ENG101', 'Tiếng Anh chuyên ngành', 'Tiếng Anh cho CNTT', 2)"
            )
        }
    }
}
