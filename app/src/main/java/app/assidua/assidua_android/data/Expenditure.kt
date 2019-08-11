package app.assidua.assidua_android.data

import androidx.room.Entity
import java.math.BigDecimal
import java.util.*

@Entity(tableName = "expenditure_table")
data class Expenditure(
        var name: String,
        var value: BigDecimal,
        var date: Date,
        var budgetId: UUID,
        var id: UUID = UUID.randomUUID())

