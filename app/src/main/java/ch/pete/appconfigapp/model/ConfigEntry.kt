package ch.pete.appconfigapp.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.util.*

enum class ResultType(val id: Int) {
    SUCCESS(0), ACCESS_DENIED(1), EXCEPTION(2)
}

@Entity(tableName = "execution_result")
data class ExecutionResult(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    var configId: Long? = null,
    val timestamp: Calendar = Calendar.getInstance(),
    val resultType: ResultType,
    val valuesCount: Int = 0,
    val message: String? = null
)

@Entity(tableName = "key_value")
data class KeyValue(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    var configId: Long? = null,
    val key: String,
    val value: String
)

@Entity(tableName = "config")
data class Config(
    // 0L means not set
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val name: String,
    val authority: String
)

data class ConfigEntry(
    @Embedded
    val config: Config,

    @Relation(
        parentColumn = "id",
        entityColumn = "configId"
    )
    val keyValues: List<KeyValue>,

    @Relation(
        parentColumn = "id",
        entityColumn = "configId"
    )
    val executionResults: List<ExecutionResult>
)
