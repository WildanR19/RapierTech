package com.example.rapiertech.ui.goal;

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
import com.example.rapiertech.adapter.AdapterGoal;
import com.example.rapiertech.api.ApiClient;
import com.example.rapiertech.api.ApiInterface;
import com.example.rapiertech.model.goal.Goal;
import com.example.rapiertech.model.goal.GoalData;
import com.example.rapiertech.widget.Widget;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoalFragment extends Fragment {

    private View view;
    private RecyclerView rvData;
    private RecyclerView.Adapter adData;
    private SwipeRefreshLayout srlData;
    private ProgressBar loading;
    private ApiInterface apiInterface;
    private SessionManager sessionManager;
    private Widget widget;
    private List<GoalData> goalDataList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_goal, container, false);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        widget = new Widget();
        sessionManager = new SessionManager(getContext());

        rvData = view.findViewById(R.id.rvDataGoal);
        RecyclerView.LayoutManager lmData = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        rvData.setLayoutManager(lmData);

        loading = view.findViewById(R.id.loadingGoal);
        srlData = view.findViewById(R.id.srlDataGoal);

        FloatingActionButton fab = view.findViewById(R.id.fab_Goal);

        srlData.setOnRefreshListener(() -> {
            srlData.setRefreshing(true);
            retrieveData();
            srlData.setRefreshing(false);
        });

        return view;
    }

    private void retrieveData() {
        loading.setVisibility(View.VISIBLE);

        if (sessionManager.getRoleId().equals("1")) {
            Call<Goal> goalRetrieveData = apiInterface.goalRetrieveData();
            goalRetrieveData.enqueue(new Callback<Goal>() {
                @Override
                public void onResponse(Call<Goal> call, Response<Goal> response) {
                    if (response.body() != null) {
                        if (response.isSuccessful() && response.body().isStatus()) {
                            goalDataList = response.body().getData();
                            adData = new AdapterGoal(requireActivity(), goalDataList, GoalFragment.this);
                            rvData.setAdapter(adData);
                            adData.notifyDataSetChanged();
                        } else {
                            widget.errorToast(response.body().getMessage(), requireActivity());
                        }
                        loading.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<Goal> call, Throwable t) {
                    widget.noConnectToast(t.getMessage(), requireActivity());
                    loading.setVisibility(View.INVISIBLE);
                }
            });
        } else {
            Call<Goal> goalRetrieveDataUser = apiInterface.goalRetrieveDataUser(Integer.parseInt(sessionManager.getUserDetail().get(SessionManager.USER_ID)));
            goalRetrieveDataUser.enqueue(new Callback<Goal>() {
                @Override
                public void onResponse(Call<Goal> call, Response<Goal> response) {
                    if (response.body() != null) {
                        if (response.isSuccessful() && response.body().isStatus()) {
                            goalDataList = response.body().getData();
                            adData = new AdapterGoal(requireActivity(), goalDataList, GoalFragment.this);
                            rvData.setAdapter(adData);
                            adData.notifyDataSetChanged();
                        } else {
                            widget.errorToast(response.body().getMessage(), requireActivity());
                        }
                        loading.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<Goal> call, Throwable t) {
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
        requireActivity().setTitle("Goals");
    }
}