package com.example.rapiertech.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.rapiertech.R;
import com.example.rapiertech.activity.LoginActivity;
import com.example.rapiertech.activity.SessionManager;
import com.example.rapiertech.api.ApiClient;
import com.example.rapiertech.api.ApiInterface;
import com.example.rapiertech.model.empdetail.EmpDetail;
import com.example.rapiertech.model.employee.Employee;
import com.example.rapiertech.ui.employee.EmployeeDetailsFragment;
import com.example.rapiertech.widget.Widget;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private View view;
    private ApiInterface apiInterface;
    private SessionManager sessionManager;
    private Widget widget;
    private TextView tvName, tvEmail, tvLocation, tvPhone, tvGender;
    private ImageView imgEmp;
    private LinearLayout llEdit, llChangePw, llNotif, llSetting, llLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        widget = new Widget();
        sessionManager = new SessionManager(getContext());

        findView();
        initUi();

        return view;
    }

    private void initUi() {
        tvName.setText(widget.capitalizeText(sessionManager.getUserDetail().get(SessionManager.NAME)));
        tvEmail.setText(sessionManager.getUserDetail().get(SessionManager.EMAIL));

        String urlImage = ApiClient.getStorage() + sessionManager.getUserDetail().get(SessionManager.PHOTO_PATH);
        Glide.with(requireActivity()).load(urlImage).diskCacheStrategy(DiskCacheStrategy.NONE).into(imgEmp);
        imgEmp.setClipToOutline(true);

        Call<EmpDetail> employeeCall = apiInterface.employeeDetailData(Integer.parseInt(sessionManager.getUserDetail().get(SessionManager.USER_ID)));
        employeeCall.enqueue(new Callback<EmpDetail>() {
            @Override
            public void onResponse(Call<EmpDetail> call, Response<EmpDetail> response) {
                if (response.body() != null) {
                    if (response.isSuccessful() && response.body().isStatus()) {
                        tvLocation.setText(response.body().getData().getAddress());
                        tvPhone.setText(response.body().getData().getPhone());
                        tvGender.setText(response.body().getData().getGender());

                        llEdit.setOnClickListener(v -> {
                            Bundle bundle = new Bundle();
                            bundle.putInt("id", Integer.parseInt(sessionManager.getUserDetail().get(SessionManager.USER_ID)));
                            bundle.putString("name", sessionManager.getUserDetail().get(SessionManager.NAME));
                            bundle.putString("email", sessionManager.getUserDetail().get(SessionManager.EMAIL));
                            bundle.putInt("roleId", Integer.parseInt(sessionManager.getRoleId()));
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
                        });
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

        llChangePw.setOnClickListener(v -> {
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View views = inflater.inflate(R.layout.dialog_change_password, null);
            EditText etCurrentPassword = views.findViewById(R.id.currentPassword);
            EditText etPassword = views.findViewById(R.id.newPassword);
            EditText etConfirmPassword = views.findViewById(R.id.confirmPassword);

            MaterialAlertDialogBuilder mDialog = new MaterialAlertDialogBuilder(requireActivity());
            mDialog.setView(views)
                    .setTitle(R.string.change_password)
                    .setPositiveButton(R.string.save, (dialog, which) -> {
                        int userId = Integer.parseInt(sessionManager.getUserDetail().get(SessionManager.USER_ID));
                        Call<Employee> updatePassword = apiInterface.employeeChangePassword(userId,
                                etCurrentPassword.getText().toString(),
                                etPassword.getText().toString(),
                                etConfirmPassword.getText().toString()
                        );
                        updatePassword.enqueue(new Callback<Employee>() {
                            @Override
                            public void onResponse(@NotNull Call<Employee> call, @NotNull Response<Employee> response) {
                                if (response.body() != null) {
                                    if (response.isSuccessful() && response.body().isStatus()) {
                                        widget.successToast(response.body().getMessage(), requireActivity());
                                        dialog.dismiss();
                                    } else {
                                        widget.errorToast(response.body().getMessage(), requireActivity());
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NotNull Call<Employee> call, @NotNull Throwable t) {
                                widget.noConnectToast(t.getMessage(), requireActivity());
                            }
                        });
                    })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                    .show();
        });

        llLogout.setOnClickListener(v -> logout());

    }

    private void logout() {
        sessionManager.logoutSession();
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        widget.successToast("Logout Success", requireActivity());
    }

    private void findView() {
        tvName = view.findViewById(R.id.nameEmpProfile);
        tvEmail = view.findViewById(R.id.emailEmpProfile);
        imgEmp = view.findViewById(R.id.imgEmpProfile);
        tvLocation = view.findViewById(R.id.locationProfile);
        tvPhone = view.findViewById(R.id.phoneProfile);
        tvGender = view.findViewById(R.id.genderProfile);
        llEdit = view.findViewById(R.id.ll_editProfile);
        llChangePw = view.findViewById(R.id.ll_changePwProfile);
        llSetting = view.findViewById(R.id.ll_settingProfile);
        llLogout = view.findViewById(R.id.ll_logOutProfile);
    }

    @Override
    public void onResume() {
        super.onResume();

        requireActivity().setTitle("Profile");
    }
}