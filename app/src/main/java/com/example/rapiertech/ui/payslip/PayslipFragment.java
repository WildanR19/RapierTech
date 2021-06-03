package com.example.rapiertech.ui.payslip;

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
import com.example.rapiertech.adapter.AdapterDataPayslip;
import com.example.rapiertech.api.ApiClient;
import com.example.rapiertech.api.ApiInterface;
import com.example.rapiertech.model.payslip.Payslip;
import com.example.rapiertech.model.payslip.PayslipData;
import com.example.rapiertech.widget.Widget;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayslipFragment extends Fragment {

    private View view;
    private RecyclerView rvData;
    private RecyclerView.Adapter adData;
    private List<PayslipData> payslipDataList = new ArrayList<>();
    private SwipeRefreshLayout srlData;
    private ProgressBar loadingData;
    private ApiInterface apiInterface;
    private SessionManager sessionManager;
    private Widget widget;

    public PayslipFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_payslip, container, false);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        widget = new Widget();

        rvData = view.findViewById(R.id.rvDataPayslip);
        RecyclerView.LayoutManager lmData = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        rvData.setLayoutManager(lmData);

        loadingData = view.findViewById(R.id.loadingDataPayslip);
        srlData = view.findViewById(R.id.srlDataPayslip);

        FloatingActionButton fab = view.findViewById(R.id.fab_payslip);
        fab.setOnClickListener(v -> {

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
            fab.setVisibility(View.GONE);
        }

        return view;
    }

    private void retrieveDataAdmin() {
        loadingData.setVisibility(View.VISIBLE);

        Call<Payslip> retrieveData = apiInterface.payslipRetrieveData();
        retrieveData.enqueue(new Callback<Payslip>() {
            @Override
            public void onResponse(Call<Payslip> call, Response<Payslip> response) {
                if (response.body() != null){
                    if (response.isSuccessful() && response.body().isStatus()){
                        payslipDataList = response.body().getData();
                        adData = new AdapterDataPayslip(requireActivity(), payslipDataList, PayslipFragment.this);
                        rvData.setAdapter(adData);
                        adData.notifyDataSetChanged();
                    } else {
                        widget.errorToast(response.body().getMessage(), requireActivity());
                    }
                }
                loadingData.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<Payslip> call, Throwable t) {
                widget.noConnectToast(t.getMessage(), requireActivity());
                loadingData.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (sessionManager.getRoleId().equals("1")){
            retrieveDataAdmin();
        } else {
            retrieveDataUser();
        }
        requireActivity().setTitle("Payslip");
    }

    private void retrieveDataUser() {
    }
}