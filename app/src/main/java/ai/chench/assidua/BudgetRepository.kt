package ai.chench.assidua

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

class BudgetRepository (private val expenditureDAO: ExpenditureDAO) {
    val allExpendiures: LiveData<List<Expenditure>> = expenditureDAO.getAllExpenditures()

    @WorkerThread
    suspend fun insert(expenditure: Expenditure) {
        expenditureDAO.insert(expenditure)
    }

    @WorkerThread
    suspend fun delete(expenditure: Expenditure) {
        expenditureDAO.deleteExpenditure(expenditure.id)
    }
}