package com.example.rapiertech.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rapiertech.R;
import com.example.rapiertech.model.employee.EmployeeData;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AdapterDataEmployee extends RecyclerView.Adapter<AdapterDataEmployee.HolderData> {

    private Context context;
    private List<EmployeeData> empData;
    private String name, email, role, status, ppPath, job;

    public AdapterDataEmployee(Context context, List<EmployeeData> empList){
        this.context = context;
        this.empData = empList;
    }

    @NonNull
    @NotNull
    @Override
    public AdapterDataEmployee.HolderData onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_employee, parent, false);
        return new AdapterDataEmployee.HolderData(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterDataEmployee.HolderData holder, int position) {
        EmployeeData ed = empData.get(position);
        holder.tvId.setText(String.valueOf(ed.getId()));
        holder.tvName.setText(ed.getName());
        holder.tvJob.setText(ed.getJob());
        holder.tvStatus.setText(ed.getStatus());
        if (ed.getStatus().equalsIgnoreCase("active")){
            holder.tvStatus.setBackgroundResource(R.drawable.bg_label_green);
        } else {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_label_red);
        }
    }

    @Override
    public int getItemCount() {
        return empData.size();
    }

    public class HolderData extends RecyclerView.ViewHolder {

        ImageView imgEmp;
        TextView tvId, tvName, tvJob, tvStatus;
        public HolderData(@NonNull @NotNull View itemView) {
            super(itemView);

            imgEmp = itemView.findViewById(R.id.img_emp);
            tvId = itemView.findViewById(R.id.id_emp);
            tvName = itemView.findViewById(R.id.name_emp);
            tvJob = itemView.findViewById(R.id.job_emp);
            tvStatus = itemView.findViewById(R.id.status_emp);
        }
    }
}
