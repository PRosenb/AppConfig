package ch.pete.appconfigapp.model

import androidx.room.TypeConverter


class ResultTypeConverter {
    @TypeConverter
    fun toResultType(id: Int): ResultType {
        return when (id) {
            ResultType.SUCCESS.id -> ResultType.SUCCESS
            ResultType.ACCESS_DENIED.id -> ResultType.ACCESS_DENIED
            ResultType.EXCEPTION.id -> ResultType.EXCEPTION
            else -> throw IllegalArgumentException("Unknown id $id")
        }
    }

    @TypeConverter
    fun toInteger(resultType: ResultType): Int {
        return resultType.id
    }
}
