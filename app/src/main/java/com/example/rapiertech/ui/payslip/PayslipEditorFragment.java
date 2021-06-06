package com.example.rapiertech.ui.payslip;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.example.rapiertech.R;
import com.example.rapiertech.api.ApiClient;
import com.example.rapiertech.api.ApiInterface;
import com.example.rapiertech.model.payslip.Payslip;
import com.example.rapiertech.widget.Widget;
import com.google.android.material.datepicker.MaterialDatePicker;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayslipEditorFragment extends Fragment {

    private View view;
    private Widget widget;
    private EditText etEmp, etSalary, etFromDate, etToDate, etAllowance, etDeduction, etOvertime, etOther;
    private AutoCompleteTextView tvPayment, tvStatus;
    private String empName, fromDate, toDate, payment, status;
    private int payslipId, salary, allowance, deduction, overtime, other;
    private Boolean isEditable;
    private EditText[] etList;
    private AutoCompleteTextView[] actvList;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private Long fdTime, tdTime;
    private String[] paymentList, statusList;
    private Menu actionMenu;
    private ApiInterface apiInterface;
    private FragmentManager fm;

    public PayslipEditorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (getArguments() != null) {
            payslipId = bundle.getInt("id");
            empName = bundle.getString("empName");
            salary = bundle.getInt("salary");
            fromDate = bundle.getString("fromDate");
            toDate = bundle.getString("toDate");
            allowance = bundle.getInt("allowances");
            deduction = bundle.getInt("deductions");
            overtime = bundle.getInt("overtimes");
            other = bundle.getInt("others");
            payment = bundle.getString("payment");
            status = bundle.getString("status");
            isEditable = bundle.getBoolean("isEditable");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_payslip_editor, container, false);
        widget = new Widget();
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        fm = requireActivity().getSupportFragmentManager();

        setHasOptionsMenu(true);
        findView();

        etList = new EditText[]{etEmp, etSalary, etFromDate, etToDate, etAllowance, etDeduction, etOvertime, etOther};
        actvList = new AutoCompleteTextView[]{tvPayment, tvStatus};
        paymentList = new String[]{"Transfer", "Cash"};
        statusList = new String[]{"Paid Off", "In Progress", "Cancel"};

        initUi();

        return view;
    }

    private void initUi() {
        if (!isEditable){
            for (EditText editText : etList) {
                widget.disableEditText(editText);
            }
            for (AutoCompleteTextView autoCompleteTextView : actvList) {
                widget.disableAutoCompleteTextView(autoCompleteTextView);
                widget.setActvFocusableFalse(autoCompleteTextView);
            }
        } else {
            widget.disableEditText(etEmp);
            widget.disableEditText(etSalary);
            for (AutoCompleteTextView autoCompleteTextView : actvList) {
                widget.setActvFocusableFalse(autoCompleteTextView);
            }
        }

        etEmp.setText(empName);
        etSalary.setText(widget.formatRupiah((double) salary));
        etFromDate.setText(widget.changeDateFormat(fromDate));
        etToDate.setText(widget.changeDateFormat(toDate));
        etAllowance.setText(widget.editTextFormatRupiah((double) allowance));
        etDeduction.setText(widget.editTextFormatRupiah((double) deduction));
        etOvertime.setText(widget.editTextFormatRupiah((double) overtime));
        etOther.setText(widget.editTextFormatRupiah((double) other));
        tvPayment.setText(widget.capitalizeText(payment));
        tvStatus.setText(widget.capitalizeText(status));

        MaterialDatePicker.Builder mDateBuilder = MaterialDatePicker.Builder.datePicker().setTitleText("Select a Date");
        calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC+7"));
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC+7"));

        etFromDate.setOnClickListener(v -> {
            if (fromDate != null){
                try {
                    Date fd = dateFormat.parse(fromDate);
                    fdTime = fd.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                mDateBuilder.setSelection(fdTime);
            }
            MaterialDatePicker<Long> mDatePicker = mDateBuilder.build();
            mDatePicker.show(requireActivity().getSupportFragmentManager(), null);
            mDatePicker.addOnPositiveButtonClickListener(selection -> {
                calendar.setTimeInMillis(selection);
                etFromDate.setText(mDatePicker.getHeaderText());
                fromDate = dateFormat.format(calendar.getTime());
            });
        });
        etFromDate.setFocusable(false);

        etToDate.setOnClickListener(v -> {
            if (toDate != null){
                try {
                    Date td = dateFormat.parse(toDate);
                    tdTime = td.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                mDateBuilder.setSelection(tdTime);
            }
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
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(requireActivity(), R.layout.support_simple_spinner_dropdown_item, statusList);
        tvStatus.setAdapter(statusAdapter);

        widget.editTextFormatRupiahChangeListener(etDeduction);
        widget.editTextFormatRupiahChangeListener(etAllowance);
        widget.editTextFormatRupiahChangeListener(etOvertime);
        widget.editTextFormatRupiahChangeListener(etOther);
    }

    private void findView() {
        etSalary = view.findViewById(R.id.etSalaryPayslip);
        etFromDate = view.findViewById(R.id.etFromDatePayslip);
        etToDate = view.findViewById(R.id.etToDatePayslip);
        etAllowance = view.findViewById(R.id.etAllowancePayslip);
        etDeduction = view.findViewById(R.id.etDeductionPayslip);
        etOvertime = view.findViewById(R.id.etOvertimePayslip);
        etOther = view.findViewById(R.id.etOtherPayslip);
        etEmp = view.findViewById(R.id.etEmpPayslip);
        tvPayment = view.findViewById(R.id.tvPaymentPayslip);
        tvStatus = view.findViewById(R.id.tvStatusPayslip);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_update, menu);
        actionMenu = menu;

        setMenu(false, false, false, isEditable, false);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        allowance = widget.getDigitOnly(etAllowance.getText().toString());
        deduction = widget.getDigitOnly(etDeduction.getText().toString());
        overtime = widget.getDigitOnly(etOvertime.getText().toString());
        other = widget.getDigitOnly(etOther.getText().toString());
        payment = tvPayment.getText().toString();
        status = tvStatus.getText().toString();

        int itemId = item.getItemId();
        if (itemId == R.id.menu_update) {
            updateData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setMenu(boolean bEdit, boolean bDelete, boolean bSave, boolean bUpdate, boolean bCancel) {
        actionMenu.findItem(R.id.menu_edit).setVisible(bEdit);
        actionMenu.findItem(R.id.menu_delete).setVisible(bDelete);
        actionMenu.findItem(R.id.menu_save).setVisible(bSave);
        actionMenu.findItem(R.id.menu_update).setVisible(bUpdate);
        actionMenu.findItem(R.id.menu_cancel).setVisible(bCancel);
    }

    private void updateData() {
        Call<Payslip> updateData = apiInterface.payslipUpdateData(payslipId, fromDate, toDate, allowance, deduction, overtime, other, payment, status);
        updateData.enqueue(new Callback<Payslip>() {
            @Override
            public void onResponse(Call<Payslip> call, Response<Payslip> response) {
                if (response.body() != null) {
                    if (response.isSuccessful() && response.body().isStatus()) {
                        widget.successToast(response.body().getMessage(), requireActivity());
                        fm.popBackStack();
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
}