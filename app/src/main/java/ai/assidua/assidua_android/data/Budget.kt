package ai.assidua.assidua_android.data

import java.math.BigDecimal
import java.util.*

data class Budget(
        var name: String,
        var balance: BigDecimal,
        var expenditures: MutableList<Expenditure>,
        var id: UUID = UUID.randomUUID())