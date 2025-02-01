package com.comedy.suggester.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.comedy.suggester.generator.PromptStrings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Database singleton.
 * Code adapted from https://developer.android.com/codelabs/basic-android-kotlin-compose-persisting-data-room#6
 */
@Database(
    entities = [AppSettings::class, CharacterProfile::class, GeneratedSuggestions::class],
    version = 6, exportSchema = false
)
@TypeConverters(MyTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appSettingsDao(): AppSettingsDao
    abstract fun characterProfileDao(): CharacterProfileDao
    abstract fun generatedSuggestionsDao(): GeneratedSuggestionsDao

    companion object {
        private const val LOG_TAG = "AppDatabase"
        private const val DATABASE_NAME = "comedy_coach_db"

        @Volatile
        private var Instance: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            Log.d(LOG_TAG, "getDatabase, instance is null? = ${Instance == null}")
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .addCallback(setupDb())
                    .build()
                    .also { Instance = it }
            }
        }

        /**
         * If db is empty, initialize w/ default sense of humor
         */
        private fun setupDb(): Callback {
            return object : Callback() {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    Log.d(LOG_TAG, "setupDb")
                    Instance?.let { database ->
                        CoroutineScope(Dispatchers.IO).launch {
                            val dao = database.characterProfileDao()
                            if (dao.getCount() == 0) {
                                Log.d(LOG_TAG, "initializing default character profile")
                                dao.createNewProfile(
                                    CharacterProfile(
                                        id = CharacterProfile.MY_ID,
                                        senseOfHumor = PromptStrings.DEFAULT_SENSE_OF_HUMOR,
                                        aliases = mapOf()
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }


}