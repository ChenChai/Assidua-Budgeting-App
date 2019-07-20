package ai.chench.assidua

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.util.*

@Entity(tableName = "budget_table")
data class Budget(
        @ColumnInfo(name = "name") var name: String,
        @ColumnInfo(name = "balance") var balance: BigDecimal) {

        @PrimaryKey
        @ColumnInfo(name = "id")
        var id: UUID = UUID.randomUUID()
}