package com.example.rapiertech.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.rapiertech.R;
import com.example.rapiertech.activity.SessionManager;
import com.example.rapiertech.adapter.home.AdapterOngoingProject;
import com.example.rapiertech.adapter.home.AdapterPendingLeave;
import com.example.rapiertech.api.ApiClient;
import com.example.rapiertech.api.ApiInterface;
import com.example.rapiertech.model.home.Home;
import com.example.rapiertech.model.leave.Leave;
import com.example.rapiertech.model.leave.LeaveData;
import com.example.rapiertech.model.project.Project;
import com.example.rapiertech.model.project.ProjectData;
import com.example.rapiertech.ui.admin.DepartmentFragment;
import com.example.rapiertech.ui.admin.JobFragment;
import com.example.rapiertech.ui.employee.EmployeeFragment;
import com.example.rapiertech.ui.leave.LeaveFragment;
import com.example.rapiertech.ui.project.ProjectFragment;
import com.example.rapiertech.widget.Widget;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private View view;
    private RecyclerView rvProject, rvLeave;
    private RecyclerView.Adapter adapterProject, adapterLeave;
    private SwipeRefreshLayout srlData;
    private ProgressBar loading;
    private ApiInterface apiInterface;
    private SessionManager sessionManager;
    private Widget widget;
    private List<ProjectData> projectDataList = new ArrayList<>();
    private List<LeaveData> leaveDataList = new ArrayList<>();
    private TextView tvTotalEmp, tvTotalDept, tvTotalJob, tvTotalProject, tvMoreEmp, tvMoreDept, tvMoreJob, tvMoreProject, tvViewAllProject, tvViewAllLeave;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        widget = new Widget();
        sessionManager = new SessionManager(getContext());

        findView();
        moveFunction();

        srlData.setOnRefreshListener(() -> {
            srlData.setRefreshing(true);
            retrieveData();
            srlData.setRefreshing(false);
        });

        return view;
    }

    private void moveFunction() {
        tvMoreEmp.setOnClickListener(v -> {
            Fragment fragment = new EmployeeFragment();
            moveFragment(fragment);
        });

        tvMoreDept.setOnClickListener(v -> {
            Fragment fragment = new DepartmentFragment();
            moveFragment(fragment);
        });

        tvMoreJob.setOnClickListener(v -> {
            Fragment fragment = new JobFragment();
            moveFragment(fragment);
        });

        tvMoreProject.setOnClickListener(v -> {
            Fragment fragment = new ProjectFragment();
            moveFragment(fragment);
        });

        tvViewAllProject.setOnClickListener(v -> {
            Fragment fragment = new ProjectFragment();
            moveFragment(fragment);
        });

        tvViewAllLeave.setOnClickListener(v -> {
            Fragment fragment = new LeaveFragment();
            moveFragment(fragment);
        });
    }

    private void moveFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit();
    }

    public void retrieveData() {
        loading.setVisibility(View.VISIBLE);

        Call<Home> getHomeData = apiInterface.homeRetrieveData();
        getHomeData.enqueue(new Callback<Home>() {
            @Override
            public void onResponse(@NotNull Call<Home> call, @NotNull Response<Home> response) {
                if (response.body() != null) {
                    if (response.isSuccessful() && response.body().isStatus()) {
                        tvTotalEmp.setText(String.valueOf(response.body().getData().getTotalEmployee()));
                        tvTotalDept.setText(String.valueOf(response.body().getData().getTotalDepartment()));
                        tvTotalJob.setText(String.valueOf(response.body().getData().getTotalJob()));
                        tvTotalProject.setText(String.valueOf(response.body().getData().getTotalProject()));
                    } else {
                        widget.errorToast(response.body().getMessage(), requireActivity());
                    }
                    loading.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<Home> call, @NotNull Throwable t) {
                widget.noConnectToast(t.getMessage(), requireActivity());
                loading.setVisibility(View.INVISIBLE);
            }
        });

        if (sessionManager.getRoleId().equals("1")) {
            Call<Project> getOngoingProjectData = apiInterface.projectOngoingRetrieveData();
            getOngoingProjectData.enqueue(new Callback<Project>() {
                @Override
                public void onResponse(@NotNull Call<Project> call, @NotNull Response<Project> response) {
                    if (response.body() != null) {
                        if (response.isSuccessful() && response.body().isStatus()) {
                            projectDataList = response.body().getData();
                            RecyclerView.LayoutManager lmData = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
                            rvProject.setLayoutManager(lmData);
                            adapterProject = new AdapterOngoingProject(requireActivity(), projectDataList, HomeFragment.this);
                            rvProject.setAdapter(adapterProject);
                            adapterProject.notifyDataSetChanged();
                        } else {
                            widget.errorToast(response.body().getMessage(), requireActivity());
                        }
                        loading.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onFailure(@NotNull Call<Project> call, @NotNull Throwable t) {
                    widget.noConnectToast(t.getMessage(), requireActivity());
                    loading.setVisibility(View.INVISIBLE);
                }
            });

            Call<Leave> getPendingLeaveData = apiInterface.leavePendingRetrieveData();
            getPendingLeaveData.enqueue(new Callback<Leave>() {
                @Override
                public void onResponse(@NotNull Call<Leave> call, @NotNull Response<Leave> response) {
                    if (response.body() != null) {
                        if (response.isSuccessful() && response.body().isStatus()) {
                            leaveDataList = response.body().getData();
                            RecyclerView.LayoutManager lmData = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
                            rvLeave.setLayoutManager(lmData);
                            adapterLeave = new AdapterPendingLeave(requireActivity(), leaveDataList, HomeFragment.this);
                            rvLeave.setAdapter(adapterLeave);
                            adapterLeave.notifyDataSetChanged();
                        } else {
                            widget.errorToast(response.body().getMessage(), requireActivity());
                        }
                        loading.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onFailure(@NotNull Call<Leave> call, @NotNull Throwable t) {
                    widget.noConnectToast(t.getMessage(), requireActivity());
                    loading.setVisibility(View.INVISIBLE);
                }
            });
        } else {
            int userId = Integer.parseInt(sessionManager.getUserDetail().get(SessionManager.USER_ID));

            Call<Project> getOngoingProjectUser = apiInterface.projectOngoingUserRetrieveData(userId);
            getOngoingProjectUser.enqueue(new Callback<Project>() {
                @Override
                public void onResponse(@NotNull Call<Project> call, @NotNull Response<Project> response) {
                    if (response.body() != null) {
                        if (response.isSuccessful() && response.body().isStatus()) {
                            projectDataList = response.body().getData();
                            RecyclerView.LayoutManager lmData = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
                            rvProject.setLayoutManager(lmData);
                            adapterProject = new AdapterOngoingProject(requireActivity(), projectDataList, HomeFragment.this);
                            rvProject.setAdapter(adapterProject);
                            adapterProject.notifyDataSetChanged();
                        } else {
                            widget.errorToast(response.body().getMessage(), requireActivity());
                        }
                        loading.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onFailure(@NotNull Call<Project> call, @NotNull Throwable t) {
                    widget.noConnectToast(t.getMessage(), requireActivity());
                    loading.setVisibility(View.INVISIBLE);
                }
            });

            Call<Leave> getPendingLeaveUser = apiInterface.leavePendingRetrieveDataUser(userId);
            getPendingLeaveUser.enqueue(new Callback<Leave>() {
                @Override
                public void onResponse(@NotNull Call<Leave> call, @NotNull Response<Leave> response) {
                    if (response.body() != null) {
                        if (response.isSuccessful() && response.body().isStatus()) {
                            leaveDataList = response.body().getData();
                            RecyclerView.LayoutManager lmData = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
                            rvLeave.setLayoutManager(lmData);
                            adapterLeave = new AdapterPendingLeave(requireActivity(), leaveDataList, HomeFragment.this);
                            rvLeave.setAdapter(adapterLeave);
                            adapterLeave.notifyDataSetChanged();
                        } else {
                            widget.errorToast(response.body().getMessage(), requireActivity());
                        }
                        loading.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onFailure(@NotNull Call<Leave> call, @NotNull Throwable t) {
                    widget.noConnectToast(t.getMessage(), requireActivity());
                    loading.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    private void findView() {
        tvTotalEmp = view.findViewById(R.id.totalEmpHome);
        tvTotalDept = view.findViewById(R.id.totalDeptHome);
        tvTotalJob = view.findViewById(R.id.totalJobHome);
        tvTotalProject = view.findViewById(R.id.totalProjectHome);
        tvMoreEmp = view.findViewById(R.id.moreEmpHome);
        tvMoreDept = view.findViewById(R.id.moreDeptHome);
        tvMoreJob = view.findViewById(R.id.moreJobHome);
        tvMoreProject = view.findViewById(R.id.moreProjectHome);
        tvViewAllProject = view.findViewById(R.id.viewAllProjectHome);
        tvViewAllLeave = view.findViewById(R.id.viewAllLeaveHome);
        srlData = view.findViewById(R.id.srlDataHome);
        loading = view.findViewById(R.id.loadingHome);
        rvProject = view.findViewById(R.id.rvOngoingProjectHome);
        rvLeave = view.findViewById(R.id.rvPendingLeaveHome);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //Set title of this fragment
        requireActivity().setTitle("Home");
        retrieveData();
    }
}