package com.example.rapiertech.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rapiertech.R;
import com.example.rapiertech.api.ApiClient;
import com.example.rapiertech.api.ApiInterface;
import com.example.rapiertech.model.employee.Employee;
import com.example.rapiertech.model.employee.EmployeeData;
import com.example.rapiertech.model.leave.LeaveData;
import com.example.rapiertech.model.leave.LeaveType;
import com.example.rapiertech.model.leave.LeaveTypeData;
import com.example.rapiertech.ui.leave.LeaveFragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import www.sanju.motiontoast.MotionToast;

public class AdapterDataLeave extends RecyclerView.Adapter<AdapterDataLeave.ViewHolder> {

    private final Context context;
    private List<LeaveData> leaveDataList;
    private List<EmployeeData> employeeDataList;
    private List<LeaveTypeData> leaveTypeDataList;
    private LeaveFragment leaveFragment;
    private ApiInterface apiInterface;
//    private int userId, leaveTypeId;

    public AdapterDataLeave(Context context, List<LeaveData> leaveDataList, LeaveFragment leaveFragment) {
        this.context = context;
        this.leaveDataList = leaveDataList;
        this.leaveFragment = leaveFragment;
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

        int userId = leaveData.getUserId();
        int leaveTypeId = leaveData.getLeaveTypeId();

        getUserById(holder, userId);
        getLeaveTypeById(holder, leaveTypeId);

        holder.tvId.setText(String.valueOf(leaveData.getId()));
        holder.tvReason.setText(leaveData.getReason());
        String duration = leaveData.getDuration();
        holder.tvDuration.setText(duration.substring(0, 1).toUpperCase() + duration.substring(1));
        holder.tvFromDate.setText(leaveData.getFromDate());
        holder.tvToDate.setText(leaveData.getToDate());
        String status = leaveData.getStatus();
        holder.tvStatus.setText(status.substring(0, 1).toUpperCase() + status.substring(1));
        if (status.equalsIgnoreCase("approved")){
            holder.tvStatus.setBackgroundTintList(context.getColorStateList(R.color.success));
            holder.vBorder.setBackgroundResource(R.color.success);
        } else if (status.equalsIgnoreCase("pending")){
            holder.tvStatus.setBackgroundTintList(context.getColorStateList(R.color.warning));
            holder.vBorder.setBackgroundResource(R.color.warning);
        } else {
            holder.tvStatus.setBackgroundTintList(context.getColorStateList(R.color.danger));
            holder.vBorder.setBackgroundResource(R.color.danger);
        }
    }

    private void getLeaveTypeById(ViewHolder holder, int leaveTypeId) {
        Call<LeaveType> showData = apiInterface.leaveTypeRetrieveData();
        showData.enqueue(new Callback<LeaveType>() {
            @Override
            public void onResponse(Call<LeaveType> call, Response<LeaveType> response) {
                if (response.body() != null && response.isSuccessful() && response.body().isStatus()){
                    leaveTypeDataList = response.body().getData();
                    for (int i = 0; i < leaveTypeDataList.size(); i++){
                        if (leaveTypeId == leaveTypeDataList.get(i).getId()){
                            holder.tvType.setText(leaveTypeDataList.get(i).getTypeName());
                        }
                    }
                } else {
                    errorToast("LeaveType" + response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<LeaveType> call, Throwable t) {
                noConnectToast("LeaveType" + t.getMessage());
            }
        });
    }

    private void getUserById(ViewHolder holder, int userId) {
        Call<Employee> showData = apiInterface.employeeRetrieveData();
        showData.enqueue(new Callback<Employee>() {
            @Override
            public void onResponse(Call<Employee> call, Response<Employee> response) {
                if (response.body() != null && response.isSuccessful() && response.body().isStatus()){
                    employeeDataList = response.body().getData();
                    for (int i = 0; i < employeeDataList.size(); i++){
                        if (userId == employeeDataList.get(i).getId()){
                            holder.tvName.setText(employeeDataList.get(i).getName());
                        }
                    }
                } else {
                    errorToast("User" + response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<Employee> call, Throwable t) {
                noConnectToast("User" + t.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return leaveDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvId, tvName, tvReason, tvDuration, tvFromDate, tvToDate, tvType, tvStatus;
        View vBorder;

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
        }
    }

    private void noConnectToast(String message) {
        MotionToast.Companion.createColorToast((Activity) context, "Cannot connect server",
                message,
                MotionToast.TOAST_ERROR,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(context,R.font.helvetica_regular)
        );
    }

    private void errorToast(String message) {
        MotionToast.Companion.createColorToast((Activity) context, "Error",
                message,
                MotionToast.TOAST_ERROR,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(context,R.font.helvetica_regular)
        );
    }

    private void successToast(String message) {
        MotionToast.Companion.createColorToast((Activity) context, "Success",
                message,
                MotionToast.TOAST_SUCCESS,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(context,R.font.helvetica_regular)
        );
    }
}
