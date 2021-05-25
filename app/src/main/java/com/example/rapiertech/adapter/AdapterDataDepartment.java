package com.example.rapiertech.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rapiertech.R;
import com.example.rapiertech.api.ApiClient;
import com.example.rapiertech.api.ApiInterface;
import com.example.rapiertech.model.department.Department;
import com.example.rapiertech.model.department.DepartmentData;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                                builder.setMessage("Are you sure?")
                                        .setCancelable(true)
                                        .setPositiveButton(R.string.alert_delete, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                deleteData(dd.getId());
                                                dialog.dismiss();
                                                listDept.remove(position);
                                                notifyItemRemoved(position);
                                                notifyItemRangeChanged(position, listDept.size());
                                            }
                                        })
                                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                builder.show();
                                break;
                        }
                        return true;
                    }
                });
            }
        });
    }

    private void deleteData(int id) {
        ApiInterface aiData = ApiClient.getClient().create(ApiInterface.class);
        Call<Department> deletedata = aiData.departmentDeleteData(id);
        deletedata.enqueue(new Callback<Department>() {
            @Override
            public void onResponse(Call<Department> call, Response<Department> response) {
                Toast.makeText(ctx, response.body().getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Department> call, Throwable t) {
                Toast.makeText(ctx, "Cannot connect server"+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
