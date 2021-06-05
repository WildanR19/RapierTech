package com.example.rapiertech.ui.payslip;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.rapiertech.R;
import com.example.rapiertech.activity.SessionManager;
import com.example.rapiertech.adapter.AdapterDataPayslip;
import com.example.rapiertech.api.ApiClient;
import com.example.rapiertech.api.ApiInterface;
import com.example.rapiertech.model.payslip.Payslip;
import com.example.rapiertech.model.payslip.PayslipData;
import com.example.rapiertech.widget.Widget;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayslipFragment extends Fragment {

    private View view, shadowBg;
    private RecyclerView rvData;
    private RecyclerView.Adapter adData;
    private List<PayslipData> payslipDataList = new ArrayList<>();
    private SwipeRefreshLayout srlData;
    private ProgressBar loadingData;
    private ApiInterface apiInterface;
    private SessionManager sessionManager;
    private Widget widget;
    private FloatingActionButton fab, fabBasic, fabAuto;
    private TextView fabBasicText, fabAutoText;
    private Boolean isAllFabsVisible;
    private String fromDate, toDate;


    public PayslipFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_payslip, container, false);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        widget = new Widget();

        rvData = view.findViewById(R.id.rvDataPayslip);
        RecyclerView.LayoutManager lmData = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        rvData.setLayoutManager(lmData);

        loadingData = view.findViewById(R.id.loadingDataPayslip);
        srlData = view.findViewById(R.id.srlDataPayslip);

        fab = view.findViewById(R.id.fab_payslip);
        fabBasic = view.findViewById(R.id.fab_basicSalary);
        fabAuto = view.findViewById(R.id.fab_autoPayslip);
        fabBasicText = view.findViewById(R.id.fab_basicSalary_text);
        fabAutoText = view.findViewById(R.id.fab_autoPayslip_text);
        shadowBg = view.findViewById(R.id.shadowView);

        isAllFabsVisible = false;

        fab.setOnClickListener(v -> {
            if (!isAllFabsVisible) {
                fabShow();
            } else {
                fabHide();
            }
        });

        shadowBg.setOnClickListener(v -> {
            fabHide();
        });

        fabAuto.setOnClickListener(v -> {
            MaterialAlertDialogBuilder mDialog = new MaterialAlertDialogBuilder(requireActivity());
            LayoutInflater inflater1 = LayoutInflater.from(requireActivity());
            View dialogView = inflater1.inflate(R.layout.dialog_auto_generate, null);
            String[] paymentList = {"Cash", "Transfer"};

            EditText etFromDate = dialogView.findViewById(R.id.etFromDateAuto);
            EditText etToDate = dialogView.findViewById(R.id.etToDateAuto);
            AutoCompleteTextView tvPayment = dialogView.findViewById(R.id.tvPaymentAuto);

            MaterialDatePicker.Builder mDateBuilder = MaterialDatePicker.Builder.datePicker().setTitleText("Select a Date");
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC+7"));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC+7"));

            etFromDate.setOnClickListener(v1 -> {
                MaterialDatePicker<Long> mDatePicker = mDateBuilder.build();
                mDatePicker.show(requireActivity().getSupportFragmentManager(), null);
                mDatePicker.addOnPositiveButtonClickListener(selection -> {
                    calendar.setTimeInMillis(selection);
                    etFromDate.setText(mDatePicker.getHeaderText());
                    fromDate = dateFormat.format(calendar.getTime());
                });
            });
            etFromDate.setFocusable(false);

            etToDate.setOnClickListener(v1 -> {
                MaterialDatePicker<Long> mDatePicker = mDateBuilder.build();
                mDatePicker.show(requireActivity().getSupportFragmentManager(), null);
                mDatePicker.addOnPositiveButtonClickListener(selection -> {
                    calendar.setTimeInMillis(selection);
                    etToDate.setText(mDatePicker.getHeaderText());
                    toDate = dateFormat.format(calendar.getTime());
                });
            });
            etToDate.setFocusable(false);

            ArrayAdapter<String> paymentAdapter = new ArrayAdapter<>(requireActivity(), R.layout.support_simple_spinner_dropdown_item, paymentList);
            tvPayment.setAdapter(paymentAdapter);
            widget.setActvFocusableFalse(tvPayment);

            mDialog.setView(dialogView)
                    .setTitle("Auto Generate Payslip")
                    .setPositiveButton("Generate", (dialog, which) -> {
                        autoGenerate(fromDate, toDate, tvPayment.getText().toString());
                        dialog.dismiss();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                    });
            AlertDialog alertDialog = mDialog.create();
            alertDialog.show();

            Button positiveButton = alertDialog.getButton((DialogInterface.BUTTON_POSITIVE));
            positiveButton.setBackgroundColor(requireActivity().getResources().getColor(R.color.primary));
            positiveButton.setTextColor(requireActivity().getResources().getColor(R.color.white));
        });

        fabBasic.setOnClickListener(v -> {
            BasicSalaryFragment basicSalaryFragment = new BasicSalaryFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, basicSalaryFragment)
                    .addToBackStack(null)
                    .commit();
        });

        sessionManager = new SessionManager(getContext());
        srlData.setOnRefreshListener(() -> {
            srlData.setRefreshing(true);
            retrieveData();
            srlData.setRefreshing(false);
        });

        if (!sessionManager.getRoleId().equals("1")){
            fab.setVisibility(View.GONE);
        }

        return view;
    }

    private void fabHide() {
        fabBasic.hide();
        fabAuto.hide();
        fabBasicText.setVisibility(View.GONE);
        fabAutoText.setVisibility(View.GONE);
        shadowBg.setVisibility(View.GONE);
        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
        isAllFabsVisible = false;
    }

    private void fabShow() {
        fabBasic.show();
        fabAuto.show();
        fabBasicText.setVisibility(View.VISIBLE);
        fabAutoText.setVisibility(View.VISIBLE);
        shadowBg.setVisibility(View.VISIBLE);
        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));
        isAllFabsVisible = true;
    }

    private void autoGenerate(String fromdate, String todate, String payment) {
        Call<Payslip> autogenerate = apiInterface.payslipAutoGenerate(fromdate, todate, payment);
        autogenerate.enqueue(new Callback<Payslip>() {
            @Override
            public void onResponse(Call<Payslip> call, Response<Payslip> response) {
                if (response.body() != null){
                    if (response.isSuccessful() && response.body().isStatus()){
                        widget.successToast(response.body().getMessage(), requireActivity());
                        retrieveData();
                    } else {
                        widget.errorToast(response.body().getMessage(), requireActivity());
                    }
                }
            }

            @Override
            public void onFailure(Call<Payslip> call, Throwable t) {
                widget.noConnectToast(t.getMessage(), requireActivity());
            }
        });
    }

    public void retrieveData() {
        loadingData.setVisibility(View.VISIBLE);
        int userId = Integer.parseInt(sessionManager.getUserDetail().get(SessionManager.USER_ID));

        if (sessionManager.getRoleId().equals("1")) {
            Call<Payslip> retrieveData = apiInterface.payslipRetrieveData();
            retrieveData.enqueue(new Callback<Payslip>() {
                @Override
                public void onResponse(Call<Payslip> call, Response<Payslip> response) {
                    if (response.body() != null){
                        if (response.isSuccessful() && response.body().isStatus()){
                            payslipDataList = response.body().getData();
                            adData = new AdapterDataPayslip(requireActivity(), payslipDataList, PayslipFragment.this);
                            rvData.setAdapter(adData);
                            adData.notifyDataSetChanged();
                        } else {
                            widget.errorToast(response.body().getMessage(), requireActivity());
                        }
                    }
                    loadingData.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onFailure(Call<Payslip> call, Throwable t) {
                    widget.noConnectToast(t.getMessage(), requireActivity());
                    loadingData.setVisibility(View.INVISIBLE);
                }
            });
        } else {
            Call<Payslip> retrieveData = apiInterface.payslipRetrieveDataUser(userId);
            retrieveData.enqueue(new Callback<Payslip>() {
                @Override
                public void onResponse(Call<Payslip> call, Response<Payslip> response) {
                    if (response.body() != null){
                        if (response.isSuccessful() && response.body().isStatus()){
                            payslipDataList = response.body().getData();
                            adData = new AdapterDataPayslip(requireActivity(), payslipDataList, PayslipFragment.this);
                            rvData.setAdapter(adData);
                            adData.notifyDataSetChanged();
                        } else {
                            widget.errorToast(response.body().getMessage(), requireActivity());
                        }
                    }
                    loadingData.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onFailure(Call<Payslip> call, Throwable t) {
                    widget.noConnectToast(t.getMessage(), requireActivity());
                    loadingData.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        retrieveData();
        requireActivity().setTitle("Payslip");
    }
}