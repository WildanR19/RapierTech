package com.example.rapiertech.ui.department;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.rapiertech.R;
import com.example.rapiertech.adapter.AdapterDataDepartment;
import com.example.rapiertech.api.ApiClient;
import com.example.rapiertech.api.ApiInterface;
import com.example.rapiertech.model.department.Department;
import com.example.rapiertech.model.department.DepartmentData;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DepartmentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DepartmentFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DepartmentFragment() {
        // Required empty public constructor
    }

    private RecyclerView rvData;
    private RecyclerView.Adapter adData;
    private RecyclerView.LayoutManager lmData;
    private List<DepartmentData> listData = new ArrayList<>();

    public static DepartmentFragment newInstance(String param1, String param2) {
        DepartmentFragment fragment = new DepartmentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_department, container, false);

        rvData = root.findViewById(R.id.rvDataDepartment);
        lmData = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvData.setLayoutManager(lmData);

        retrieveData();
        return root;

    }

    private void retrieveData() {
        ApiInterface aiData = ApiClient.getClient().create(ApiInterface.class);
        Call<Department> tampildata = aiData.departmentRetrieveData();

        tampildata.enqueue(new Callback<Department>() {
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
            }

            @Override
            public void onFailure(Call<Department> call, Throwable t) {
                Toast.makeText(getActivity(), "Cannot connect server"+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //Set title of this fragment
        if (getActivity() != null)
        {
            getActivity().setTitle("Department");
        }
    }
}