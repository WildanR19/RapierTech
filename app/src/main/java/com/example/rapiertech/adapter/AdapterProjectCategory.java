package com.example.rapiertech.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.rapiertech.R;
import com.example.rapiertech.api.ApiClient;
import com.example.rapiertech.api.ApiInterface;
import com.example.rapiertech.model.project.ProjectCategory;
import com.example.rapiertech.model.project.ProjectCategoryData;
import com.example.rapiertech.ui.project.ProjectEditorFragment;
import com.example.rapiertech.widget.Widget;

import java.util.List;

import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterProjectCategory extends ArrayAdapter<ProjectCategoryData> {

    private final Context context;
    private ApiInterface apiInterface;
    private Widget widget;
    private List<ProjectCategoryData> projectCategoryDataList;
    private ProjectEditorFragment projectEditorFragment;


    public AdapterProjectCategory(@NonNull Context context, int resource, List<ProjectCategoryData> projectCategoryDataList, ProjectEditorFragment projectEditorFragment) {
        super(context, resource, projectCategoryDataList);
        this.context = context;
        this.projectCategoryDataList = projectCategoryDataList;
        this.projectEditorFragment = projectEditorFragment;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView==null){
            LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_category,null,true);
        }
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        widget = new Widget();

        ProjectCategoryData projectCategoryData = projectCategoryDataList.get(position);
        TextView tvName = convertView.findViewById(R.id.tvNameCategory);
        Button btnDelete = convertView.findViewById(R.id.btnDeleteCategory);

        tvName.setText(widget.capitalizeText(projectCategoryData.getCategoryName()));
        btnDelete.setOnClickListener(v -> {
            BottomSheetMaterialDialog mDialog = new BottomSheetMaterialDialog.Builder((Activity) context)
                    .setTitle("Delete?")
                    .setMessage("Are you sure want to delete this data?")
                    .setCancelable(false)
                    .setPositiveButton("Delete", R.drawable.ic_delete_, (dialogInterface, which) -> {
                        deleteData(projectCategoryData.getId());
                        dialogInterface.dismiss();
                    })
                    .setNegativeButton("Cancel", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss())
                    .build();
            mDialog.show();
        });

        return convertView;
    }

    private void deleteData(int id) {
        Call<ProjectCategory> deleteData = apiInterface.projectCategoryDeleteData(id);
        deleteData.enqueue(new Callback<ProjectCategory>() {
            @Override
            public void onResponse(Call<ProjectCategory> call, Response<ProjectCategory> response) {
                if (response.body() != null) {
                    if (response.isSuccessful() && response.body().isStatus()) {
                        widget.successToast(response.body().getMessage(), projectEditorFragment.requireActivity());
                        projectEditorFragment.retrieveDataCategory();
                        projectEditorFragment.retrieveDataCategoryDialog();
                    } else {
                        widget.errorToast(response.body().getMessage(), projectEditorFragment.requireActivity());
                    }
                }
            }

            @Override
            public void onFailure(Call<ProjectCategory> call, Throwable t) {
                widget.noConnectToast(t.getMessage(), projectEditorFragment.requireActivity());
            }
        });
    }
}
