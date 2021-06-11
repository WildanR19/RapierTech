package com.example.rapiertech.ui.project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.rapiertech.R;
import com.example.rapiertech.activity.SessionManager;
import com.example.rapiertech.adapter.AdapterProject;
import com.example.rapiertech.api.ApiClient;
import com.example.rapiertech.api.ApiInterface;
import com.example.rapiertech.model.project.Project;
import com.example.rapiertech.model.project.ProjectData;
import com.example.rapiertech.widget.Widget;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProjectFragment extends Fragment {

    private View view;
    private RecyclerView rvData;
    private RecyclerView.Adapter adData;
    private SwipeRefreshLayout srlData;
    private ProgressBar loading;
    private ApiInterface apiInterface;
    private SessionManager sessionManager;
    private Widget widget;
    private List<ProjectData> projectDataList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_project, container, false);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        widget = new Widget();
        sessionManager = new SessionManager(getContext());

        rvData = view.findViewById(R.id.rvDataProject);
        RecyclerView.LayoutManager lmData = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        rvData.setLayoutManager(lmData);

        loading = view.findViewById(R.id.loadingProject);
        srlData = view.findViewById(R.id.srlDataProject);

        FloatingActionButton fab = view.findViewById(R.id.fabProject);
        if (!sessionManager.getRoleId().equals("1")){
            fab.setVisibility(View.GONE);
        }
        fab.setOnClickListener(v -> {
        ProjectEditorFragment projectEditorFragment = new ProjectEditorFragment();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, projectEditorFragment)
                .addToBackStack(null)
                .commit();
        });

        srlData.setOnRefreshListener(() -> {
            srlData.setRefreshing(true);
            retrieveData();
            srlData.setRefreshing(false);
        });
        return view;
    }

    public void retrieveData() {
        loading.setVisibility(View.VISIBLE);

        if (sessionManager.getRoleId().equals("1")) {
            Call<Project> retrieveData = apiInterface.projectRetrieveData();
            retrieveData.enqueue(new Callback<Project>() {
                @Override
                public void onResponse(@NotNull Call<Project> call, @NotNull Response<Project> response) {
                    if (response.body() != null) {
                        if (response.isSuccessful() && response.body().isStatus()) {
                            projectDataList = response.body().getData();
                            adData = new AdapterProject(requireActivity(), projectDataList, ProjectFragment.this);
                            rvData.setAdapter(adData);
                            adData.notifyDataSetChanged();
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
        } else {
            Call<Project> retrieveData = apiInterface.projectUserRetrieveData(Integer.parseInt(Objects.requireNonNull(sessionManager.getUserDetail().get(SessionManager.USER_ID))));
            retrieveData.enqueue(new Callback<Project>() {
                @Override
                public void onResponse(@NotNull Call<Project> call, @NotNull Response<Project> response) {
                    if (response.body() != null) {
                        if (response.isSuccessful() && response.body().isStatus()) {
                            projectDataList = response.body().getData();
                            adData = new AdapterProject(requireActivity(), projectDataList, ProjectFragment.this);
                            rvData.setAdapter(adData);
                            adData.notifyDataSetChanged();
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
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        retrieveData();
        requireActivity().setTitle("Project");
    }
}