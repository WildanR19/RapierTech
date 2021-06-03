package com.example.rapiertech.ui.admin.employee;

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
import android.widget.TextView;

import com.example.rapiertech.R;
import com.example.rapiertech.api.ApiClient;
import com.example.rapiertech.api.ApiInterface;
import com.example.rapiertech.model.department.Department;
import com.example.rapiertech.model.department.DepartmentData;
import com.example.rapiertech.model.empdetail.EmpDetail;
import com.example.rapiertech.model.employee.Employee;
import com.example.rapiertech.model.empstatus.EmpStatus;
import com.example.rapiertech.model.empstatus.EmpStatusData;
import com.example.rapiertech.model.job.Job;
import com.example.rapiertech.model.job.JobData;
import com.example.rapiertech.model.role.Role;
import com.example.rapiertech.model.role.RoleData;
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
import java.util.TimeZone;

import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import www.sanju.motiontoast.MotionToast;

public class EmployeeDetailsFragment extends Fragment {

    private ApiInterface apiInterface;
    private EditText etName, etEmail, etPassword, etAddress, etPhone, etJoin, etLast;
    private View view;
    private AutoCompleteTextView tvJob, tvDept, tvGender, tvRole, tvStatus;
    private TextView tvId;
    private List<JobData> jobList = new ArrayList<>();
    private List<DepartmentData> deptList = new ArrayList<>();
    private List<RoleData> roleList = new ArrayList<>();
    private List<EmpStatusData> statusList = new ArrayList<>();
    private String[] genderList = {"Male", "Female"};
    private ArrayAdapter<DepartmentData> deptAdapter;
    private ArrayAdapter<JobData> jobAdapter;
    private ArrayAdapter<RoleData> roleAdapter;
    private ArrayAdapter<EmpStatusData> statusAdapter;
    private ArrayAdapter<String> genderAdapter;
    private int empId, deptId, jobId, statusId, roleId;
    private String name, email, password, address, phone, joinDate, lastDate, gender;
    private Long jdTime, ldTime;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private EditText[] etList;
    private AutoCompleteTextView[] actvList;
    private Menu actionMenu;
    private TextInputLayout tlPassword;
    private FragmentManager fm;
    private Widget widget;

    public EmployeeDetailsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (getArguments() != null) {
            empId = bundle.getInt("id");
            name = bundle.getString("name");
            email = bundle.getString("email");
            jobId = bundle.getInt("jobId");
            deptId = bundle.getInt("departmentId");
            roleId = bundle.getInt("roleId");
            address = bundle.getString("address");
            phone = bundle.getString("phone");
            gender = bundle.getString("gender");
            statusId = bundle.getInt("empStatusId");
            joinDate = bundle.getString("joinDate");
            lastDate = bundle.getString("lastDate");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_employee_details, container, false);
        widget = new Widget();
        setHasOptionsMenu(true);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        fm = requireActivity().getSupportFragmentManager();

        findView();

        calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC+7"));
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC+7"));

        etList = new EditText[]{etName, etEmail, etPassword, etAddress, etPhone, etJoin, etLast};
        actvList = new AutoCompleteTextView[]{tvDept, tvGender, tvStatus, tvRole, tvJob};

        if (empId != 0) {
            setDataUpdate();
            requireActivity().setTitle("Employee Details");
            tlPassword.setHelperTextEnabled(false);
        } else {
            requireActivity().setTitle("New Employee");
        }

