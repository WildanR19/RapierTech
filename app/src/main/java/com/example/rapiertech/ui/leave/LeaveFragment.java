package com.example.rapiertech.ui.leave;

import android.app.Activity;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.rapiertech.R;
import com.example.rapiertech.adapter.AdapterDataJob;
import com.example.rapiertech.adapter.AdapterDataLeave;
import com.example.rapiertech.api.ApiClient;
import com.example.rapiertech.api.ApiInterface;
import com.example.rapiertech.model.leave.Leave;
import com.example.rapiertech.model.leave.LeaveData;
import com.example.rapiertech.ui.admin.JobFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import www.sanju.motiontoast.MotionToast;

public class LeaveFragment extends Fragment {

    private RecyclerView rvData;
    private RecyclerView.Adapter adData;
    private List<LeaveData> leaveDataList = new ArrayList<>();
    private SwipeRefreshLayout srlData;
    private ProgressBar loadingData;
    private ApiInterface apiInterface;

    public LeaveFragment() {
        // Required empty public constructor
    }

//    public static LeaveFragment newInstance(String param1, String param2) {
//        LeaveFragment fragment = new LeaveFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_leave, container, false);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        rvData = view.findViewById(R.id.rvDataLeave);
        RecyclerView.LayoutManager lmData = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        rvData.setLayoutManager(lmData);

        loadingData = view.findViewById(R.id.loadingDataLeave);

        srlData = view.findViewById(R.id.srlDataLeave);
        srlData.setOnRefreshListener(() -> {
            srlData.setRefreshing(true);
            retrieveData();
            srlData.setRefreshing(false);
        });

        FloatingActionButton fab = view.findViewById(R.id.fab_leave);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LeaveEditorFragment leaveEditorFragment = new LeaveEditorFragment();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, leaveEditorFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        retrieveData();
        requireActivity().setTitle("Leaves");
    }

    private void retrieveData() {
        loadingData.setVisibility(View.VISIBLE);

        Call<Leave> showLeaveData = apiInterface.leaveRetrieveData();
        showLeaveData.enqueue(new Callback<Leave>() {
            @Override
            public void onResponse(Call<Leave> call, Response<Leave> response) {
                if (response.body() != null && response.isSuccessful() && response.body().isStatus()){
                    leaveDataList = response.body().getData();
                    adData = new AdapterDataLeave(requireActivity(), leaveDataList, LeaveFragment.this);
                    rvData.setAdapter(adData);
                    adData.notifyDataSetChanged();
                } else {
                    assert response.body() != null;
                    errorToast(response.body().getMessage());
                }
                loadingData.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<Leave> call, Throwable t) {
                noConnectToast(t.getMessage());
                loadingData.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void noConnectToast(String message) {
        MotionToast.Companion.createColorToast(requireActivity(), "Cannot connect server",
                message,
                MotionToast.TOAST_ERROR,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(requireActivity(),R.font.helvetica_regular)
        );
        loadingData.setVisibility(View.INVISIBLE);
    }

    private void errorToast(String message) {
        MotionToast.Companion.createColorToast((Activity) requireActivity(), "Error",
                message,
                MotionToast.TOAST_ERROR,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(requireActivity(),R.font.helvetica_regular)
        );
        loadingData.setVisibility(View.INVISIBLE);
    }

    private void successToast(String message) {
        MotionToast.Companion.createColorToast((Activity) requireActivity(), "Success",
                message,
                MotionToast.TOAST_SUCCESS,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(requireActivity(),R.font.helvetica_regular)
        );
        loadingData.setVisibility(View.INVISIBLE);
    }
}