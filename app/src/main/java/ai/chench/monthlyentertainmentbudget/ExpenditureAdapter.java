package ai.chench.monthlyentertainmentbudget;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        holder.expenditureValue.setText(String.format(Locale.CANADA, "$%.2f",expenditure.getValue()));
        holder.expenditureDate.setText(expenditures.get(position).getDate().toString());
    }

    @Override
    public int getItemCount() {
        return expenditures.size();
    }
}
