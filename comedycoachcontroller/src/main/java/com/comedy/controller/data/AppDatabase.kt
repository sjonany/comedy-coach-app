import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import com.comedy.controller.data.AppSettings
import com.comedy.controller.data.AppSettingsDao

/**
 * Database singleton.
 * Code adapted from https://developer.android.com/codelabs/basic-android-kotlin-compose-persisting-data-room#6
 */
@Database(entities = [AppSettings::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appSettingDao():AppSettingsDao

    companion object {
        private const val DATABASE_NAME = "comedy_coach_db"

        @Volatile
        private var Instance: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}