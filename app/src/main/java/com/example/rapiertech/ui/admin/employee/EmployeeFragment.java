package com.example.rapiertech.ui.admin.employee;

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
import com.example.rapiertech.adapter.AdapterDataEmployee;
import com.example.rapiertech.api.ApiClient;
import com.example.rapiertech.api.ApiInterface;
import com.example.rapiertech.model.employee.Employee;
import com.example.rapiertech.model.employee.EmployeeData;
import com.example.rapiertech.widget.Widget;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployeeFragment extends Fragment {

    private RecyclerView rvData;
    private RecyclerView.Adapter adData;
    private List<EmployeeData> dataList = new ArrayList<>();
    private SwipeRefreshLayout srlData;
    private ProgressBar loadingData;
    private Widget widget;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_employee, container, false);
        widget = new Widget();
        rvData = view.findViewById(R.id.rvDataEmp);

        RecyclerView.LayoutManager lmData = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        rvData.setLayoutManager(lmData);

        srlData = view.findViewById(R.id.srlDataEmp);
        srlData.setOnRefreshListener(() -> {
            srlData.setRefreshing(true);
            retrieveData();
            srlData.setRefreshing(false);
        });

        loadingData = view.findViewById(R.id.loadingDataEmp);

        FloatingActionButton fab = view.findViewById(R.id.fab_emp);
        fab.setOnClickListener(v -> openForm());

        return view;
    }

    private void openForm() {
        EmployeeDetailsFragment fragment = new EmployeeDetailsFragment();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void retrieveData() {
        loadingData.setVisibility(View.VISIBLE);

        ApiInterface apiData = ApiClient.getClient().create(ApiInterface.class);
        Call<Employee> showdata = apiData.employeeRetrieveData();

        showdata.enqueue(new Callback<Employee>() {
            @Override
            public void onResponse(@NotNull Call<Employee> call, @NotNull Response<Employee> response) {
                if (response.body() != null){
                    if (response.isSuccessful() && response.body().isStatus()){
                        dataList = response.body().getData();
                        adData = new AdapterDataEmployee(requireActivity(), dataList);
                        rvData.setAdapter(adData);
                        adData.notifyDataSetChanged();
                    }else{
                        widget.errorToast(response.body().getMessage(), requireActivity());
                    }
                }
                loadingData.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(@NotNull Call<Employee> call, @NotNull Throwable t) {
               widget.noConnectToast(t.getMessage(), requireActivity());
                loadingData.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        retrieveData();
        //Set title of this fragment
        requireActivity().setTitle("Employee");
    }
}