        initUi();
        return view;
    }

    private void setDataUpdate() {
        tvId.setText(String.valueOf(empId));
        etName.setText(name);
        etEmail.setText(email);
        etAddress.setText(address);
        tvGender.setText(gender);
        etPhone.setText(phone);
        etJoin.setText(joinDate);
        etLast.setText(lastDate);

        detailView();
    }

    private void findView() {
        tvId = view.findViewById(R.id.tvIdDetail);
        etName = view.findViewById(R.id.etNameDetail);
        etEmail = view.findViewById(R.id.etEmailDetail);
        etPassword = view.findViewById(R.id.etPasswordDetail);
        etAddress = view.findViewById(R.id.etAddressDetail);
        etPhone = view.findViewById(R.id.etPhoneDetail);
        tvDept = view.findViewById(R.id.tvDeptDetail);
        tvJob = view.findViewById(R.id.tvJobDetail);
        tvRole = view.findViewById(R.id.tvRoleDetail);
        tvGender = view.findViewById(R.id.tvGenderDetail);
        tvStatus = view.findViewById(R.id.tvStatusDetail);
        etJoin = view.findViewById(R.id.etJoinDetail);
        etLast = view.findViewById(R.id.etLastDetail);
        tlPassword = view.findViewById(R.id.tfPasswordDetail);
    }

    private void initUi() {
        Call<Department> deptdata = apiInterface.departmentRetrieveData();
        deptdata.enqueue(new Callback<Department>() {
            @Override
            public void onResponse(Call<Department> call, Response<Department> response) {
                if (response.isSuccessful()){
                    deptList = response.body().getData();
                    for (int i = 0; i < deptList.size(); i++){
                        if (deptId == deptList.get(i).getId()){
                            tvDept.setText(deptList.get(i).getName());
                        }
                    }
                    deptAdapter = new ArrayAdapter<>(requireActivity(), R.layout.support_simple_spinner_dropdown_item, deptList);
                    tvDept.setAdapter(deptAdapter);
                    tvDept.setOnItemClickListener((parent, view, position, id) -> {
                        deptId = deptList.get(position).getId();
                    });
                }else {
                    widget.errorToast(response.body().getMessage(), requireActivity());
                }
            }

            @Override
            public void onFailure(Call<Department> call, Throwable t) {
                widget.noConnectToast(t.getMessage(), requireActivity());
            }
        });

        Call<Job> jobData = apiInterface.jobRetrieveData();
        jobData.enqueue(new Callback<Job>() {
            @Override
            public void onResponse(Call<Job> call, Response<Job> response) {
                if (response.isSuccessful()){
                    jobList = response.body().getData();
                    for (int i = 0; i < jobList.size(); i++){
                        if (jobId == jobList.get(i).getId()){
                            tvJob.setText(jobList.get(i).getName());
                        }
                    }

                    jobAdapter = new ArrayAdapter<>(requireActivity(), R.layout.support_simple_spinner_dropdown_item, jobList);
                    tvJob.setAdapter(jobAdapter);
                    tvJob.setOnItemClickListener((parent, view, position, id) -> {
                        jobId = jobList.get(position).getId();
                    });
//                    List<String> listJob = new ArrayList<>();
//                    for (int i = 0; i < jobList.size(); i++){
//                        listJob.add(jobList.get(i).getName());
//                    }
//
//                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), R.layout.support_simple_spinner_dropdown_item, listJob);
//                    tvJob.setAdapter(adapter);
//                    tvJob.setOnItemClickListener((parent, view, position, id) -> {
//                        jobName = parent.getItemAtPosition(position).toString();
//                        jobId = jobList.get(position).getId();
//                    });
                }else {
                    widget.errorToast(response.body().getMessage(), requireActivity());
                }
            }

            @Override
            public void onFailure(Call<Job> call, Throwable t) {
                widget.noConnectToast(t.getMessage(), requireActivity());
            }
        });

        Call<Role> roleData = apiInterface.roleRetrieveData();
        roleData.enqueue(new Callback<Role>() {
            @Override
            public void onResponse(Call<Role> call, Response<Role> response) {
                if (response.isSuccessful()){
                    roleList = response.body().getData();
                    for (int i = 0; i < roleList.size(); i++){
                        if (roleId == roleList.get(i).getId()){
                            tvRole.setText(roleList.get(i).getName());
                        }
                    }

                    roleAdapter = new ArrayAdapter<>(requireActivity(), R.layout.support_simple_spinner_dropdown_item, roleList);
                    tvRole.setAdapter(roleAdapter);
                    tvRole.setOnItemClickListener((parent, view, position, id) -> {
                        roleId = roleList.get(position).getId();
                    });
                }else {
                    widget.errorToast(response.body().getMessage(), requireActivity());
                }
            }

            @Override
            public void onFailure(Call<Role> call, Throwable t) {
                widget.noConnectToast(t.getMessage(), requireActivity());
            }
        });

        Call<EmpStatus> statusData = apiInterface.statusRetrieveData();
        statusData.enqueue(new Callback<EmpStatus>() {
            @Override
            public void onResponse(Call<EmpStatus> call, Response<EmpStatus> response) {
                if (response.isSuccessful()){
                    statusList = response.body().getData();
                    for (int i = 0; i < statusList.size(); i++){
                        if (statusId == statusList.get(i).getId()){
                            tvStatus.setText(statusList.get(i).getStatusName());
                        }
                    }

                    statusAdapter = new ArrayAdapter<>(requireActivity(), R.layout.support_simple_spinner_dropdown_item, statusList);
                    tvStatus.setAdapter(statusAdapter);
                    tvStatus.setOnItemClickListener((parent, view, position, id) -> {
                        statusId = statusList.get(position).getId();
                    });
                } else {
                    widget.errorToast(response.body().getMessage(), requireActivity());
                }
            }

            @Override
            public void onFailure(Call<EmpStatus> call, Throwable t) {
                widget.noConnectToast(t.getMessage(), requireActivity());
            }
        });

        genderAdapter =  new ArrayAdapter<>(requireActivity(), R.layout.support_simple_spinner_dropdown_item, genderList);
        tvGender.setAdapter(genderAdapter);

        MaterialDatePicker.Builder mDateBuilder = MaterialDatePicker.Builder.datePicker().setTitleText("Select a Date");

        etJoin.setOnClickListener(v -> {
            if (joinDate != null){
                try {
                    Date jd = dateFormat.parse(joinDate);
                    jdTime = jd.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                mDateBuilder.setSelection(jdTime);
            }
            MaterialDatePicker<Long> mDatePicker = mDateBuilder.build();
            mDatePicker.show(requireActivity().getSupportFragmentManager(), null);
            mDatePicker.addOnPositiveButtonClickListener(selection -> {
                calendar.setTimeInMillis(selection);
                etJoin.setText(mDatePicker.getHeaderText());
                joinDate = dateFormat.format(calendar.getTime());
            });
        });
        etJoin.setFocusable(false);

        etLast.setOnClickListener(v -> {
            if (lastDate != null){
                try {
                    Date ld = dateFormat.parse(lastDate);
                    ldTime = ld.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                mDateBuilder.setSelection(ldTime);
            }
            MaterialDatePicker<Long> mDatePicker = mDateBuilder.build();
            mDatePicker.show(requireActivity().getSupportFragmentManager(), null);
            mDatePicker.addOnPositiveButtonClickListener(selection -> {
                calendar.setTimeInMillis(selection);
                lastDate = dateFormat.format(calendar.getTime());
                etLast.setText(mDatePicker.getHeaderText());
            });
        });
        etLast.setFocusable(false);

        for (int i = 0; i < actvList.length; i++){
            widget.setActvFocusableFalse(actvList[i]);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_update, menu);
        actionMenu = menu;

        if (empId != 0){
            setMenu(true, true, false, false, false);
        } else {
            setMenu(false, false, true, false, false);
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
        name = etName.getText().toString();
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        address = etAddress.getText().toString();
        phone = etPhone.getText().toString();
        gender = tvGender.getText().toString();
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

    private void deleteData() {
        Call<Employee> deleteData  = apiInterface.employeeDeleteData(empId);
        deleteData.enqueue(new Callback<Employee>() {
            @Override
            public void onResponse(Call<Employee> call, Response<Employee> response) {
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
            public void onFailure(Call<Employee> call, Throwable t) {
                widget.noConnectToast(t.getMessage(), requireActivity());
            }
        });
    }

    private void updateData() {
        Call<EmpDetail> updateData = apiInterface.employeeUpdateData(empId, name, email, password, roleId, address, deptId, jobId, phone, gender, joinDate, lastDate, statusId);
        updateData.enqueue(new Callback<EmpDetail>() {
            @Override
            public void onResponse(Call<EmpDetail> call, Response<EmpDetail> response) {
                if (response.isSuccessful() && response.body().isStatus()){
                    widget.successToast(response.body().getMessage(), requireActivity());
                    fm.popBackStack();
                } else {
                    widget.errorToast(response.body().getMessage(), requireActivity());
                }
            }

            @Override
            public void onFailure(Call<EmpDetail> call, Throwable t) {
                widget.noConnectToast(t.getMessage(), requireActivity());
            }
        });
    }

    private void detailView() {
        for (int i = 0; i < etList.length; i++){
            widget.disableEditText(etList[i]);
        }
        for (int i = 0; i < actvList.length; i++){
            widget.disableAutoCompleteTextView(actvList[i]);
        }
        tlPassword.setHelperTextEnabled(false);
    }

    private void editView() {
        for (int i = 0; i < etList.length; i++){
            widget.enableEditText(etList[i]);
        }
        for (int i = 0; i < actvList.length; i++){
            widget.enableAutoCompleteTextView(actvList[i]);
        }
        tlPassword.setHelperText(getString(R.string.helper_password));
    }

    private boolean validation() {
        String message = "Required!";
        if (name.isEmpty()){
            etName.setError(message);
            return false;
        } else if (email.isEmpty()){
            etEmail.setError(message);
            return false;
        } else if (password.isEmpty()){
            if (empId == 0){
                etPassword.setError(message);
                return false;
            }
        } else if (address.isEmpty()){
            etAddress.setError(message);
            return false;
        } else if (phone.isEmpty()) {
            etPhone.setError(message);
            return false;
        }
//         else if (gender.isEmpty()){
//            tvGender.setError(message);
//            return false;
//        } else if (deptId == 0){
//            tvDept.setError(message);
//            return false;
//        } else if (jobId == 0){
//            tvJob.setError(message);
//            return false;
//        } else if (roleId == 0){
//            tvRole.setError(message);
//            return false;
//        } else if (statusId == 0){
//            tvStatus.setError(message);
//            return false;
//        } else if (joinDate.isEmpty()){
//            etJoin.setError(message);
//            return false;
//        } else if (lastDate.isEmpty()){
//            etLast.setError(message);
//            return false;
//        }
        return true;
    }

    private void saveData() {

        Call<EmpDetail> createData = apiInterface.employeeCreateData(name, email, password, roleId, address, deptId, jobId, phone, gender, joinDate, lastDate, statusId);
        createData.enqueue(new Callback<EmpDetail>() {
            @Override
            public void onResponse(Call<EmpDetail> call, Response<EmpDetail> response) {
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
            public void onFailure(Call<EmpDetail> call, Throwable t) {
                widget.noConnectToast(t.getMessage(), requireActivity());
            }
        });
    }


}