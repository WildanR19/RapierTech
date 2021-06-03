package com.example.rapiertech.ui.leave;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.rapiertech.R;
import com.example.rapiertech.activity.SessionManager;
import com.example.rapiertech.adapter.AdapterDataLeave;
import com.example.rapiertech.api.ApiClient;
import com.example.rapiertech.api.ApiInterface;
import com.example.rapiertech.model.leave.Leave;
import com.example.rapiertech.model.leave.LeaveData;
import com.example.rapiertech.widget.Widget;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeaveFragment extends Fragment {

    private RecyclerView rvData;
    private RecyclerView.Adapter adData;
    private List<LeaveData> leaveDataList = new ArrayList<>();
    private SwipeRefreshLayout srlData;
    private ProgressBar loadingData;
    private ApiInterface apiInterface;
    private SessionManager sessionManager;
    private Widget widget;

    public LeaveFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_leave, container, false);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        widget = new Widget();

        rvData = view.findViewById(R.id.rvDataLeave);
        RecyclerView.LayoutManager lmData = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        rvData.setLayoutManager(lmData);

        loadingData = view.findViewById(R.id.loadingDataLeave);
        srlData = view.findViewById(R.id.srlDataLeave);

        FloatingActionButton fab = view.findViewById(R.id.fab_leave);
        fab.setOnClickListener(v -> {
            LeaveEditorFragment leaveEditorFragment = new LeaveEditorFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, leaveEditorFragment)
                    .addToBackStack(null)
                    .commit();
        });

        sessionManager = new SessionManager(getContext());
        if (sessionManager.getRoleId().equals("1")){
            retrieveDataAdmin();
            srlData.setOnRefreshListener(() -> {
                srlData.setRefreshing(true);
                retrieveDataAdmin();
                srlData.setRefreshing(false);
            });
        } else {
            retrieveDataUser();
            srlData.setOnRefreshListener(() -> {
                srlData.setRefreshing(true);
                retrieveDataUser();
                srlData.setRefreshing(false);
            });
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (sessionManager.getRoleId().equals("1")){
            retrieveDataAdmin();
        } else {
            retrieveDataUser();
        }
        requireActivity().setTitle("Leaves");
    }

    public void retrieveDataUser() {
        loadingData.setVisibility(View.VISIBLE);

        Call<Leave> showLeaveDataByUserId = apiInterface.leaveRetrieveDataUser(Integer.parseInt(Objects.requireNonNull(sessionManager.getUserDetail().get(SessionManager.USER_ID))));
        showLeaveDataByUserId.enqueue(new Callback<Leave>() {
            @Override
            public void onResponse(@NotNull Call<Leave> call, @NotNull Response<Leave> response) {
                if (response.body() != null) {
                    if (response.isSuccessful() && response.body().isStatus()) {
                        leaveDataList = response.body().getData();
                        adData = new AdapterDataLeave(requireActivity(), leaveDataList, LeaveFragment.this);
                        rvData.setAdapter(adData);
                        adData.notifyDataSetChanged();
                    } else {
                        widget.errorToast(response.body().getMessage(), requireActivity());
                    }
                }
                loadingData.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(@NotNull Call<Leave> call, @NotNull Throwable t) {
                widget.noConnectToast(t.getMessage(), requireActivity());
                loadingData.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void retrieveDataAdmin() {
        loadingData.setVisibility(View.VISIBLE);

        Call<Leave> showLeaveData = apiInterface.leaveRetrieveData();
        showLeaveData.enqueue(new Callback<Leave>() {
            @Override
            public void onResponse(@NotNull Call<Leave> call, @NotNull Response<Leave> response) {
                if (response.body() != null){
                    if (response.isSuccessful() && response.body().isStatus()){
                        leaveDataList = response.body().getData();
                        adData = new AdapterDataLeave(requireActivity(), leaveDataList, LeaveFragment.this);
                        rvData.setAdapter(adData);
                        adData.notifyDataSetChanged();
                    } else {
                        widget.errorToast(response.body().getMessage(), requireActivity());
                    }
                }
                loadingData.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(@NotNull Call<Leave> call, @NotNull Throwable t) {
                widget.noConnectToast(t.getMessage(), requireActivity());
                loadingData.setVisibility(View.INVISIBLE);
            }
        });
    }
}