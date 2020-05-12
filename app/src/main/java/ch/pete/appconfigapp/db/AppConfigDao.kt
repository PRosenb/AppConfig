package ch.pete.appconfigapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import ch.pete.appconfigapp.model.Config
import ch.pete.appconfigapp.model.ConfigEntry
import ch.pete.appconfigapp.model.ExecutionResult
import ch.pete.appconfigapp.model.KeyValue

@Dao
interface AppConfigDao {
    @Transaction
    @Query("SELECT * FROM config")
    fun fetchConfigEntries(): LiveData<List<ConfigEntry>>

    @Transaction
    @Query("SELECT * FROM config WHERE config.id = :configId")
    fun fetchConfigEntryByIdAsLiveData(configId: Long): LiveData<ConfigEntry>?

    @Transaction
    @Query("SELECT * FROM config WHERE config.id = :configId")
    suspend fun fetchConfigEntryById(configId: Long): ConfigEntry?

    @Query("SELECT * FROM config WHERE config.id = :configId")
    fun fetchConfigById(configId: Long): LiveData<Config>

    @Transaction
    @Query("SELECT * FROM execution_result WHERE configId = :configId ORDER BY timestamp DESC")
    fun fetchExecutionResultEntriesByConfigId(configId: Long): LiveData<List<ExecutionResult>>

    @Query("SELECT * FROM key_value WHERE configId = :configId ORDER BY `key`")
    fun keyValueEntriesByConfigId(configId: Long): LiveData<List<KeyValue>>

    @Query("SELECT * FROM key_value WHERE id = :keyValueId")
    fun keyValueEntryByKeyValueId(keyValueId: Long): LiveData<KeyValue>

    @Transaction
    suspend fun deleteConfigEntry(configEntry: ConfigEntry) {
        deleteConfig(configEntry.config)
        deleteKeyValues(configEntry.keyValues)
        deleteExecutionResults(configEntry.executionResults)
    }

    @Transaction
    suspend fun cloneConfigEntryWithoutResults(configEntry: ConfigEntry, newName: String) {
        val configId = insertConfig(
            configEntry.config.copy(
                id = null,
                name = newName
            )
        )

        // https://issuetracker.google.com/issues/62848977
        val keyValues =
            configEntry.keyValues.map {
                it.copy(
                    id = null,
                    configId = configId
                )
            }

        insertKeyValues(keyValues)
    }

    @Query("INSERT INTO config (name, authority) VALUES ('','')")
    suspend fun insertEmptyConfig(): Long

    @Insert
    suspend fun insertConfig(config: Config): Long

    @Insert
    suspend fun insertKeyValue(keyValue: KeyValue): Long

    @Insert
    suspend fun insertKeyValues(keyValues: List<KeyValue>)

    @Insert
    suspend fun insertExecutionResult(executionResult: ExecutionResult): Long

    @Insert
    suspend fun insertExecutionResults(executionResults: List<ExecutionResult>)

    @Query("UPDATE config SET name = :name WHERE id = :configId")
    suspend fun updateConfigName(name: String, configId: Long)

    @Query("UPDATE config SET authority = :authority WHERE id = :configId")
    suspend fun updateConfigAuthority(authority: String, configId: Long)

    @Update
    suspend fun updateKeyValue(keyValue: KeyValue): Int

    @Update
    suspend fun updateExecutionResult(executionResults: List<ExecutionResult>): Int

    @Delete
    suspend fun deleteConfig(config: Config): Int

    @Delete
    suspend fun deleteKeyValues(keyValues: List<KeyValue>): Int

    @Delete
    suspend fun deleteKeyValue(keyValue: KeyValue): Int

    @Delete
    suspend fun deleteExecutionResults(executionResults: List<ExecutionResult>): Int
}
