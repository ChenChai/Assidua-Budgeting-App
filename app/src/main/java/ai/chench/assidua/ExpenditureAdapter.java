package ai.chench.assidua;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;

public class ExpenditureAdapter extends RecyclerView.Adapter<ExpenditureAdapter.ViewHolder> {
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

    private List<Expenditure> expenditures;

    public ExpenditureAdapter(List<Expenditure> expenditures) {
        this.expenditures = expenditures;
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
        holder.expenditureValue.setText(expenditure.getValue().setScale(2, RoundingMode.HALF_DOWN).toString());
        holder.expenditureDate.setText(expenditure.getDate().toString());
    }

    @Override
    public int getItemCount() {
        return expenditures.size();
    }
}
