package ch.pete.appconfigapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ch.pete.appconfigapp.model.Config
import ch.pete.appconfigapp.model.ExecutionResult
import ch.pete.appconfigapp.model.KeyValue
import ch.pete.appconfigapp.model.ResultTypeConverter

@Database(entities = [Config::class, KeyValue::class, ExecutionResult::class], version = 1)
@TypeConverters(ResultTypeConverter::class)
abstract class AppConfigDatabase : RoomDatabase() {
    abstract fun appConfigDao(): AppConfigDao
}
