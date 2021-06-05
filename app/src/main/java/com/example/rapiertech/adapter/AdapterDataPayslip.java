package com.example.rapiertech.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rapiertech.R;
import com.example.rapiertech.activity.SessionManager;
import com.example.rapiertech.api.ApiClient;
import com.example.rapiertech.api.ApiInterface;
import com.example.rapiertech.model.payslip.Payslip;
import com.example.rapiertech.model.payslip.PayslipData;
import com.example.rapiertech.ui.payslip.PayslipEditorFragment;
import com.example.rapiertech.ui.payslip.PayslipFragment;
import com.example.rapiertech.widget.Widget;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterDataPayslip extends RecyclerView.Adapter<AdapterDataPayslip.ViewHolder> {

    private final Context context;
    private List<PayslipData> payslipDataList;
    private PayslipFragment payslipFragment;
    private SessionManager sessionManager;
    private Widget widget;
    private ApiInterface apiInterface;

    public AdapterDataPayslip(Context context, List<PayslipData> payslipDataList, PayslipFragment payslipFragment) {
        this.context = context;
        this.payslipDataList = payslipDataList;
        this.payslipFragment = payslipFragment;
    }


    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_payslip,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterDataPayslip.ViewHolder holder, int position) {
        PayslipData payslipData = payslipDataList.get(position);
        sessionManager = new SessionManager(context);
        widget = new Widget();
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        holder.tvName.setText(payslipData.getEmpName());
        holder.tvJob.setText(payslipData.getJob());
        holder.tvPayment.setText(widget.capitalizeText(payslipData.getPayment()));

        String fromDate = widget.changeDateFormat(payslipData.getForDate());
        holder.tvFromDate.setText(fromDate);
        String toDate = widget.changeDateFormat(payslipData.getToDate());
        holder.tvToDate.setText(toDate);

        String status = payslipData.getStatus();
        holder.tvStatus.setText(widget.capitalizeText(status));
        if (status.equalsIgnoreCase("paid off")){
            holder.tvStatus.setBackgroundTintList(context.getColorStateList(R.color.light_green));
        } else if (status.equalsIgnoreCase("in progress")){
            holder.tvStatus.setBackgroundTintList(context.getColorStateList(R.color.light_blue));
        } else {
            holder.tvStatus.setBackgroundTintList(context.getColorStateList(R.color.light_red));
        }

        holder.itemView.setOnClickListener(v -> {

            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
            View view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_payslip, null);
            LinearLayout llEdit, llDelete, llDetail, llPrint;
            llEdit = view.findViewById(R.id.ll_edit);
            llDelete = view.findViewById(R.id.ll_delete);
            llDetail = view.findViewById(R.id.ll_detail);
            llPrint = view.findViewById(R.id.ll_print);

            if (!sessionManager.getRoleId().equals("1")) {
                llEdit.setVisibility(View.GONE);
                llDelete.setVisibility(View.GONE);
            }
            llEdit.setOnClickListener(vi -> {
                Bundle bundle = new Bundle();
                bundle.putInt("id", payslipData.getId());
                bundle.putInt("userId", payslipData.getUserId());
                bundle.putString("fromDate", payslipData.getForDate());
                bundle.putString("toDate", payslipData.getToDate());
                bundle.putInt("allowances", payslipData.getAllowances());
                bundle.putInt("deductions", payslipData.getDeductions());
                bundle.putInt("overtimes", payslipData.getOvertimes());
                bundle.putInt("others", payslipData.getOthers());
                bundle.putString("payment", payslipData.getPayment());
                bundle.putString("status", payslipData.getStatus());

                Fragment fragment = new PayslipEditorFragment();
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = ((FragmentActivity)v.getContext()).getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit();

                bottomSheetDialog.dismiss();
            });

            llDelete.setOnClickListener(vi -> {
                BottomSheetMaterialDialog mDialog = new BottomSheetMaterialDialog.Builder((Activity) context)
                        .setTitle("Delete?")
                        .setMessage("Are you sure want to delete this data?")
                        .setCancelable(false)
                        .setPositiveButton("Delete", R.drawable.ic_delete_, (dialogInterface, which) -> {
                            deleteData(payslipData.getId());
                            dialogInterface.dismiss();
                        })
                        .setNegativeButton("Cancel", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss())
                        .build();
                mDialog.show();
                bottomSheetDialog.dismiss();
            });

            llDetail.setOnClickListener(vi -> {
                bottomSheetDialog.dismiss();
            });

            llPrint.setOnClickListener(vi -> {
                bottomSheetDialog.dismiss();
            });

            bottomSheetDialog.setContentView(view);
            bottomSheetDialog.show();
        });
    }

    private void deleteData(int id) {
        Call<Payslip> deleteData = apiInterface.payslipDeleteData(id);
        deleteData.enqueue(new Callback<Payslip>() {
            @Override
            public void onResponse(Call<Payslip> call, Response<Payslip> response) {
                if (response.body() != null) {
                    if (response.isSuccessful() && response.body().isStatus()) {
                        widget.successToast(response.body().getMessage(), payslipFragment.requireActivity());
                        payslipFragment.retrieveData();
                    } else {
                        widget.errorToast(response.body().getMessage(), payslipFragment.requireActivity());
                    }
                }
            }

            @Override
            public void onFailure(Call<Payslip> call, Throwable t) {
                widget.noConnectToast(t.getMessage(), payslipFragment.requireActivity());
            }
        });
    }

    @Override
    public int getItemCount() {
        return payslipDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvJob, tvFromDate, tvToDate, tvStatus, tvPayment;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.nameEmpPayslip);
            tvJob = itemView.findViewById(R.id.jobEmpPayslip);
            tvFromDate = itemView.findViewById(R.id.fromDate_payslip);
            tvToDate = itemView.findViewById(R.id.toDate_payslip);
            tvStatus = itemView.findViewById(R.id.status_payslip);
            tvPayment = itemView.findViewById(R.id.payment_payslip);
        }
    }
}
