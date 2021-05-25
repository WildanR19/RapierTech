package com.example.rapiertech.ui.department;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.rapiertech.R;
import com.example.rapiertech.adapter.AdapterDataDepartment;
import com.example.rapiertech.api.ApiClient;
import com.example.rapiertech.api.ApiInterface;
import com.example.rapiertech.model.department.Department;
import com.example.rapiertech.model.department.DepartmentData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DepartmentFragment extends Fragment {

//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    public DepartmentFragment() {
//        // Required empty public constructor
//    }

    private RecyclerView rvData;
    private RecyclerView.Adapter adData;
    private RecyclerView.LayoutManager lmData;
    private List<DepartmentData> listData = new ArrayList<>();
    private SwipeRefreshLayout srlData;
    private ProgressBar loadingData;
    private FloatingActionButton fab;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_department, container, false);

        rvData = root.findViewById(R.id.rvDataDepartment);
        lmData = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvData.setLayoutManager(lmData);

        srlData = root.findViewById(R.id.srlDataDepartment);
        srlData.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srlData.setRefreshing(true);
                retrieveData();
                srlData.setRefreshing(false);
            }
        });

        loadingData = root.findViewById(R.id.loadingDataDepartment);

        fab = root.findViewById(R.id.fab_department);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        return root;

    }

    public void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_dialog, null);
        etName = view.findViewById(R.id.add_name);

        builder.setView(view)
                .setTitle(R.string.add_title_dialog)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        name = etName.getText().toString();
                        createData();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void createData() {
        ApiInterface apiData = ApiClient.getClient().create(ApiInterface.class);
        Call<Department> createdata = apiData.departmentCreateData(name);

        createdata.enqueue(new Callback<Department>() {
            @Override
            public void onResponse(Call<Department> call, Response<Department> response) {
                if (response.body() != null && response.isSuccessful() && response.body().isStatus()){
                    String message = response.body().getMessage();

                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    retrieveData();
                }else{
                    Toast.makeText(getActivity(), "!! "+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Department> call, Throwable t) {
                Toast.makeText(getActivity(), "Cannot connect server"+t.getMessage(), Toast.LENGTH_SHORT).show();
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
                    adData = new AdapterDataDepartment(getActivity(), listData);
                    rvData.setAdapter(adData);
                    adData.notifyDataSetChanged();
                }else{
                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
                loadingData.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<Department> call, Throwable t) {
                Toast.makeText(getActivity(), "Cannot connect server"+t.getMessage(), Toast.LENGTH_SHORT).show();
                loadingData.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();
        retrieveData();
        //Set title of this fragment
        if (getActivity() != null)
        {
            getActivity().setTitle("Department");
        }
    }
}