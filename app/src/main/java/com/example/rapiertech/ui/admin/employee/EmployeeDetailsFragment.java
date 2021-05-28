package com.example.rapiertech.ui.admin.employee;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.example.rapiertech.R;
import com.example.rapiertech.api.ApiClient;
import com.example.rapiertech.api.ApiInterface;
import com.example.rapiertech.model.department.Department;
import com.example.rapiertech.model.department.DepartmentData;
import com.example.rapiertech.model.empstatus.EmpStatus;
import com.example.rapiertech.model.empstatus.EmpStatusData;
import com.example.rapiertech.model.job.Job;
import com.example.rapiertech.model.job.JobData;
import com.example.rapiertech.model.role.Role;
import com.example.rapiertech.model.role.RoleData;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import www.sanju.motiontoast.MotionToast;

public class EmployeeDetailsFragment extends Fragment {

    private EditText etName, etEmail, etPassword, etAddress, etPhone, etJoin, etLast;
    private View view;
    private AutoCompleteTextView tvJob, tvDept, tvGender, tvRole, tvStatus;
    private TextView tvId;
    private List<JobData> jobList = new ArrayList<>();
    private List<DepartmentData> deptList = new ArrayList<>();
    private List<RoleData> roleList = new ArrayList<>();
    private List<EmpStatusData> statusList = new ArrayList<>();
    private String[] genderList = {"Male", "Female"};
    private ArrayAdapter<JobData> jobAdapter;
    private ArrayAdapter<DepartmentData> deptAdapter;
    private ArrayAdapter<RoleData> roleAdapter;
    private ArrayAdapter<EmpStatusData> statusAdapter;
    private ArrayAdapter<String> genderAdapter;
    private int empId, deptId, jobId, statusId;
    private String name, email, job, role, address, phone, joinDate, lastDate;
    private Long jdTime, ldTime;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;

    public EmployeeDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (getArguments() != null) {
            empId = bundle.getInt("id");
            name = bundle.getString("name");
            email = bundle.getString("email");
            job = bundle.getString("job");
            role = bundle.getString("role");
            address = bundle.getString("address");
            phone = bundle.getString("phone");
            deptId = bundle.getInt("departmentId");
            statusId = bundle.getInt("empStatusId");
            joinDate = bundle.getString("joinDate");
            lastDate = bundle.getString("lastDate");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_employee_details, container, false);

        setHasOptionsMenu(true);

        calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC+7"));
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC+7"));

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

        tvId.setText(String.valueOf(empId));
        etName.setText(name);
        etEmail.setText(email);
        etAddress.setText(address);
        etPhone.setText(phone);
        tvJob.setText(job);
        tvRole.setText(role);
        etJoin.setText(joinDate);
        etLast.setText(lastDate);

