package ai.chench.assidua;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ai.chench.assidua.data.Expenditure;

public class ExpenditureAdapter extends RecyclerView.Adapter<ExpenditureAdapter.ViewHolder> {
    private static final String TAG = "ExpenditureAdapter";

    static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView expenditureName;
        public TextView expenditureValue;
        public TextView expenditureDate;

        public ViewHolder(View v) {
            super(v);
            expenditureName = v.findViewById(R.id.expenditureName);
            expenditureValue = v.findViewById(R.id.expenditureValue);
            expenditureDate = v.findViewById(R.id.expenditureDate);
        }
    }

    private List<Expenditure> expenditures = new ArrayList<>();

    public ExpenditureAdapter() {
    }

    public void setExpenditures(List<Expenditure> expenditures) {
        this.expenditures = expenditures;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expenditure_viewholder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Expenditure expenditure = expenditures.get(position);
        holder.expenditureName.setText(expenditure.getName());

        // use String.format here to set locale
        holder.expenditureValue.setText(String.format(Locale.CANADA, expenditure.getValue().setScale(2, RoundingMode.HALF_DOWN).toString()));
        holder.expenditureDate.setText(expenditure.getDate().toString());

        holder.itemView.setOnClickListener((View v) -> {
            Log.d(TAG, "Clicked expenditure with UUID: " + expenditures.get(position).getId().toString());
        });

    }

    @Override
    public int getItemCount() {
        return expenditures.size();
    }
}
