package ai.chench.assidua

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.TypeConverter
import java.math.BigDecimal
import java.util.*

@Dao
interface ExpenditureDAO {
    @Query("SELECT * FROM expenditure_table ORDER BY date ASC;")
    fun getAllExpenditures(): LiveData<List<Expenditure>>

    @Insert
    suspend fun insert(expenditure: Expenditure)

    @Query("DELETE FROM expenditure_table WHERE id=:id")
    fun deleteExpenditure(id: UUID)

    @Query("DELETE FROM expenditure_table")
    fun deleteAllExpenditures()
}

