package app.assidua.assidua_android.data

import java.math.BigDecimal
import java.util.*

data class Expenditure(
        var name: String,
        var value: BigDecimal,
        var date: Date,
        var budgetId: UUID,
        var id: UUID = UUID.randomUUID())

