package com.example.rapiertech.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rapiertech.R;
import com.example.rapiertech.api.ApiClient;
import com.example.rapiertech.api.ApiInterface;
import com.example.rapiertech.model.job.Job;
import com.example.rapiertech.model.job.JobData;
import com.example.rapiertech.ui.admin.JobFragment;
import com.example.rapiertech.widget.Widget;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterDataJob extends RecyclerView.Adapter<AdapterDataJob.HolderData> {

    private final Context context;
    private final List<JobData> jobList;
    private String name;
    private final JobFragment fragment;
    private Widget widget;

    public AdapterDataJob(Context context, List<JobData> jobList, JobFragment fragment){
        this.context = context;
        this.jobList = jobList;
        this.fragment = fragment;
    }

    @NonNull
    @NotNull
    @Override
    public HolderData onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_deptjob, parent, false);
        return new HolderData(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull HolderData holder, int position) {
        JobData jd = jobList.get(position);
        widget = new Widget();

        holder.tvId.setText(String.valueOf(jd.getId()));
        holder.tvName.setText(jd.getName());
        holder.totalDept.setText(jd.getTotal() + " Employee");
        if (jd.getTotal() == 0){
            holder.totalDept.setBackgroundTintList(context.getColorStateList(R.color.light_red));
        } else {
            holder.totalDept.setBackgroundTintList(context.getColorStateList(R.color.light_green));
        }
        holder.menuPopup.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.getMenuInflater().inflate(R.menu.home, popupMenu.getMenu());
            popupMenu.show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                popupMenu.setForceShowIcon(true);
            }
            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.action_edit) {
                    MaterialAlertDialogBuilder editDialog = new MaterialAlertDialogBuilder(context);
                    LayoutInflater inflater = LayoutInflater.from(context);
                    View view = inflater.inflate(R.layout.dialog_add_deptjob, null);

                    EditText etName = view.findViewById(R.id.add_deptJobName);
                    etName.setText(jd.getName());

                    editDialog.setView(view)
                            .setTitle(R.string.add_title_dialog)
                            .setPositiveButton(R.string.save, (dialog, which) -> {
                                name = etName.getText().toString();
                                updateData(jd.getId());
                                dialog.dismiss();
                            })
                            .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

                    AlertDialog alertDialog = editDialog.create();
                    alertDialog.show();
                } else if (itemId == R.id.action_delete) {
                    BottomSheetMaterialDialog mDialog = new BottomSheetMaterialDialog.Builder((Activity) context)
                            .setTitle("Delete?")
                            .setMessage("Are you sure want to delete this data?")
                            .setCancelable(false)
                            .setPositiveButton("Delete", R.drawable.ic_delete_, (dialogInterface, which) -> {
                                deleteData(jd.getId());
                                dialogInterface.dismiss();
                            })
                            .setNegativeButton("Cancel", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss())
                            .build();
                    mDialog.show();
                }
                return true;
            });
        });
    }

    private void updateData(int id) {
        ApiInterface apiData = ApiClient.getClient().create(ApiInterface.class);
        Call<Job> updatedata = apiData.jobUpdateData(id, name);
        updatedata.enqueue(new Callback<Job>() {
            @Override
            public void onResponse(Call<Job> call, Response<Job> response) {
                if (response.body() != null){
                    if (response.isSuccessful() && response.body().isStatus()){
                        widget.successToast(response.body().getMessage(), fragment.requireActivity());
                        fragment.retrieveData();
                    }else{
                        widget.errorToast(response.body().getMessage(), fragment.requireActivity());
                    }
                }
            }

            @Override
            public void onFailure(Call<Job> call, Throwable t) {
                widget.noConnectToast(t.getMessage(), fragment.requireActivity());
            }
        });
    }

    private void deleteData(int id) {
        ApiInterface apiData = ApiClient.getClient().create(ApiInterface.class);
        Call<Job> deletedata = apiData.jobDeleteData(id);
        deletedata.enqueue(new Callback<Job>() {
            @Override
            public void onResponse(Call<Job> call, Response<Job> response) {
                if (response.body() != null){
                    if (response.isSuccessful()){
                        widget.successToast(response.body().getMessage(), fragment.requireActivity());
                        fragment.retrieveData();
                    } else {
                        widget.errorToast(response.body().getMessage(), fragment.requireActivity());
                    }
                }
            }

            @Override
            public void onFailure(Call<Job> call, Throwable t) {
                widget.noConnectToast(t.getMessage(), fragment.requireActivity());
            }
        });
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public static class HolderData extends RecyclerView.ViewHolder {
        TextView tvName, tvId, totalDept;
        ImageView menuPopup;

        public HolderData(@NonNull @NotNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_nameDeptJob);
            tvId = itemView.findViewById(R.id.tv_idDeptJob);
            menuPopup = itemView.findViewById(R.id.more_deptJob);
            totalDept = itemView.findViewById(R.id.total_deptJob);
        }
    }
}
