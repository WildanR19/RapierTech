package com.example.rapiertech.ui.admin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.rapiertech.R;
import com.example.rapiertech.adapter.AdapterDataJob;
import com.example.rapiertech.api.ApiClient;
import com.example.rapiertech.api.ApiInterface;
import com.example.rapiertech.model.job.Job;
import com.example.rapiertech.model.job.JobData;
import com.example.rapiertech.widget.Widget;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobFragment extends Fragment {

    private RecyclerView rvData;
    private RecyclerView.Adapter adData;
    private List<JobData> listData = new ArrayList<>();
    private SwipeRefreshLayout srlData;
    private ProgressBar loadingData;
    private EditText etName;
    private String name;
    private Widget widget;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_job, container, false);
        widget = new Widget();

        rvData = root.findViewById(R.id.rvDataJob);
        RecyclerView.LayoutManager lmData = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        rvData.setLayoutManager(lmData);

        srlData = root.findViewById(R.id.srlDataJob);
        srlData.setOnRefreshListener(() -> {
            srlData.setRefreshing(true);
            retrieveData();
            srlData.setRefreshing(false);
        });

        loadingData = root.findViewById(R.id.loadingDataJob);

        FloatingActionButton fab = root.findViewById(R.id.fab_job);
        fab.setOnClickListener(v -> showDialog());

        return root;
    }

    public void showDialog(){

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_deptjob, null);
        etName = view.findViewById(R.id.add_deptJobName);

        MaterialAlertDialogBuilder mDialog = new MaterialAlertDialogBuilder(requireActivity());
        mDialog.setView(view)
                .setTitle(R.string.add_title_dialog)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    name = etName.getText().toString();
                    createData();
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void createData() {

        loadingData.setVisibility(View.VISIBLE);

        ApiInterface apiData = ApiClient.getClient().create(ApiInterface.class);
        Call<Job> createdata = apiData.jobCreateData(name);

        createdata.enqueue(new Callback<Job>() {
            @Override
            public void onResponse(@NotNull Call<Job> call, @NotNull Response<Job> response) {
                if (response.body() != null){
                    if (response.isSuccessful() && response.body().isStatus()){
                        widget.successToast(response.body().getMessage(), requireActivity());
                        retrieveData();
                    } else {
                        widget.errorToast(response.body().getMessage(), requireActivity());
                    }
                }
                loadingData.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<Job> call, Throwable t) {
                widget.noConnectToast(t.getMessage(), requireActivity());
                loadingData.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void retrieveData() {

        loadingData.setVisibility(View.VISIBLE);

        ApiInterface apiData = ApiClient.getClient().create(ApiInterface.class);
        Call<Job> showdata = apiData.jobRetrieveData();

        showdata.enqueue(new Callback<Job>() {
            @Override
            public void onResponse(Call<Job> call, Response<Job> response) {
                if (response.body() != null){
                    if (response.isSuccessful() && response.body().isStatus()){
                        listData = response.body().getData();
                        adData = new AdapterDataJob(requireActivity(), listData, JobFragment.this);
                        rvData.setAdapter(adData);
                        adData.notifyDataSetChanged();
                        loadingData.setVisibility(View.INVISIBLE);
                    }else{
                        widget.errorToast(response.body().getMessage(), requireActivity());
                    }
                }
            }

            @Override
            public void onFailure(Call<Job> call, Throwable t) {
                widget.noConnectToast(t.getMessage(), requireActivity());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        retrieveData();
        //Set title of this fragment
        requireActivity().setTitle("Job");
    }
}