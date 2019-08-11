package app.assidua.assidua_android.data

import android.app.Application
import android.content.Context
import android.util.Log

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import app.assidua.assidua_android.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.io.File

import java.util.*

class ExpenditureViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val TAG = "ExpenditureViewModel"
    }

    // This class is responsible for handling the order of the budgets, since that isn't relevant
    // to the data in the budgets, which is handled by the repository.
    private var budgetOrder: MutableList<UUID> = mutableListOf()
    private var mApplication = application

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _budgets: LiveData<MutableMap<UUID, Budget>>
    val budgets: LiveData<List<Budget>>

    private val repository: BudgetRepository

    init {
//        Log.d(TAG, "Created a new $TAG")
        repository = BudgetRepository(File(application.filesDir, application.getString(R.string.budget_directory_location)))
        _budgets = repository.allBudgets

        // Transform budget map livedata to a non-mutable list in the correct budget order!
        budgets = Transformations.switchMap(_budgets) { budgetMap: MutableMap<UUID, Budget> ->
            val budgetList: MutableList<Budget?> = MutableList(budgetMap.size) {null}

//            Log.e("CHEN", "-------------Looping through budget map:")
            // Loop through budgets!
            for ((id, budget) in budgetMap) {
                val index: Int = budgetOrder.indexOf(id)
//                Log.d("CHEN", "Index of ${budget.name} is $index")
                if (index >= 0) {
                    budgetList[index] = budget
                } else {
                    // It doesn't exist in the order list yet, add it to the end.
                    budgetOrder.add(id)
                    budgetList.add(budget)
//                    Log.e("CHEN", "Budget does not yet exist in budget order, adding ${budget.name}, new budget order size is ${budgetOrder.size}")
                    saveBudgetOrder()
                }
            }

//            Log.e("CHEN", "Looping through budget list for null errors. BudgetList is: $budgetList, budget order is: $budgetOrder")
            // Loop downwards, removing any
            for (i in budgetList.size - 1 downTo 0) {
                if (budgetList[i] == null) {
//                    Log.e("CHEN", "Budget list at $i is null, removing!")
                    // Budget no longer exists; remove!
                    budgetOrder.removeAt(i)
                    budgetList.removeAt(i)
                }
            }

            val liveData: MutableLiveData<List<Budget>> = MutableLiveData()
            liveData.value = budgetList as List<Budget>

            return@switchMap liveData
        }

        loadBudgetOrder()
    }

    fun addBudget(budget: Budget) {
        repository.addBudget(budget)
        budgetOrder.add(budget.id)
        saveBudgetOrder()
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
        budgetOrder.remove(budgetId)
        saveBudgetOrder()
        return repository.deleteBudget(budgetId)
    }

    /**
     * @param expenditure The expenditure to delete
     * @param budget The budget from which to delete the expenditure.
     */
    fun deleteExpenditure(expenditure: Expenditure, budget: Budget) {

    }

    fun moveBudgetLeft(budgetId: UUID) {
//        Log.d("CHEN", "Trying Moving budget left! ")

        budgetOrder.indexOf(budgetId).let {index ->
//            Log.d("CHEN", "Index was $index")

            if (index < 0) {
//                Log.e(TAG, "Just tried to move budget id $budgetId to the right, but budget could not be found in budget order list!")
                return@let
            }

            if (index >= 1) {
                // if index isn't leftmost element
                Collections.swap(budgetOrder, index, index - 1)
//                Log.d("CHEN", "New index should be ${index - 1}")

                saveBudgetOrder()
                repository.notifyBudgetObservers()
            }
        }
    }

    fun moveBudgetRight(budgetId: UUID) {
//        Log.d("CHEN", "Trying Moving budget right! ")
        budgetOrder.let {
            val index = it.indexOf(budgetId)

            if (index < 0) {
//                Log.e(TAG, "Just tried to move budget id $budgetId to the right, but budget could not be found in budget order list!")
                return@let
            }
//            Log.d("CHEN", "Index was $index")
            // make sure index isn't last element, and also a valid element
            if (index < it.size - 1) {
                // if index isn't leftmost element
                Collections.swap(budgetOrder, index, index + 1)
//                Log.d("CHEN", "New index should be ${index + 1}")
                saveBudgetOrder()
                repository.notifyBudgetObservers()
            }
        }
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

    private fun loadBudgetOrder() {
        mApplication.getSharedPreferences(mApplication.getString(R.string.preference_file_key), Context.MODE_PRIVATE).let {

            val jsonString = it.getString(mApplication.getString(R.string.preference_budget_order), "")
            if (jsonString == "") {
                budgetOrder = mutableListOf()
            } else {
                budgetOrder = Gson().fromJson(
                        jsonString,
                        object : TypeToken<List<UUID>>() {}.type)
            }
        }
    }

    private fun saveBudgetOrder() {
        mApplication.getSharedPreferences(mApplication.getString(R.string.preference_file_key), Context.MODE_PRIVATE).let {
            it.edit()
                    .putString(mApplication.getString(R.string.preference_budget_order),
                            Gson().toJson(budgetOrder))
                    .apply()
        }
    }
}

