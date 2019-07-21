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
    @Insert
    suspend fun insertExpenditure(expenditure: Expenditure)

    @Query("SELECT * FROM expenditure_table ORDER BY date ASC;")
    fun getAllExpenditures(): LiveData<List<Expenditure>>

    @Query("SELECT * FROM expenditure_table WHERE budget_id=:budgetId ORDER BY date ASC;")
    fun getExpendituresFromBudget(budgetId: UUID) : LiveData<List<Expenditure>>

    @Query("DELETE FROM expenditure_table WHERE id=:id")
    fun deleteExpenditure(id: UUID)

    // Deletes all expenditures associated with a budget
    @Query("DELETE FROM expenditure_table WHERE budget_id=:budgetId")
    fun deleteExpendituresFromBudget(budgetId: UUID)

    @Query("DELETE FROM expenditure_table")
    fun deleteAllExpenditures()

    @Insert
    suspend fun insertBudget(budget: Budget)

    @Query("""
        UPDATE budget_table
        SET name=:budgetName,
            balance=:budgetBalance
        WHERE id=:budgetId;
    """)
    fun updateBudget(budgetId: UUID, budgetName: String, budgetBalance: BigDecimal)

    // NOTE: Does not delete the expenditures associated with the budget!
    @Query("DELETE FROM budget_table WHERE id=:id")
    fun deleteBudget(id: UUID)

    @Query("SELECT * FROM budget_table WHERE id=:id")
    fun getBudgetFromId(id: UUID): LiveData<Budget>

    @Query("SELECT * FROM budget_table")
    fun getAllBudgets(): LiveData<List<Budget>>

    //@Query("SELECT * FROM expenditure_table WHERE budget_id=:budgetId")
    //fun getBudgetExpenditures(budgetId: UUID): LiveData<List<Expenditure>>
}

