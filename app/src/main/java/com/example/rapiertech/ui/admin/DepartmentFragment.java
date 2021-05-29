package com.example.rapiertech.ui.admin;

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
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.rapiertech.R;
import com.example.rapiertech.adapter.AdapterDataDepartment;
import com.example.rapiertech.api.ApiClient;
import com.example.rapiertech.api.ApiInterface;
import com.example.rapiertech.model.department.Department;
import com.example.rapiertech.model.department.DepartmentData;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import www.sanju.motiontoast.MotionToast;

public class DepartmentFragment extends Fragment {

//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    private String mParam1;
//    private String mParam2;
//
//    public DepartmentFragment() {
//        // Required empty public constructor
//    }

    private RecyclerView rvData;
    private RecyclerView.Adapter adData;
    private List<DepartmentData> listData = new ArrayList<>();
    private SwipeRefreshLayout srlData;
    private ProgressBar loadingData;
    private EditText etName;
    private String name;

//    public static DepartmentFragment newInstance(String param1, String param2) {
//        DepartmentFragment fragment = new DepartmentFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_department, container, false);

        rvData = root.findViewById(R.id.rvDataDepartment);
        RecyclerView.LayoutManager lmData = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        rvData.setLayoutManager(lmData);

        srlData = root.findViewById(R.id.srlDataDepartment);
        srlData.setOnRefreshListener(() -> {
            srlData.setRefreshing(true);
            retrieveData();
            srlData.setRefreshing(false);
        });

        loadingData = root.findViewById(R.id.loadingDataDepartment);

        FloatingActionButton fab = root.findViewById(R.id.fab_department);
        fab.setOnClickListener(v -> showDialog());

        return root;

    }

    public void showDialog(){

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_dialog_deptjob, null);
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
        Call<Department> createdata = apiData.departmentCreateData(name);

        createdata.enqueue(new Callback<Department>() {
            @Override
            public void onResponse(Call<Department> call, Response<Department> response) {
                if (response.body() != null && response.isSuccessful() && response.body().isStatus()){
                    successToast(response.body().getMessage());
                    retrieveData();
                }else
                    errorToast(response.body().getMessage());
            }

            @Override
            public void onFailure(Call<Department> call, Throwable t) {
                noConnectToast(t.getMessage());
            }
        });
    }

    public void retrieveData() {

        loadingData.setVisibility(View.VISIBLE);

        ApiInterface apiData = ApiClient.getClient().create(ApiInterface.class);
        Call<Department> showdata = apiData.departmentRetrieveData();

        showdata.enqueue(new Callback<Department>() {
            @Override
            public void onResponse(Call<Department> call, Response<Department> response) {
                if (response.body() != null && response.isSuccessful() && response.body().isStatus()){
                    listData = response.body().getData();
                    adData = new AdapterDataDepartment(requireActivity(), listData, DepartmentFragment.this);
                    rvData.setAdapter(adData);
                    adData.notifyDataSetChanged();
                    loadingData.setVisibility(View.INVISIBLE);
                }else{
                    errorToast(response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<Department> call, Throwable t) {
                noConnectToast(t.getMessage());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        retrieveData();
        //Set title of this fragment
        requireActivity().setTitle("Department");
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