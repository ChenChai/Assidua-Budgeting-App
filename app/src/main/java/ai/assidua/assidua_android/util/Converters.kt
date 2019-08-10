package ai.assidua.assidua_android.util

import androidx.room.TypeConverter
import java.math.BigDecimal
import java.util.*

public class Converters {
    @TypeConverter
    fun fromBigDecimal(value: BigDecimal?) : String? {
        return value?.let { it.toPlainString() }
    }

    @TypeConverter
    fun toBigDecimal(value: String?) : BigDecimal? {
        return BigDecimal(value)
    }

    @TypeConverter
    fun fromUUID(value: UUID?) : String? {
        return value.toString()
    }

    @TypeConverter
    fun toUUID(value: String?) : UUID? {
        return UUID.fromString(value)
    }

    @TypeConverter
    fun fromDate(value: Date?) : Long? {
        return value?.time
    }

    @TypeConverter
    fun toDate(value: Long?) : Date? {
        return value?.let { Date(it) }
    }
}
