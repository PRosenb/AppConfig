package ch.pete.appconfigapp.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

object DatabaseBuilder {
    private const val DB_NAME = "appconfig.db"

    fun builder(appContext: Context): RoomDatabase.Builder<AppConfigDatabase> =
        Room.databaseBuilder(appContext, AppConfigDatabase::class.java, DB_NAME)
}
