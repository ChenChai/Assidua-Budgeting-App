package ai.chench.monthlyentertainmentbudget;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView balanceTextView;

    private EditText expenditureCostEditText;
    private EditText expenditureNameEditText;

    private Button addExpenditureButton;
    private Button undoExpenditureButton;

    private RecyclerView recyclerView;
    private ExpenditureAdapter adapter;

    private float balance;
    private List<Expenditure> expenditures;

    class Expenditure {
        private float value;
        private String name;
        private Date date;

        public Expenditure(String name, float value, Date date) {
            this.name = name;
            this.value = value;
            this.date = date;
        }

        public float getValue() {
            return value;
        }
        public void setValue(float value) {
            this.value = value;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public Date getDate() {
            return date;
        }
        public void setDate(Date date) {
            this.date = date;
        }
    }

    private SharedPreferences sharedPreferences;

    private View.OnClickListener addClickListener = (View v) -> {
        float expenditureValue;
        try {
            expenditureValue = Float.parseFloat(expenditureCostEditText.getText().toString());
        } catch (NumberFormatException e) {
            expenditureCostEditText.setError(getString(R.string.error_not_a_number));
            return;
        }
        balance -= expenditureValue;

        expenditures.add(new Expenditure(expenditureNameEditText.getText().toString(),
                expenditureValue, Calendar.getInstance().getTime()));
        adapter.notifyDataSetChanged();
        saveExpenditures();
        // update the view
        updateBalanceView();

        expenditureNameEditText.setText("");
        expenditureCostEditText.setText("");
    };

    private View.OnClickListener undoClickListener = (View v) -> {
        if (expenditures.size() > 0) {
            // get most recent expenditure
            Expenditure undoExpenditure = expenditures.get(expenditures.size() - 1);

            balance += undoExpenditure.value;

            expenditures.remove(expenditures.size() - 1);
            saveExpenditures();
            updateBalanceView();
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        loadExpenditures();

        balanceTextView = findViewById(R.id.remainingMoneyTextView);
        updateBalanceView();

        expenditureCostEditText = findViewById(R.id.expenditureCostEditText);
        expenditureNameEditText = findViewById(R.id.expenditureNameEditText);

        addExpenditureButton = findViewById(R.id.addExpenditureButton);
        addExpenditureButton.setOnClickListener(addClickListener);

        undoExpenditureButton = findViewById(R.id.undoExpenditureButton);
        undoExpenditureButton.setOnClickListener(undoClickListener);

        RecyclerView recyclerView = findViewById(R.id.expendituresRecyclerView);
        adapter = new ExpenditureAdapter(expenditures);

        recyclerView.setAdapter(adapter);

        // reversed layout so most recent items show up at the top
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, true);
        layoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(layoutManager);
    }

    // this function updates the textview of the current balance
    private void updateBalanceView() {
        // change the color of the textView based on whether the balance is positive or negative
        if (Math.round(balance) == 0) {
            balanceTextView.setTextColor(getResources().getColor(R.color.colorNeutral));
        } else if (balance > 0) {
            balanceTextView.setTextColor(getResources().getColor(R.color.colorPositive));
        } else {
            balanceTextView.setTextColor(getResources().getColor(R.color.colorNegative));
        }
        
        balanceTextView.setText(String.format(Locale.CANADA, "$%.2f", balance));
    }

    // These two functions save and load the current balance and expenditure list from shared preferences.
    private void saveExpenditures() {
        Gson gson = new Gson();
        sharedPreferences.edit()
                .putString(getString(R.string.expenditures_key),
                        gson.toJson(expenditures,new TypeToken<List<Expenditure>>(){}.getType()))
                .putFloat(getString(R.string.balance_remaining_key), balance)
                .apply();
    }

    private void loadExpenditures() {
        String expendituresString = sharedPreferences.getString(getString(R.string.expenditures_key), null);

        balance = sharedPreferences.getFloat(getString(R.string.balance_remaining_key), 0f);
        if (expendituresString == null) {
            expenditures = new ArrayList<>();
        } else {
            Gson gson = new Gson();
            expenditures = gson.fromJson(expendituresString, new TypeToken<List<Expenditure>>(){}.getType());
        }
    }
}
