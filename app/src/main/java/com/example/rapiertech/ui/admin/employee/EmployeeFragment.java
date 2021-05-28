package com.example.rapiertech.ui.admin.employee;

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
import com.example.rapiertech.adapter.AdapterDataEmployee;
import com.example.rapiertech.api.ApiClient;
import com.example.rapiertech.api.ApiInterface;
import com.example.rapiertech.model.employee.Employee;
import com.example.rapiertech.model.employee.EmployeeData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import www.sanju.motiontoast.MotionToast;

public class EmployeeFragment extends Fragment {

    private RecyclerView rvData;
    private RecyclerView.Adapter adData;
    private List<EmployeeData> dataList = new ArrayList<>();
    private SwipeRefreshLayout srlData;
    private ProgressBar loadingData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_employee, container, false);
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
            public void onResponse(Call<Employee> call, Response<Employee> response) {
                if (response.body() != null && response.isSuccessful() && response.body().isStatus()){
                    dataList = response.body().getData();
                    adData = new AdapterDataEmployee(requireActivity(), dataList);
                    rvData.setAdapter(adData);
                    adData.notifyDataSetChanged();
                }else{
                    MotionToast.Companion.createColorToast(requireActivity(), "Error",
                            response.body().getMessage(),
                            MotionToast.TOAST_ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(requireActivity(),R.font.helvetica_regular)
                    );
                }
                loadingData.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<Employee> call, Throwable t) {
                MotionToast.Companion.createColorToast(requireActivity(), "Cannot connect server",
                        t.getMessage(),
                        MotionToast.TOAST_NO_INTERNET,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(requireActivity(),R.font.helvetica_regular)
                );
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