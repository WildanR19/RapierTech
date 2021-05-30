package com.example.rapiertech.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rapiertech.R;
import com.example.rapiertech.api.ApiClient;
import com.example.rapiertech.api.ApiInterface;
import com.example.rapiertech.model.empdetail.EmpDetail;
import com.example.rapiertech.model.empdetail.EmpDetailData;
import com.example.rapiertech.model.employee.EmployeeData;
import com.example.rapiertech.ui.admin.employee.EmployeeDetailsFragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import www.sanju.motiontoast.MotionToast;

public class AdapterDataEmployee extends RecyclerView.Adapter<AdapterDataEmployee.HolderData> {

    private final Context context;
    private final List<EmployeeData> empData;
    private List<EmpDetailData> detailData;
//    private String name, email, role, status, ppPath, job;
//    private int idEmp;

    public AdapterDataEmployee(Context context, List<EmployeeData> empList){
        this.context = context;
        this.empData = empList;
    }

    @NonNull
    @NotNull
    @Override
    public AdapterDataEmployee.HolderData onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_employee, parent, false);
        return new HolderData(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterDataEmployee.HolderData holder, int position) {
        EmployeeData ed = empData.get(position);
        holder.tvId.setText(String.valueOf(ed.getId()));
        holder.tvName.setText(ed.getName());
        holder.tvJob.setText(ed.getJob());
        holder.tvStatus.setText(ed.getStatus());
        if (ed.getStatus().equalsIgnoreCase("active")){
            holder.tvStatus.setBackgroundTintList(context.getColorStateList(R.color.success));
        } else {
            holder.tvStatus.setBackgroundTintList(context.getColorStateList(R.color.danger));
        }
        holder.itemView.setOnClickListener(v -> {

            ApiInterface apiData = ApiClient.getClient().create(ApiInterface.class);
            Call<EmpDetail> showData = apiData.employeeDetailData(ed.getId());

            showData.enqueue(new Callback<EmpDetail>() {
                @Override
                public void onResponse(Call<EmpDetail> call, Response<EmpDetail> response) {
                    if (response.isSuccessful()){
                        Bundle bundle = new Bundle();
                        bundle.putInt("id", ed.getId());
                        bundle.putString("name", ed.getName());
                        bundle.putString("email", ed.getEmail());
                        bundle.putInt("roleId", ed.getRole_id());
                        bundle.putString("address", response.body().getData().getAddress());
                        bundle.putString("phone", response.body().getData().getPhone());
                        bundle.putString("gender", response.body().getData().getGender());
                        bundle.putString("joinDate", response.body().getData().getJoinDate());
                        bundle.putString("lastDate", response.body().getData().getLastDate());
                        bundle.putInt("empStatusId", response.body().getData().getStatusId());
                        bundle.putInt("departmentId", response.body().getData().getDepartmentId());
                        bundle.putInt("jobId", response.body().getData().getJobId());

                        Fragment fragment = new EmployeeDetailsFragment();
                        fragment.setArguments(bundle);
                        FragmentManager fragmentManager = ((FragmentActivity)v.getContext()).getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, fragment)
                                .setReorderingAllowed(true)
                                .addToBackStack(null)
                                .commit();
                    } else {
                        errorToast(response.body().getMessage());
                    }
                }

                @Override
                public void onFailure(Call<EmpDetail> call, Throwable t) {
                    noConnectToast(t.getMessage());
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return empData.size();
    }

    public static class HolderData extends RecyclerView.ViewHolder {

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
