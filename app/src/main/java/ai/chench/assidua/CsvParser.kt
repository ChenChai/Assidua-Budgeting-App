package ai.chench.assidua

import java.io.File
import java.math.BigDecimal

class CsvParser {
    companion object {
        fun parseBudget(csv: File): Budget? {
            return Budget("Test", BigDecimal(123), mutableListOf<Expenditure>())
        }
    }
}
