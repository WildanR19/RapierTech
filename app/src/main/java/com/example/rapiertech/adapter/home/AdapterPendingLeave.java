package com.example.rapiertech.adapter.home;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rapiertech.R;
import com.example.rapiertech.activity.SessionManager;
import com.example.rapiertech.api.ApiClient;
import com.example.rapiertech.api.ApiInterface;
import com.example.rapiertech.model.employee.Employee;
import com.example.rapiertech.model.employee.EmployeeData;
import com.example.rapiertech.model.leave.Leave;
import com.example.rapiertech.model.leave.LeaveData;
import com.example.rapiertech.model.leave.LeaveType;
import com.example.rapiertech.model.leave.LeaveTypeData;
import com.example.rapiertech.ui.home.HomeFragment;
import com.example.rapiertech.widget.Widget;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterPendingLeave extends RecyclerView.Adapter<AdapterPendingLeave.ViewHolder> {

    private final Context context;
    private final List<LeaveData> leaveDataList;
    private List<EmployeeData> employeeDataList;
    private List<LeaveTypeData> leaveTypeDataList;
    private final HomeFragment homeFragment;
    private ApiInterface apiInterface;
    private Widget widget;

    public AdapterPendingLeave(Context context, List<LeaveData> leaveDataList, HomeFragment homeFragment) {
        this.context = context;
        this.leaveDataList = leaveDataList;
        this.homeFragment = homeFragment;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_leave,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        LeaveData leaveData = leaveDataList.get(position);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        SessionManager sessionManager = new SessionManager(context);
        widget = new Widget();

        int userId = leaveData.getUserId();
        int leaveTypeId = leaveData.getLeaveTypeId();

        getUserById(holder, userId);
        getLeaveTypeById(holder, leaveTypeId);

        holder.tvId.setText(String.valueOf(leaveData.getId()));
        holder.tvReason.setText(leaveData.getReason());
        holder.tvDuration.setText(widget.capitalizeText(leaveData.getDuration()));

        String fromDate = widget.changeDateFormat(leaveData.getFromDate());
        holder.tvFromDate.setText(fromDate);
        String toDate = widget.changeDateFormat(leaveData.getToDate());
        holder.tvToDate.setText(toDate);
        String status = leaveData.getStatus();
        holder.tvStatus.setText(widget.capitalizeText(status));

        if (status.equalsIgnoreCase("pending") && sessionManager.getRoleId().equals("1")) {
            holder.tvStatus.setBackgroundTintList(context.getColorStateList(R.color.warning));
            holder.vBorder.setBackgroundResource(R.color.warning);
            holder.llConfirm.setVisibility(View.VISIBLE);
        } else {
            holder.tvStatus.setBackgroundTintList(context.getColorStateList(R.color.warning));
            holder.vBorder.setBackgroundResource(R.color.warning);
            holder.llConfirm.setVisibility(View.GONE);
        }

        holder.btnApproved.setOnClickListener(v -> {
            MaterialDialog approveDialog = new MaterialDialog.Builder((Activity) context)
                    .setTitle("Approve ?")
                    .setMessage("Are you sure you want to approve this leave ?")
                    .setCancelable(false)
                    .setPositiveButton("Approve", R.drawable.ic_delete_, (dialogInterface, which) -> {
                        approveLeave(leaveData.getId());
                        dialogInterface.dismiss();
                    })
                    .setNegativeButton("Cancel", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss())
                    .build();
            approveDialog.show();
        });

        holder.btnRejected.setOnClickListener(v -> {
            MaterialAlertDialogBuilder rejectDialog = new MaterialAlertDialogBuilder(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.dialog_add_deptjob, null);

            EditText etReasonDialog = view.findViewById(R.id.add_deptJobName);
            TextInputLayout tilName = view.findViewById(R.id.name_text_field);
            tilName.setHint("Reject Reason");

            rejectDialog.setView(view)
                    .setTitle("Leave Reject Reason (optional)")
                    .setPositiveButton(R.string.reject, (dialog, which) -> {
                        String reason = etReasonDialog.getText().toString();
                        rejectLeave(leaveData.getId(), reason);
                        dialog.dismiss();
                    })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

            AlertDialog alertDialog = rejectDialog.create();
            alertDialog.show();
        });
    }

    private void rejectLeave(int id, String reason) {
        Call<Leave> rejectedLeave = apiInterface.leaveRejected(id, reason);
        rejectedLeave.enqueue(new Callback<Leave>() {
            @Override
            public void onResponse(@NotNull Call<Leave> call, @NotNull Response<Leave> response) {
                if (response.body() != null) {
                    if (response.isSuccessful() && response.body().isStatus()) {
                        widget.successToast(response.body().getMessage(), (FragmentActivity) context);
                        homeFragment.retrieveData();
                    } else {
                        widget.errorToast(response.body().getMessage(), (FragmentActivity) context);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<Leave> call, @NotNull Throwable t) {
                widget.noConnectToast(t.getMessage(), (FragmentActivity) context);
            }
        });

    }

    private void approveLeave(int id) {
        Call<Leave> approvedLeave = apiInterface.leaveApproved(id);
        approvedLeave.enqueue(new Callback<Leave>() {
            @Override
            public void onResponse(@NotNull Call<Leave> call, @NotNull Response<Leave> response) {
                if (response.body() != null) {
                    if (response.isSuccessful() && response.body().isStatus()) {
                        widget.successToast(response.body().getMessage(), (FragmentActivity) context);
                        homeFragment.retrieveData();
                    } else {
                        widget.errorToast(response.body().getMessage(), (FragmentActivity) context);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<Leave> call, @NotNull Throwable t) {
                widget.noConnectToast(t.getMessage(), (FragmentActivity) context);
            }
        });
    }

    private void getLeaveTypeById(AdapterPendingLeave.ViewHolder holder, int leaveTypeId) {
        holder.loadingData.setVisibility(View.VISIBLE);

        Call<LeaveType> showData = apiInterface.leaveTypeRetrieveData();
        showData.enqueue(new Callback<LeaveType>() {
            @Override
            public void onResponse(@NotNull Call<LeaveType> call, @NotNull Response<LeaveType> response) {
                if (response.body() != null){
                    if (response.isSuccessful() && response.body().isStatus()){
                        leaveTypeDataList = response.body().getData();
                        for (int i = 0; i < leaveTypeDataList.size(); i++){
                            if (leaveTypeId == leaveTypeDataList.get(i).getId()){
                                holder.tvType.setText(leaveTypeDataList.get(i).getTypeName());
                            }
                        }
                    } else {
                        widget.errorToast("LeaveType" + response.body().getMessage(), homeFragment.requireActivity());
                    }
                }
                holder.loadingData.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(@NotNull Call<LeaveType> call, @NotNull Throwable t) {
                widget.noConnectToast("LeaveType" + t.getMessage(), homeFragment.requireActivity());
                holder.loadingData.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void getUserById(AdapterPendingLeave.ViewHolder holder, int userId) {
        holder.loadingData.setVisibility(View.VISIBLE);

        Call<Employee> showData = apiInterface.employeeRetrieveData();
        showData.enqueue(new Callback<Employee>() {
            @Override
            public void onResponse(@NotNull Call<Employee> call, @NotNull Response<Employee> response) {
                if (response.body() != null){
                    if (response.isSuccessful() && response.body().isStatus()){
                        employeeDataList = response.body().getData();
                        for (int i = 0; i < employeeDataList.size(); i++){
                            if (userId == employeeDataList.get(i).getId()){
                                holder.tvName.setText(employeeDataList.get(i).getName());
                            }
                        }
                    } else {
                        widget.errorToast("User" + response.body().getMessage(), homeFragment.requireActivity());
                    }
                }
                holder.loadingData.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(@NotNull Call<Employee> call, @NotNull Throwable t) {
                widget.noConnectToast("User" + t.getMessage(), homeFragment.requireActivity());
                holder.loadingData.setVisibility(View.INVISIBLE);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        int limit = 3;
        return Math.min(leaveDataList.size(), limit);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvId, tvName, tvReason, tvDuration, tvFromDate, tvToDate, tvType, tvStatus;
        View vBorder;
        LinearLayout llConfirm;
        Button btnApproved, btnRejected;
        ProgressBar loadingData;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tvId = itemView.findViewById(R.id.id_leave);
            tvName = itemView.findViewById(R.id.name_emp_leave);
            tvReason = itemView.findViewById(R.id.reason_leave);
            tvDuration = itemView.findViewById(R.id.duration_leave);
            tvFromDate = itemView.findViewById(R.id.fromDate_leave);
            tvToDate = itemView.findViewById(R.id.toDate_leave);
            vBorder = itemView.findViewById(R.id.border_leave);
            tvType = itemView.findViewById(R.id.type_leave);
            tvStatus = itemView.findViewById(R.id.status_leave);
            llConfirm = itemView.findViewById(R.id.llApproveConfirm);
            btnApproved = itemView.findViewById(R.id.btnApprovedLeave);
            btnRejected = itemView.findViewById(R.id.btnRejectedLeave);
            loadingData = itemView.findViewById(R.id.loadingDataCardLeave);
        }
    }
}
