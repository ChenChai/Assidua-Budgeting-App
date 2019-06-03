package ai.chench.monthlyentertainmentbudget;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView remainingMoneyTextView;
    private Button addExpenditureButton;
    private Button undoExpenditureButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        remainingMoneyTextView = findViewById(R.id.remainingMoneyTextView);

    }
}
