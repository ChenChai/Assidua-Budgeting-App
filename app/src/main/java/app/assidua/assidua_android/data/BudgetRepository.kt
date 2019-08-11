package app.assidua.assidua_android.data

import app.assidua.assidua_android.util.CsvBudgetIoUtil
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.HashMap

class BudgetRepository(private val budgetDirectory: File) {

    // Extension to update observers of updated data
    fun <T> MutableLiveData<T>.notifyObservers() {
        this.value = this.value
    }
    fun notifyBudgetObservers() {
        _allBudgets.notifyObservers()
    }

    companion object {
        const val TAG = "BudgetRepository"

    }

    private val _allBudgets: MutableLiveData<MutableMap<UUID, Budget>> = MutableLiveData()

    // Backing property: https://kotlinlang.org/docs/reference/properties.html#backing-properties
    val allBudgets: LiveData<MutableMap<UUID, Budget>>
        get() = _allBudgets

    init {
        val budgetMap: MutableMap<UUID, Budget> = HashMap()

        budgetDirectory.mkdirs()
        // Read all the budgets from the directory
        budgetDirectory.listFiles().forEach { file ->
//            Log.d(TAG, "Attempting to parse a budget from: $file")
            // Attempt to parse the budget (will return null if failed to parse)
            CsvBudgetIoUtil.parseBudget(FileInputStream(file))?.let {

                // If successful, add the budget to the map
                budget -> budgetMap.put(budget.id, budget)
            }
        }

        // Provide the initial budget value
        _allBudgets.value = budgetMap
    }

    fun getBudgetFromId(id: UUID): Budget? {
        return _allBudgets.value?.get(id)
    }

    fun addExpenditure(expenditure: Expenditure, budgetId: UUID) {
        val budget = getBudgetFromId(budgetId)
        budget?.let {
            it.balance = budget.balance.add(expenditure.value)
            it.expenditures.add(expenditure)

            saveBudget(budget)
            _allBudgets.notifyObservers()
        }
    }

    fun addBudget(budget: Budget) {
        _allBudgets.value?.put(budget.id, budget)
        saveBudget(budget)

        _allBudgets.notifyObservers()
    }

    fun setBudgetName(budgetId: UUID, name: String) {
        _allBudgets.value?.get(budgetId)?.let { budget ->
            budget.name = name
            saveBudget(budget)
            _allBudgets.notifyObservers()
        }
    }

    fun deleteLastExpenditure(budgetId: UUID) {
        val budget = getBudgetFromId(budgetId)

        budget?.let {
            if (it.expenditures.size > 0) {

                val expenditure = it.expenditures.last()

                // subtract that value from the balance
                budget.balance = budget.balance.subtract(expenditure.value)

                // Remove the expenditure from the list
                it.expenditures.removeAt(
                        // remove the last element from the list
                        it.expenditures.size - 1
                )

                saveBudget(budget)
                _allBudgets.notifyObservers()
            }
        }
    }

    /**
     * Deletes a budget.
     * @param budgetId The UUID of the budget to delete
     * @return Whether deletion was successful
     */
    fun deleteBudget(budgetId: UUID) : Boolean {
        Log.d(TAG, "Deleting budget with ID $budgetId")

        // remove the budget from memory
        _allBudgets.value?.remove(budgetId)
        _allBudgets.notifyObservers()
        // Delete the CSV file for the budget
        return File(budgetDirectory, budgetId.toString()).delete()
    }

    private fun saveAllBudgets() {
        _allBudgets.value?.forEach {
            val budget = it.value
            CsvBudgetIoUtil.saveBudget(budget,
                    FileOutputStream(File(budgetDirectory, budget.id.toString())))
        }
    }

    private fun saveBudget(budget: Budget) {
        CsvBudgetIoUtil.saveBudget(budget,
                FileOutputStream(File(budgetDirectory, budget.id.toString())))
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