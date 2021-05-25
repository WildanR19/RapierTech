package com.example.rapiertech.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rapiertech.R;
import com.example.rapiertech.model.department.DepartmentData;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AdapterDataDepartment extends RecyclerView.Adapter<AdapterDataDepartment.HolderData>{
    private Context ctx;
    private List<DepartmentData> listDept;

    public AdapterDataDepartment(Context ctx, List<DepartmentData> listDept) {
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
        holder.totalDept.setText(String.valueOf(dd.getTotal()) + " Employee");
        if (dd.getTotal() == 0){
            holder.totalDept.setBackgroundResource(R.drawable.bg_total_empty);
        }
        holder.menuPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(ctx, v);
                popupMenu.getMenuInflater().inflate(R.menu.home, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId())
                        {
                            case R.id.action_edit:
                                Toast.makeText(ctx, "Edit Clicked", Toast.LENGTH_SHORT).show();
                                break;

                            case R.id.action_delete:
                                Toast.makeText(ctx, "Delete Clicked", Toast.LENGTH_SHORT).show();
                                String temp = listDept.get(position).getName();
                                deleteItem(position);
                                break;
                        }
                        return false;
                    }
                });
            }
        });
    }

    private void deleteItem(int position) {
        listDept.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, listDept.size());
    }

    @Override
    public int getItemCount() {
        return listDept.size();
    }

    public class HolderData extends RecyclerView.ViewHolder {
        TextView tvName, tvId, totalDept;
        ImageView menuPopup;

        public HolderData(@NonNull @NotNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_nameDept);
            tvId = itemView.findViewById(R.id.tv_idDept);
            menuPopup = itemView.findViewById(R.id.more_dept);
            totalDept = itemView.findViewById(R.id.total_dept);
        }
    }
}
