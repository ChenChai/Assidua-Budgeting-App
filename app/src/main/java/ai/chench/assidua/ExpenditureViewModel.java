package ai.chench.assidua;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExpenditureViewModel extends AndroidViewModel {

    private MutableLiveData<List<Expenditure>> expenditures = new MutableLiveData<>();
    private SharedPreferences sharedPreferences;

    private MutableLiveData<BigDecimal> balance = new MutableLiveData<>();

    public ExpenditureViewModel(Application application) {
        super(application);

        sharedPreferences = getApplication().getSharedPreferences(getApplication().getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        // Get the previous list of expenditures and the balance from shared preferences
        String expendituresString = sharedPreferences.getString(getApplication().getString(R.string.expenditures_key), null);
        if (expendituresString == null) {
            expenditures = new MutableLiveData<>();
            expenditures.setValue(new ArrayList<>());
        } else {
            Gson gson = new Gson();
            expenditures.setValue(gson.fromJson(expendituresString, new TypeToken<List<Expenditure>>(){}.getType()));
        }

        balance.setValue(new BigDecimal(
                sharedPreferences.getString(getApplication().getString(R.string.balance_remaining_key), "0")));
    }

    public LiveData<List<Expenditure>> getExpenditures() {
        return expenditures;
    }

    public LiveData<BigDecimal> getBalance() {
        return balance;
    }

    public void undoLastExpenditure() {
        if (expenditures.getValue().size() > 0) {
            // get most recent expenditure
            Expenditure undoExpenditure = expenditures.getValue().get(expenditures.getValue().size() - 1);

            expenditures.getValue().remove(expenditures.getValue().size() - 1);

            balance.setValue(balance.getValue().add(undoExpenditure.getValue()));
            saveExpenditures();
        }
    }

    public void addExpenditure(Expenditure expenditure) {
        expenditures.getValue().add(expenditure);
        balance.setValue(balance.getValue().subtract(expenditure.getValue()));

        saveExpenditures();
    }

    private void saveExpenditures() {
        Gson gson = new Gson();
        sharedPreferences.edit()
                .putString(getApplication().getString(R.string.expenditures_key),
                        gson.toJson(expenditures.getValue(), new TypeToken<List<Expenditure>>(){}.getType()))
                .putString(getApplication().getString(R.string.balance_remaining_key),
                        // put the balance in as a big decimal rounded to two decimal places.
                        String.format(Locale.CANADA, balance.getValue().setScale(2, RoundingMode.HALF_EVEN).toString()))
                .apply();
    }

}
