package com.example.rapiertech.ui.payslip;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.example.rapiertech.R;
import com.example.rapiertech.widget.Widget;

public class PayslipEditorFragment extends Fragment {

    private View view;
    private Widget widget;
    private EditText etSalary, etFromDate, etToDate, etAllowance, etDeduction, etOvertime, etOther;
    private AutoCompleteTextView tvEmp, tvPayment, tvStatus;
    private String fromDate, toDate, payment, status;
    private int payslipId, empId, salary, allowance, deduction, overtime, other;

    public PayslipEditorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (getArguments() != null) {
            payslipId = bundle.getInt("id");
            empId = bundle.getInt("empId");
            salary = bundle.getInt("salary");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_payslip_editor, container, false);

        etFromDate = view.findViewById(R.id.etFromDatePayslip);
        etSalary = view.findViewById(R.id.etSalaryPayslip);
        widget = new Widget();

        etSalary.setFocusable(false);
        etSalary.setEnabled(false);

        etFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                widget.successToast("salary "+etSalary.getText().toString(), requireActivity());
            }
        });
        return view;
    }
}