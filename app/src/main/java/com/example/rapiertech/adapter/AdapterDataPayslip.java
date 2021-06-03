package com.example.rapiertech.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rapiertech.R;
import com.example.rapiertech.activity.SessionManager;
import com.example.rapiertech.model.payslip.PayslipData;
import com.example.rapiertech.ui.payslip.PayslipFragment;
import com.example.rapiertech.widget.Widget;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class AdapterDataPayslip extends RecyclerView.Adapter<AdapterDataPayslip.ViewHolder> {

    private final Context context;
    private List<PayslipData> payslipDataList;
    private PayslipFragment payslipFragment;
    private SessionManager sessionManager;
    private Widget widget;

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
        return new AdapterDataPayslip.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterDataPayslip.ViewHolder holder, int position) {
        PayslipData payslipData = payslipDataList.get(position);
        sessionManager = new SessionManager(context);
        widget = new Widget();

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

//        holder.itemView.setOnClickListener(v -> {
//            Bundle bundle = new Bundle();
//            bundle.putInt("id", payslipData.getId());
//            bundle.putInt("userId", payslipData.getUserId());
//            bundle.putString("fromDate", payslipData.getForDate());
//            bundle.putString("toDate", payslipData.getToDate());
//            bundle.putInt("allowances", payslipData.getAllowances());
//            bundle.putInt("deductions", payslipData.getDeductions());
//            bundle.putInt("overtimes", payslipData.getOvertimes());
//            bundle.putInt("others", payslipData.getOthers());
//            bundle.putString("payment", payslipData.getPayment());
//            bundle.putString("status", payslipData.getStatus());
//        });
    }

    @Override
    public int getItemCount() {
        return payslipDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

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
