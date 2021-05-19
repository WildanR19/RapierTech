package com.example.rapiertech.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rapiertech.R;
import com.example.rapiertech.model.department.DepartmentData;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AdapterData extends RecyclerView.Adapter<AdapterData.HolderData>{
    private Context ctx;
    private List<DepartmentData> listDept;

    public AdapterData(Context ctx, List<DepartmentData> listDept) {
        this.ctx = ctx;
        this.listDept = listDept;
    }

    @NonNull
    @NotNull
    @Override
    public HolderData onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        HolderData holder = new HolderData(layout);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull HolderData holder, int position) {
        DepartmentData dd = listDept.get(position);

        holder.tvId.setText(String.valueOf(dd.getId()));
        holder.tvName.setText(dd.getName());
    }

    @Override
    public int getItemCount() {
        return listDept.size();
    }

    public class HolderData extends RecyclerView.ViewHolder {
        TextView tvName, tvId;

        public HolderData(@NonNull @NotNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_nameDept);
            tvId = itemView.findViewById(R.id.tv_idDept);
        }
    }
}
