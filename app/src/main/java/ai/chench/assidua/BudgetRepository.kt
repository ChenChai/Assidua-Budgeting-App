package ai.chench.assidua

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.io.File
import java.util.*
import kotlin.collections.HashMap

class BudgetRepository(val directory: File) {
    // Extension to update observers of updated data
    fun <T> MutableLiveData<T>.notifyObservers() {
        this.value = this.value
    }

    private val _allBudgets: MutableLiveData<MutableMap<UUID, Budget>> = MutableLiveData()

    // Backing property: https://kotlinlang.org/docs/reference/properties.html#backing-properties
    val allBudgets: LiveData<MutableMap<UUID, Budget>>
        get() = _allBudgets

    init {
        val budgetMap: MutableMap<UUID, Budget> = HashMap()
        CsvParser.parseBudget(directory).let { budgetMap.put(it!!.id, it) }
        _allBudgets.value = budgetMap

//        // Read all the stuff from the file
//        directory.listFiles().forEach {
//            val budget: Budget? = CsvParser.parseBudget(it)
//            budget?.let { budgets.add(budget) }
//        }
    }

    fun getBudgetFromId(id: UUID): Budget? {
        return _allBudgets.value?.get(id)
    }

    fun addExpenditure(expenditure: Expenditure, budgetId: UUID) {
        val budget = getBudgetFromId(budgetId)
        budget?.let {
            it.balance = budget.balance.add(expenditure.value)
            it.expenditures.add(expenditure)
        }

        _allBudgets.notifyObservers()
    }

    fun addBudget(budget: Budget) {
        _allBudgets.value?.put(budget.id, budget)
        _allBudgets.notifyObservers()
    }

    fun deleteLastExpenditure(budgetId: UUID) {
        val budget = getBudgetFromId(budgetId)

        budget?.let {
            if (it.expenditures.size > 0) {

                val expenditure = it.expenditures.last()

                // subtract that value from the balance
                budget.balance.subtract(expenditure.value)

                // Remove the expenditure from the list
                it.expenditures.removeAt(
                        // remove the last element from the list
                        it.expenditures.size - 1
                )
            }
        }
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