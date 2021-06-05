package com.example.rapiertech.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.rapiertech.R;
import com.example.rapiertech.api.ApiClient;
import com.example.rapiertech.api.ApiInterface;
import com.example.rapiertech.model.basicpays.BasicPays;
import com.example.rapiertech.model.basicpays.BasicPaysData;
import com.example.rapiertech.ui.payslip.BasicSalaryFragment;
import com.example.rapiertech.widget.Widget;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterBasicSalary extends ArrayAdapter<BasicPaysData> {

    private Context context;
    private int resource;
    private List<BasicPaysData> basicPaysList;
    private ApiInterface apiInterface;
    private TextView tvjob, tvSalary, tvId;
    private Button btnDelete;
    private Widget widget;
    private BasicSalaryFragment basicSalaryFragment;

    public AdapterBasicSalary(@NonNull Context context, int resource, List<BasicPaysData> basicPaysList, BasicSalaryFragment basicSalaryFragment) {
        super(context, resource, basicPaysList);
        this.context = context;
        this.resource = resource;
        this.basicPaysList = basicPaysList;
        this.basicSalaryFragment = basicSalaryFragment;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView==null){
            LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_basic_salaries,null,true);
        }
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        widget = new Widget();

        BasicPaysData basicPaysData = basicPaysList.get(position);
        tvId = convertView.findViewById(R.id.tvIdBasic);
        tvjob = convertView.findViewById(R.id.tvJobBasic);
        tvSalary = convertView.findViewById(R.id.tvSalaryBasic);
        btnDelete = convertView.findViewById(R.id.btnDeleteBasic);

        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

        tvId.setText(String.valueOf(basicPaysData.getId()));
        tvjob.setText(basicPaysData.getName());
        tvSalary.setText(formatRupiah.format((double) basicPaysData.getAmount()));

        btnDelete.setOnClickListener(v -> {
            BottomSheetMaterialDialog mDialog = new BottomSheetMaterialDialog.Builder((Activity) context)
                    .setTitle("Delete?")
                    .setMessage("Are you sure want to delete this data?")
                    .setCancelable(false)
                    .setPositiveButton("Delete", R.drawable.ic_delete_, (dialogInterface, which) -> {
                        deleteData(basicPaysData.getId());
                        dialogInterface.dismiss();
                    })
                    .setNegativeButton("Cancel", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss())
                    .build();
            mDialog.show();
        });

        return convertView;
    }

    private void deleteData(int id) {
        Call<BasicPays> deleteData = apiInterface.basicPaysDeleteData(id);
        deleteData.enqueue(new Callback<BasicPays>() {
            @Override
            public void onResponse(Call<BasicPays> call, Response<BasicPays> response) {
                if (response.body() != null) {
                    if (response.isSuccessful() && response.body().isStatus()) {
                        widget.successToast(response.body().getMessage(), basicSalaryFragment.requireActivity());
                        basicSalaryFragment.retrieveData();
                    } else {
                        widget.errorToast(response.body().getMessage(), basicSalaryFragment.requireActivity());
                    }
                }
            }

            @Override
            public void onFailure(Call<BasicPays> call, Throwable t) {
                widget.noConnectToast(t.getMessage(), basicSalaryFragment.requireActivity());
            }
        });
    }
}
