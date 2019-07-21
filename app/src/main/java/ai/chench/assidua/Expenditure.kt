package ai.chench.assidua

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.util.*

@Entity(tableName = "expenditure_table")
data class Expenditure(
        @ColumnInfo(name = "name") var name: String,
        @ColumnInfo(name = "value") var value: BigDecimal,
        @ColumnInfo(name = "date") var date: Date,
        @ColumnInfo(name = "budget_id") var budgetId: UUID) {

        @PrimaryKey

        @ColumnInfo(name = "id")
        var id: UUID = UUID.randomUUID()
}

