package ai.chench.assidua

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import java.util.*

class BudgetRepository (private val expenditureDAO: ExpenditureDAO) {
    val allExpendiures: LiveData<List<Expenditure>> = expenditureDAO.getAllExpenditures()
    val allBudgets: LiveData<List<Budget>> = expenditureDAO.getAllBudgets()

    @WorkerThread
    suspend fun insertExpenditure(expenditure: Expenditure) {
        expenditureDAO.insertExpenditure(expenditure)
    }

    @WorkerThread
    suspend fun deleteExpenditure(expenditure: Expenditure) {
        expenditureDAO.deleteExpenditure(expenditure.id)
    }

    @WorkerThread
    suspend fun insertBudget(budget: Budget) {
        expenditureDAO.insertBudget(budget)
    }

    @WorkerThread
    suspend fun deleteBudget(budget: Budget) {
        expenditureDAO.deleteBudget(budget.id)
    }

    @WorkerThread
    fun getExpendituresFromBudget(budgetId: UUID) : LiveData<List<Expenditure>> {
        return expenditureDAO.getExpendituresFromBudget(budgetId)
    }
}