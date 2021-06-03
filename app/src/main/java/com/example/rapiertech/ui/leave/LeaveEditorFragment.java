package com.example.rapiertech.ui.leave;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.rapiertech.R;
import com.example.rapiertech.activity.SessionManager;
import com.example.rapiertech.api.ApiClient;
import com.example.rapiertech.api.ApiInterface;
import com.example.rapiertech.model.employee.Employee;
import com.example.rapiertech.model.employee.EmployeeData;
import com.example.rapiertech.model.leave.Leave;
import com.example.rapiertech.model.leave.LeaveType;
import com.example.rapiertech.model.leave.LeaveTypeData;
import com.example.rapiertech.widget.Widget;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import www.sanju.motiontoast.MotionToast;

public class LeaveEditorFragment extends Fragment {

    private View view;
    private ApiInterface apiInterface;
    private TextView tvId;
    private AutoCompleteTextView tvEmp, tvType, tvStatus;
    private EditText etFromDate, etToDate, etReason, etRejectReason;
    private RadioGroup rgDuration;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private List<EmployeeData> employeeDataList = new ArrayList<>();
    private List<LeaveTypeData> leaveTypeDataList = new ArrayList<>();
    private final String[] statusList = {"Approved", "Pending", "Rejected"};
    private ArrayAdapter<EmployeeData> employeeDataArrayAdapter;
    private ArrayAdapter<LeaveTypeData> leaveTypeDataArrayAdapter;
    private ArrayAdapter<String> statusAdapter;
    private SessionManager sessionManager;
    private TextInputLayout tilStatusLeave, tilEmpLeave, tilRejectReason;
    private int leaveId, leaveTypeId, empId;
    private String fromDate, toDate, reason, duration, status, rejectReason;
    private Long fdTime, tdTime;
    private Widget widget;
    private EditText[] etList;
    private AutoCompleteTextView[] actvList;
    private Menu actionMenu;
    private FragmentManager fm;

    public LeaveEditorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (getArguments() != null) {
            leaveId = bundle.getInt("id");
            empId = bundle.getInt("empId");
            leaveTypeId = bundle.getInt("typeId");
            duration = bundle.getString("duration");
            fromDate = bundle.getString("fromDate");
            toDate = bundle.getString("toDate");
            status = bundle.getString("status");
            reason = bundle.getString("reason");
            rejectReason = bundle.getString("rejectReason");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_leave_editor, container, false);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        widget = new Widget();
        setHasOptionsMenu(true);

        findView();
        
        fm = requireActivity().getSupportFragmentManager();
        calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC+7"));
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC+7"));

        sessionManager = new SessionManager(getContext());
        if (!sessionManager.getRoleId().equals("1")){
            tilStatusLeave.setVisibility(View.GONE);
            tilEmpLeave.setVisibility(View.GONE);
        }
        if (status != null && status.equalsIgnoreCase("Rejected")){
            tilRejectReason.setVisibility(View.VISIBLE);
            etRejectReason.setText(rejectReason);
            widget.disableEditText(etRejectReason);
        }

        etList = new EditText[]{etFromDate, etToDate, etReason};
        actvList = new AutoCompleteTextView[]{tvEmp, tvType, tvStatus};

        if (leaveId != 0) {
            setDataDetails();
            requireActivity().setTitle("Leave Details");
        } else {
            requireActivity().setTitle("New Leave");
        }

        initUi();

