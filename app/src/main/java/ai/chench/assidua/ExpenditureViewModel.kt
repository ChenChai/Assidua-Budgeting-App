package ai.chench.assidua

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale

class ExpenditureViewModel(application: Application) : AndroidViewModel(application) {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    //private val budgets: MutableLiveData<List<Budget>>
    val expenditures: LiveData<List<Expenditure>>
    private val sharedPreferences: SharedPreferences

    val balance = MutableLiveData<BigDecimal>()

    private val repository: BudgetRepository

    init {
        val dao = AssiduaRoomDatabase.getDatabase(application).expenditureDAO()
        repository = BudgetRepository(dao)

        expenditures = repository.allExpendiures

        sharedPreferences = getApplication<Application>().getSharedPreferences(getApplication<Application>().getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        // Get the previous balance from shared preferences
        // TODO put balance in the database under budgets
        balance.value = BigDecimal(
                sharedPreferences.getString(getApplication<Application>().getString(R.string.balance_remaining_key), "0"))
    }


    fun getBalance(): LiveData<BigDecimal> {
        return balance
    }

    fun undoLastExpenditure() {
        if (expenditures.value!!.isNotEmpty()) {
            // get most recent expenditure
            val expenditure = expenditures.value!![expenditures.value!!.size - 1]
            uiScope.launch(Dispatchers.IO) {
                repository.delete(expenditure)
            }

            balance.setValue(balance.value!!.add(expenditure.value))
            saveBalance()
        }
    }

    fun addExpenditure(expenditure: Expenditure) {
        uiScope.launch(Dispatchers.IO) {
            repository.insert(expenditure)
        }

        balance.setValue(balance.value!!.subtract(expenditure.value))

        saveBalance()
    }

    private fun saveBalance() {
        val gson = Gson()
        sharedPreferences.edit()
                .putString(getApplication<Application>().getString(R.string.balance_remaining_key),
                        // put the balance in as a big decimal rounded to two decimal places.
                        String.format(Locale.CANADA, balance.value!!.setScale(2, RoundingMode.HALF_EVEN).toString()))
                .apply()
    }


}
