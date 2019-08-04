package ai.chench.assidua

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.io.File
import java.math.BigDecimal
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.exp

class BudgetRepository(val directory: File) {
    // Extension to update observers of updated data
    fun <T> MutableLiveData<T>.notifyObservers() {
        this.value = this.value
    }

    private val _allBudgets: MutableLiveData<List<Budget>> = MutableLiveData()

    // Backing property: https://kotlinlang.org/docs/reference/properties.html#backing-properties
    val allBudgets: LiveData<List<Budget>>
        get() = _allBudgets

    init {
        val budgets: MutableList<Budget> = mutableListOf()
        CsvParser.parseBudget(directory).let { budgets.add(it!!) }
        _allBudgets.value = budgets

//        // Read all the stuff from the file
//        directory.listFiles().forEach {
//            val budget: Budget? = CsvParser.parseBudget(it)
//            budget?.let { budgets.add(budget) }
//        }
    }

    fun getBudgetFromId(id: UUID): LiveData<Budget> {
        val m = MutableLiveData<Budget>()

        m.value = _allBudgets.value?.find {
            it.id == id
        }

        return m
    }

    fun addExpenditure(expenditure: Expenditure, budget: Budget) {
        budget.expenditures.add(expenditure)
        budget.balance.add(expenditure.value)
        _allBudgets.notifyObservers()
    }

//    //Old database code
//    val allExpendiures: LiveData<List<Expenditure>> = expenditureDAO.getAllExpenditures()
//    val allBudgets: LiveData<List<Budget>> = expenditureDAO.getAllBudgets()

//    @WorkerThread
//    suspend fun insertExpenditure(expenditure: Expenditure) {
//        expenditureDAO.insertExpenditure(expenditure)
//    }
//
//    fun deleteExpenditure(expenditure: Expenditure) {
//        expenditureDAO.deleteExpenditure(expenditure.id)
//    }
//
//    fun deleteLastExpenditure(budget: Budget) {
//        expenditureDAO.deleteLastExpenditureFromBudget(budget.id)
//    }
//
//    @WorkerThread
//    suspend fun insertBudget(budget: Budget) {
//        expenditureDAO.insertBudget(budget)
//    }
//
//    fun updateBudget(budget:Budget) {
//        expenditureDAO.updateBudget(
//                budget.id,
//                budget.name,
//                budget.balance
//        )
//    }
//
//    fun deleteBudget(budget: Budget) {
//        expenditureDAO.deleteBudget(budget.id)
//    }
//
//    fun getBudgetFromId(id: UUID): LiveData<Budget> {
//        return expenditureDAO.getBudgetFromId(id)
//    }
//
//    fun getExpendituresFromBudget(budgetId: UUID) : LiveData<List<Expenditure>> {
//        return expenditureDAO.getExpendituresFromBudget(budgetId)
//    }
}