        return view;
    }

    private void findView() {
        tvId = view.findViewById(R.id.tvIdLeaveEditor);
        tvEmp = view.findViewById(R.id.tvEmpLeaveEditor);
        tvType = view.findViewById(R.id.tvTypeLeaveEditor);
        tvStatus = view.findViewById(R.id.tvStatusLeaveEditor);
        etFromDate = view.findViewById(R.id.etFromDateLeave);
        etToDate = view.findViewById(R.id.etToDateLeave);
        etReason = view.findViewById(R.id.etReasonLeaveEditor);
        rgDuration = view.findViewById(R.id.rgDurationLeaveEditor);
        tilEmpLeave = view.findViewById(R.id.tfEmpLeaveEditor);
        tilStatusLeave = view.findViewById(R.id.tfStatusLeaveEditor);
        tilRejectReason = view.findViewById(R.id.tfRejectReasonLeaveEditor);
        etRejectReason = view.findViewById(R.id.etRejectReasonLeaveEditor);
    }

    private void initUi() {
        Call<Employee> empData = apiInterface.employeeRetrieveData();
        empData.enqueue(new Callback<Employee>() {
            @Override
            public void onResponse(Call<Employee> call, Response<Employee> response) {
                if (response.body() != null) {
                    if (response.isSuccessful() && response.body().isStatus()) {
                        employeeDataList = response.body().getData();
                        for (int i = 0; i < employeeDataList.size(); i++){
                            if (empId == employeeDataList.get(i).getId()){
                                tvEmp.setText(employeeDataList.get(i).getName());
                            }
                        }
                        employeeDataArrayAdapter = new ArrayAdapter<>(requireActivity(), R.layout.support_simple_spinner_dropdown_item, employeeDataList);
                        tvEmp.setAdapter(employeeDataArrayAdapter);
                        tvEmp.setOnItemClickListener((parent, view, position, id) -> {
                            empId = employeeDataList.get(position).getId();
                        });
                    } else {
                        widget.errorToast(response.body().getMessage(), requireActivity());
                    }
                }
            }

            @Override
            public void onFailure(Call<Employee> call, Throwable t) {
                widget.noConnectToast(t.getMessage(), requireActivity());
            }
        });

        Call<LeaveType> leaveTypeData = apiInterface.leaveTypeRetrieveData();
        leaveTypeData.enqueue(new Callback<LeaveType>() {
            @Override
            public void onResponse(Call<LeaveType> call, Response<LeaveType> response) {
                if (response.body() != null) {
                    if (response.isSuccessful() && response.body().isStatus()) {
                        leaveTypeDataList = response.body().getData();
                        for (int i = 0; i < leaveTypeDataList.size(); i++){
                            if (leaveTypeId == leaveTypeDataList.get(i).getId()){
                                tvType.setText(leaveTypeDataList.get(i).getTypeName());
                            }
                        }
                        leaveTypeDataArrayAdapter = new ArrayAdapter<>(requireActivity(), R.layout.support_simple_spinner_dropdown_item, leaveTypeDataList);
                        tvType.setAdapter(leaveTypeDataArrayAdapter);
                        tvType.setOnItemClickListener((parent, view, position, id) -> {
                            leaveTypeId = leaveTypeDataList.get(position).getId();
                        });
                    } else {
                        widget.errorToast(response.body().getMessage(), requireActivity());
                    }
                }
            }

            @Override
            public void onFailure(Call<LeaveType> call, Throwable t) {
                widget.noConnectToast(t.getMessage(), requireActivity());
            }
        });

        statusAdapter = new ArrayAdapter<>(requireActivity(), R.layout.support_simple_spinner_dropdown_item, statusList);
        tvStatus.setAdapter(statusAdapter);

        MaterialDatePicker.Builder mDateBuilder = MaterialDatePicker.Builder.datePicker().setTitleText("Select a Date");

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

        for (int i = 0; i < actvList.length; i++){
            widget.setActvFocusableFalse(actvList[i]);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_update, menu);
        actionMenu = menu;

        if (leaveId != 0){
            setMenu(true, true, false, false, false);
        } else {
            setMenu(false, false, true, false, false);
        }

        if (status != null && !status.equalsIgnoreCase("Pending")) {
            setMenu(false, true, false, false, false);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setMenu(boolean bEdit, boolean bDelete, boolean bSave, boolean bUpdate, boolean bCancel) {
        actionMenu.findItem(R.id.menu_edit).setVisible(bEdit);
        actionMenu.findItem(R.id.menu_delete).setVisible(bDelete);
        actionMenu.findItem(R.id.menu_save).setVisible(bSave);
        actionMenu.findItem(R.id.menu_update).setVisible(bUpdate);
        actionMenu.findItem(R.id.menu_cancel).setVisible(bCancel);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        if (sessionManager.getRoleId().equals("1")){
            status = tvStatus.getText().toString();
        } else {
            status = "Pending";
            empId = Integer.parseInt(Objects.requireNonNull(sessionManager.getUserDetail().get(SessionManager.USER_ID)));
        }
        reason = etReason.getText().toString();

        int radioButtonID = rgDuration.getCheckedRadioButtonId();
        RadioButton rb = view.findViewById(radioButtonID);
        duration = rb.getText().toString();

        Boolean allFieldsChecked = validation();

        switch (item.getItemId()){
            case R.id.menu_save:
                if (allFieldsChecked){
                    saveData();
                }
                return true;

            case R.id.menu_edit:
                editView();
                setMenu(false, false, false, true, true);
                return true;

            case R.id.menu_cancel:
                detailView();
                setMenu(true, true, false, false, false);
                return true;

            case R.id.menu_update:
                if (allFieldsChecked){
                    updateData();
                }

                return true;

            case R.id.menu_delete:
                BottomSheetMaterialDialog mDialog = new BottomSheetMaterialDialog.Builder(requireActivity())
                        .setTitle("Delete?")
                        .setMessage("Are you sure want to delete this data?")
                        .setCancelable(false)
                        .setPositiveButton("Delete", R.drawable.ic_delete_, (dialogInterface, which) -> {
                            deleteData();
                            dialogInterface.dismiss();
                        })
                        .setNegativeButton("Cancel", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss())
                        .build();
                mDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateData() {
        Call<Leave> updateData = apiInterface.leaveUpdateData(leaveId, empId, leaveTypeId, duration, fromDate, toDate, status, reason);
        updateData.enqueue(new Callback<Leave>() {
            @Override
            public void onResponse(Call<Leave> call, Response<Leave> response) {
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
            public void onFailure(Call<Leave> call, Throwable t) {
                widget.noConnectToast(t.getMessage(), requireActivity());
            }
        });
    }

    private void deleteData() {
        Call<Leave> deleteData = apiInterface.leaveDeleteData(leaveId);
        deleteData.enqueue(new Callback<Leave>() {
            @Override
            public void onResponse(Call<Leave> call, Response<Leave> response) {
                if (response.body() != null){
                    if (response.isSuccessful() && response.body().isStatus()){
                        widget.successToast(response.body().getMessage(), requireActivity());
                        fm.popBackStack();
                    } else {
                        widget.errorToast(response.body().getMessage(), requireActivity());
                    }
                }
            }

            @Override
            public void onFailure(Call<Leave> call, Throwable t) {
                widget.noConnectToast(t.getMessage(), requireActivity());
            }
        });
    }

    private void saveData() {
        Call<Leave> createData = apiInterface.leaveCreateData(empId, leaveTypeId, duration, fromDate, toDate, status, reason);
        createData.enqueue(new Callback<Leave>() {
            @Override
            public void onResponse(Call<Leave> call, Response<Leave> response) {
                if (response.body() != null){
                    if (response.isSuccessful() && response.body().isStatus()){
                        widget.successToast(response.body().getMessage(), requireActivity());
                        fm.popBackStack();
                    } else {
                        widget.errorToast(response.body().getMessage(), requireActivity());
                    }
                }
            }

            @Override
            public void onFailure(Call<Leave> call, Throwable t) {
                widget.noConnectToast(t.getMessage(), requireActivity());
            }
        });
    }

    private boolean validation() {
        String message = "Required!";
        if (reason.isEmpty()){
            etReason.setError(message);
            return false;
        }
        return true;
    }

    private void setDataDetails() {
        tvId.setText(String.valueOf(leaveId));
        etReason.setText(reason);
        etFromDate.setText(fromDate);
        etToDate.setText(toDate);
        tvStatus.setText(status);
        
        detailView();
    }

    private void detailView() {
        for (int i = 0; i < etList.length; i++){
            widget.disableEditText(etList[i]);
        }
        for (int i = 0; i < actvList.length; i++){
            widget.disableAutoCompleteTextView(actvList[i]);
        }
        for(int i = 0; i < rgDuration.getChildCount(); i++){
            (rgDuration.getChildAt(i)).setEnabled(false);
        }
    }

    private void editView() {
        for (int i = 0; i < etList.length; i++){
            widget.enableEditText(etList[i]);
        }
        for (int i = 0; i < actvList.length; i++){
            widget.enableAutoCompleteTextView(actvList[i]);
        }
        for(int i = 0; i < rgDuration.getChildCount(); i++){
            (rgDuration.getChildAt(i)).setEnabled(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}