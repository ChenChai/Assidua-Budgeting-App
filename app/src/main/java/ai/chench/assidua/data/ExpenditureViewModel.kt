package ai.chench.assidua.data

import ai.chench.assidua.R
import android.app.Application
import android.util.Log

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.io.File

import java.util.*

class ExpenditureViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val TAG = "ExpenditureViewModel"
    }

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val budgets: LiveData<MutableMap<UUID, Budget>>
    private val repository: BudgetRepository

    init {
        Log.d(TAG, "Created a new $TAG")
        repository = BudgetRepository(File(application.filesDir, application.getString(R.string.budget_directory_location)))

        budgets = repository.allBudgets
    }

    fun addBudget(budget: Budget) {
        repository.addBudget(budget)
    }

    fun getBudget(budgetId: UUID): Budget? {
        return repository.getBudgetFromId(budgetId)
    }

    fun setBudgetName(budgetId: UUID, title: String) {
        repository.setBudgetName(budgetId, title)
    }

    /**
     * Deletes a budget.
     * @param budgetId The UUID of the budget to delete
     * @return whether the deletion was successful.
     */
    fun deleteBudget(budgetId: UUID): Boolean {
        return repository.deleteBudget(budgetId)
    }

    /**
     * @param expenditure The expenditure to delete
     * @param budget The budget from which to delete the expenditure.
     */
    fun deleteExpenditure(expenditure: Expenditure, budget: Budget) {

    }

    fun deleteLastExpenditure(budget: Budget) {
        repository.deleteLastExpenditure(budget.id)
    }

    /**
     * @param budget The budget for which to add the expenditure to
     */
    fun addExpenditure(expenditure: Expenditure, budget: Budget) {
        repository.addExpenditure(expenditure, budget.id)
    }
}
