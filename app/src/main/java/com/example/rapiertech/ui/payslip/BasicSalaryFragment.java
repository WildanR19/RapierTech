package com.example.rapiertech.ui.payslip;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.rapiertech.R;
import com.example.rapiertech.adapter.AdapterBasicSalary;
import com.example.rapiertech.api.ApiClient;
import com.example.rapiertech.api.ApiInterface;
import com.example.rapiertech.model.basicpays.BasicPays;
import com.example.rapiertech.model.basicpays.BasicPaysData;
import com.example.rapiertech.model.basicpays.BasicPaysJobData;
import com.example.rapiertech.widget.Widget;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BasicSalaryFragment extends Fragment {

    private List<BasicPaysData> basicPaysList = new ArrayList<>();
    private ListView listView;
    private ApiInterface apiInterface;
    private AdapterBasicSalary adapter;
    private Widget widget;
    private ProgressBar loading;
    private FloatingActionButton fabAdd;
    private int salary, jobId;
    private List<BasicPaysJobData> jobDataList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_basic_salary, container, false);
        widget = new Widget();
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        listView = view.findViewById(R.id.listviewBasicSalary);
        loading = view.findViewById(R.id.loadingBasicSalary);
        fabAdd = view.findViewById(R.id.fab_addBasicSalary);

        SwipeRefreshLayout srlData = view.findViewById(R.id.srlBasicSalary);
        srlData.setOnRefreshListener(() -> {
            srlData.setRefreshing(true);
            retrieveData();
            srlData.setRefreshing(false);
        });

        retrieveData();
        fabAdd.setOnClickListener(v -> addDialog());

        return view;
    }

    private void addDialog() {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_basic_salary, null);
        EditText etSalary = view.findViewById(R.id.etSalaryBasicAdd);
        AutoCompleteTextView tvJob = view.findViewById(R.id.tvJobBasicAdd);

        actvJob(tvJob);
        widget.setActvFocusableFalse(tvJob);

        MaterialAlertDialogBuilder mDialog = new MaterialAlertDialogBuilder(requireActivity());
        mDialog.setView(view)
                .setTitle(R.string.add_title_dialog)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    salary = Integer.parseInt(etSalary.getText().toString());
                    createData();
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void actvJob(AutoCompleteTextView tvJob) {
        Call<BasicPays> retrieveData = apiInterface.basicPaysRetrieveData();
        retrieveData.enqueue(new Callback<BasicPays>() {
            @Override
            public void onResponse(Call<BasicPays> call, Response<BasicPays> response) {
                if (response.body() != null) {
                    if (response.isSuccessful() && response.body().isStatus()) {
                        jobDataList = response.body().getJobData();
                        for (int i = 0; i < jobDataList.size(); i++){
                            if (jobId == jobDataList.get(i).getId()){
                                tvJob.setText(jobDataList.get(i).getName());
                            }
                        }
                        ArrayAdapter<BasicPaysJobData> jobAdapter = new ArrayAdapter<>(requireActivity(), R.layout.support_simple_spinner_dropdown_item, jobDataList);
                        tvJob.setAdapter(jobAdapter);
                        tvJob.setOnItemClickListener((parent, view, position, id) -> {
                            jobId = jobDataList.get(position).getId();
                        });
                    } else {
                        widget.errorToast(response.body().getMessage(), requireActivity());
                    }
                }
            }

            @Override
            public void onFailure(Call<BasicPays> call, Throwable t) {
                widget.noConnectToast(t.getMessage(), requireActivity());
            }
        });
    }

    private void createData() {
        Call<BasicPays> createData = apiInterface.basicPaysCreateData(jobId, salary);
        createData.enqueue(new Callback<BasicPays>() {
            @Override
            public void onResponse(Call<BasicPays> call, Response<BasicPays> response) {
                if (response.body() != null){
                    if (response.isSuccessful() && response.body().isStatus()){
                        widget.successToast(response.body().getMessage(), requireActivity());
                        retrieveData();
                    } else {
                        widget.errorToast(response.body().getMessage(), requireActivity());
                    }
                }
            }

            @Override
            public void onFailure(Call<BasicPays> call, Throwable t) {
                widget.noConnectToast(t.getMessage(), requireActivity());
            }
        });
    }

    public void retrieveData() {
        loading.setVisibility(View.VISIBLE);

        Call<BasicPays> retrieveData = apiInterface.basicPaysRetrieveData();
        retrieveData.enqueue(new Callback<BasicPays>() {
            @Override
            public void onResponse(Call<BasicPays> call, Response<BasicPays> response) {
                if (response.body() != null) {
                    if (response.isSuccessful() && response.body().isStatus()) {
                        basicPaysList = response.body().getData();
                        adapter = new AdapterBasicSalary(requireActivity(), R.layout.list_basic_salaries, basicPaysList, BasicSalaryFragment.this);
                        listView.setAdapter(adapter);
                    } else {
                        widget.errorToast(response.body().getMessage(), requireActivity());
                    }
                }
                loading.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<BasicPays> call, Throwable t) {
                widget.noConnectToast(t.getMessage(), requireActivity());
                loading.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        retrieveData();
        requireActivity().setTitle("Basic Salaries");
    }
}