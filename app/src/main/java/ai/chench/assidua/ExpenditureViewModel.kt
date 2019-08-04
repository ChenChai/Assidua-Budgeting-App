package ai.chench.assidua

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class ExpenditureViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val TAG = "ExpenditureViewModel"
    }

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val budgets: LiveData<List<Budget>>
    private val repository: BudgetRepository

    init {
        Log.d(TAG, "Created a new $TAG")
        repository = BudgetRepository(File(application.filesDir, application.getString(R.string.budget_directory_location)))

        budgets = repository.allBudgets
    }

    fun addBudget(budget: Budget) {

    }

    fun getBudget(budgetId: UUID): LiveData<Budget> {
        return repository.getBudgetFromId(budgetId)
    }

    /**
     * @param expenditure The expenditure to delete
     * @param budget The budget from which to delete the expenditure.
     */
    fun deleteExpenditure(expenditure: Expenditure, budget: Budget) {

    }

    fun deleteLastExpenditure(budget: Budget) {

    }

    /**
     * @param budget The budget for which to add the expenditure to
     */
    fun addExpenditure(expenditure: Expenditure, budget: Budget) {
        repository.addExpenditure(expenditure, budget)
    }
}