        initUi();
        return view;
    }

    private void initUi() {
        ApiInterface apiData = ApiClient.getClient().create(ApiInterface.class);

        deptAdapter = new ArrayAdapter<>(requireActivity(), R.layout.support_simple_spinner_dropdown_item, deptList);
        Call<Department> deptdata = apiData.departmentRetrieveData();
        deptdata.enqueue(new Callback<Department>() {
            @Override
            public void onResponse(Call<Department> call, Response<Department> response) {
                if (response.isSuccessful()){
                    for (DepartmentData post : response.body().getData()){
                        String name = post.getName();
                        DepartmentData dd = new DepartmentData(name);
                        deptList.add(dd);

                        tvDept.setAdapter(deptAdapter);
                    }
                    tvDept.setAdapter(deptAdapter);
                }else {
                    MotionToast.Companion.createColorToast(requireActivity(), "Error",
                            response.body().getMessage(),
                            MotionToast.TOAST_ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(requireActivity(),R.font.helvetica_regular)
                    );
                }
            }

            @Override
            public void onFailure(Call<Department> call, Throwable t) {
                MotionToast.Companion.createColorToast(requireActivity(), "Cannot connect server",
                        t.getMessage(),
                        MotionToast.TOAST_ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(requireActivity(),R.font.helvetica_regular)
                );
            }
        });
        tvDept.setFocusable(false);
        tvDept.setFocusableInTouchMode(false);

        jobAdapter = new ArrayAdapter<>(requireActivity(), R.layout.support_simple_spinner_dropdown_item, jobList);
        Call<Job> jobData = apiData.jobRetrieveData();
        jobData.enqueue(new Callback<Job>() {
            @Override
            public void onResponse(Call<Job> call, Response<Job> response) {
                if (response.isSuccessful()){
                    for (JobData post : response.body().getData()){
                        String name = post.getName();
                        JobData jd = new JobData(name);
                        jobList.add(jd);

                        tvJob.setAdapter(jobAdapter);
                        tvJob.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                JobData jd = (JobData) parent.getItemAtPosition(position);
                                jobId = jd.getId();
                                String jobName = jd.getName();
                                MotionToast.Companion.createColorToast(requireActivity(), "Info",
                                        "ID : " + jobId + ", Name : " + jobName,
                                        MotionToast.TOAST_INFO,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.LONG_DURATION,
                                        ResourcesCompat.getFont(requireActivity(),R.font.helvetica_regular)
                                );
                            }
                        });
                    }
                }else {
                    MotionToast.Companion.createColorToast(requireActivity(), "Error",
                            response.body().getMessage(),
                            MotionToast.TOAST_ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(requireActivity(),R.font.helvetica_regular)
                    );
                }
            }

            @Override
            public void onFailure(Call<Job> call, Throwable t) {
                MotionToast.Companion.createColorToast(requireActivity(), "Cannot connect server",
                        t.getMessage(),
                        MotionToast.TOAST_NO_INTERNET,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(requireActivity(),R.font.helvetica_regular)
                );
            }
        });
        tvJob.setFocusable(false);
        tvJob.setFocusableInTouchMode(false);

        roleAdapter = new ArrayAdapter<>(requireActivity(), R.layout.support_simple_spinner_dropdown_item, roleList);
        Call<Role> roleData = apiData.roleRetrieveData();
        roleData.enqueue(new Callback<Role>() {
            @Override
            public void onResponse(Call<Role> call, Response<Role> response) {
                if (response.isSuccessful()){
                    for (RoleData post : response.body().getData()){
                        String name = post.getName();
                        RoleData rd = new RoleData(name);

                        roleList.add(rd);
                        tvRole.setAdapter(roleAdapter);
                    }
                }else {
                    MotionToast.Companion.createColorToast(requireActivity(), "Error",
                            response.body().getMessage(),
                            MotionToast.TOAST_ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(requireActivity(),R.font.helvetica_regular)
                    );
                }
            }

            @Override
            public void onFailure(Call<Role> call, Throwable t) {
                MotionToast.Companion.createColorToast(requireActivity(), "Cannot connect server",
                        t.getMessage(),
                        MotionToast.TOAST_ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(requireActivity(),R.font.helvetica_regular)
                );
            }
        });
        tvRole.setFocusable(false);
        tvRole.setFocusableInTouchMode(false);

        statusAdapter = new ArrayAdapter<>(requireActivity(), R.layout.support_simple_spinner_dropdown_item, statusList);
        Call<EmpStatus> statusData = apiData.statusRetrieveData();
        statusData.enqueue(new Callback<EmpStatus>() {
            @Override
            public void onResponse(Call<EmpStatus> call, Response<EmpStatus> response) {
                if (response.isSuccessful()){
                    for (EmpStatusData post : response.body().getData()){
                        String name = post.getStatusName();
                        EmpStatusData sd = new EmpStatusData(name);
                        statusList.add(sd);
                        tvStatus.setAdapter(statusAdapter);
                    }
                } else {
                    MotionToast.Companion.createColorToast(requireActivity(), "Error",
                            response.body().getMessage(),
                            MotionToast.TOAST_ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(requireActivity(),R.font.helvetica_regular)
                    );
                }
            }

            @Override
            public void onFailure(Call<EmpStatus> call, Throwable t) {
                MotionToast.Companion.createColorToast(requireActivity(), "Cannot connect server",
                        t.getMessage(),
                        MotionToast.TOAST_NO_INTERNET,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(requireActivity(),R.font.helvetica_regular)
                );
            }
        });
        tvStatus.setFocusable(false);
        tvStatus.setFocusableInTouchMode(false);

        genderAdapter =  new ArrayAdapter<>(requireActivity(), R.layout.support_simple_spinner_dropdown_item, genderList);
        tvGender.setAdapter(genderAdapter);
        tvGender.setFocusable(false);
        tvGender.setFocusableInTouchMode(false);

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
                String formatted = dateFormat.format(calendar.getTime());
                etJoin.setText(formatted);
            });
        });
        etJoin.setFocusable(false);
        etJoin.setFocusableInTouchMode(false);

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
                String formatted = dateFormat.format(calendar.getTime());
                etLast.setText(formatted);
            });
        });
        etLast.setFocusable(false);
        etLast.setFocusableInTouchMode(false);
    }

    @Override
    public void onResume() {
        super.onResume();

        requireActivity().setTitle("Employee Details");
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_update, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.save:
                saveData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveData() {

    }
}