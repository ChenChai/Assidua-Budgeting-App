package ai.chench.monthlyentertainmentbudget;

import androidx.appcompat.app.AppCompatActivity;
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
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView remainingMoneyTextView;

    private EditText addExpenditureEditText;
    private Button addExpenditureButton;
    private Button undoExpenditureButton;

    private RecyclerView recyclerView;

    private float balance;
    private List<Expenditure> expenditures;

    class Expenditure {
        private float value;
        private String name;

        public Expenditure(String name, float value) {
            this.name = name;
            this.value = value;
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
    }

    private SharedPreferences sharedPreferences;

    private View.OnClickListener addClickListener = (View v) -> {
        float expenditure;
        try {
            expenditure = Float.parseFloat(addExpenditureEditText.getText().toString());
        } catch (NumberFormatException e) {
            addExpenditureEditText.setError(getString(R.string.error_not_a_number));
            return;
        }
        balance -= expenditure;

        // update the value in preferences.
        sharedPreferences.edit().putFloat(getString(R.string.balance_remaining_key), balance).apply();
        // update the view
        remainingMoneyTextView.setText(String.format(Locale.CANADA, "$%.2f", balance));
    };

    private View.OnClickListener undoClickListener = (View v) -> {
        // TODO implement
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        balance = sharedPreferences.getFloat(getString(R.string.balance_remaining_key), 0f);
        loadExpenditures();

        remainingMoneyTextView = findViewById(R.id.remainingMoneyTextView);
        remainingMoneyTextView.setText(String.format(Locale.CANADA, "$%.2f", balance));

        addExpenditureEditText = findViewById(R.id.addExpenditureEditText);

        addExpenditureButton = findViewById(R.id.addExpenditureButton);
        addExpenditureButton.setOnClickListener(addClickListener);

        undoExpenditureButton = findViewById(R.id.undoExpenditureButton);
        undoExpenditureButton.setOnClickListener(undoClickListener);
    }

    // saves the list of expenditures into the
    private void saveExpenditures() {
        Gson gson = new Gson();
        sharedPreferences.edit()
                .putString(getString(R.string.expenditures_key),
                        gson.toJson(expenditures,new TypeToken<List<Expenditure>>(){}.getType()))
                .apply();
    }
    private void loadExpenditures() {
        String expendituresString = sharedPreferences.getString(getString(R.string.expenditures_key), null);
        if (expendituresString == null) {
            expenditures = new ArrayList<>();
        } else {
            Gson gson = new Gson();
            expenditures = gson.fromJson(expendituresString, new TypeToken<List<Expenditure>>(){}.getType());
        }
    }
}
