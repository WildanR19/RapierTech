package com.example.rapiertech.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.rapiertech.R;
import com.example.rapiertech.activity.SessionManager;
import com.example.rapiertech.api.ApiClient;
import com.example.rapiertech.api.ApiInterface;
import com.example.rapiertech.model.project.Project;
import com.example.rapiertech.model.project.ProjectData;
import com.example.rapiertech.model.project.ProjectMember;
import com.example.rapiertech.model.project.ProjectMemberData;
import com.example.rapiertech.ui.project.ProjectDetailFragment;
import com.example.rapiertech.ui.project.ProjectEditorFragment;
import com.example.rapiertech.ui.project.ProjectFragment;
import com.example.rapiertech.widget.Widget;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterProject extends RecyclerView.Adapter<AdapterProject.ViewHolder> {

    private final Context context;
    private final List<ProjectData> projectDataList;
    private List<ProjectMemberData> projectMemberDataList;
    private final ProjectFragment projectFragment;
    private SessionManager sessionManager;
    private Widget widget;
    private ApiInterface apiInterface;

    public AdapterProject(Context context, List<ProjectData> projectDataList, ProjectFragment projectFragment){
        this.context = context;
        this.projectDataList = projectDataList;
        this.projectFragment = projectFragment;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_project,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        ProjectData projectData = projectDataList.get(position);
        sessionManager = new SessionManager(context);
        widget = new Widget();
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        holder.tvStatus.setText(widget.capitalizeText(projectData.getStatus()));
        if (projectData.getStatus().equalsIgnoreCase("in progress")){
            holder.tvStatus.setTextColor(context.getColor(R.color.light_blue));
        } else if (projectData.getStatus().equalsIgnoreCase("not started")){
            holder.tvStatus.setTextColor(context.getColor(R.color.text));
        } else if (projectData.getStatus().equalsIgnoreCase("on hold")){
            holder.tvStatus.setTextColor(context.getColor(R.color.light_yellow));
        } else if (projectData.getStatus().equalsIgnoreCase("canceled")){
            holder.tvStatus.setTextColor(context.getColor(R.color.light_red));
        } else {
            holder.tvStatus.setTextColor(context.getColor(R.color.light_green));
        }
        holder.tvDeadline.setText(widget.changeDateFormat(projectData.getDeadline()));
        holder.tvName.setText(widget.capitalizeText(projectData.getProjectName()));
        holder.tvCreated.setText(widget.changeDateFormat(projectData.getCreatedAt()));
        getTeamMeber(holder, projectData.getId());

        holder.itemView.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
            View view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_project, null);
            LinearLayout llEdit, llDelete, llDetail;
            llEdit = view.findViewById(R.id.ll_edit_project);
            llDelete = view.findViewById(R.id.ll_delete_project);
            llDetail = view.findViewById(R.id.ll_detail_project);

            if (!sessionManager.getRoleId().equals("1")) {
                llEdit.setVisibility(View.GONE);
                llDelete.setVisibility(View.GONE);
            }

            Bundle bundle = new Bundle();
            bundle.putInt("id", projectData.getId());
            bundle.putString("projectName", projectData.getProjectName());
            bundle.putInt("categoryId", projectData.getCategoryId());
            bundle.putString("startDate", projectData.getStartDate());
            bundle.putString("deadline", projectData.getDeadline());
            bundle.putString("status", projectData.getStatus());
            bundle.putString("summary", projectData.getProjectSummary());
            bundle.putString("note", projectData.getNotes());

            llEdit.setOnClickListener(vi -> {
                bundle.putBoolean("isEditable", true);
                moveFragment(v, bundle);
                bottomSheetDialog.dismiss();
            });

            llDelete.setOnClickListener(vi -> {
                BottomSheetMaterialDialog mDialog = new BottomSheetMaterialDialog.Builder((Activity) context)
                        .setTitle("Delete?")
                        .setMessage("Are you sure want to delete this data?")
                        .setCancelable(false)
                        .setPositiveButton("Delete", R.drawable.ic_delete_, (dialogInterface, which) -> {
                            deleteData(projectData.getId());
                            dialogInterface.dismiss();
                        })
                        .setNegativeButton("Cancel", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss())
                        .build();
                mDialog.show();
                bottomSheetDialog.dismiss();
            });

            llDetail.setOnClickListener(vi -> {
                bundle.putString("submittedName", projectData.getSubmittedByName());
                Fragment fragment = new ProjectDetailFragment();
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = ((FragmentActivity)v.getContext()).getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit();
                bottomSheetDialog.dismiss();
            });

            bottomSheetDialog.setContentView(view);
            bottomSheetDialog.show();
        });
    }

    private void deleteData(int id) {
        Call<Project> deleteData = apiInterface.projectDeleteData(id);
        deleteData.enqueue(new Callback<Project>() {
            @Override
            public void onResponse(@NotNull Call<Project> call, @NotNull Response<Project> response) {
                if (response.body() != null) {
                    if (response.isSuccessful() && response.body().isStatus()) {
                        widget.successToast(response.body().getMessage(), projectFragment.requireActivity());
                        projectFragment.retrieveData();
                    } else {
                        widget.errorToast(response.body().getMessage(), projectFragment.requireActivity());
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<Project> call, @NotNull Throwable t) {
                widget.noConnectToast(t.getMessage(), projectFragment.requireActivity());
            }
        });
    }

    private void moveFragment(View v, Bundle bundle) {
        Fragment fragment = new ProjectEditorFragment();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = ((FragmentActivity)v.getContext()).getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit();
    }

    private void getTeamMeber(ViewHolder holder, int id) {
        Call<ProjectMember> memberShowData = apiInterface.projectMemberRetrieveData(id);
        memberShowData.enqueue(new Callback<ProjectMember>() {
            @Override
            public void onResponse(@NotNull Call<ProjectMember> call, @NotNull Response<ProjectMember> response) {
                if (response.body() != null) {
                    if (response.isSuccessful() && response.body().isStatus()) {
                        projectMemberDataList = response.body().getData();
                        if (projectMemberDataList.size() > 4){
                            int moreMember = projectMemberDataList.size()-4;
                            holder.tvMore.setVisibility(View.VISIBLE);
                            holder.tvMore.setText(" and "+moreMember+" more");
                        }
                        for (int i = 0; i < projectMemberDataList.size(); i++){
                            if (i < 4){
                                ImageView imageView = new ImageView(context);
                                imageView.setLayoutParams(new ViewGroup.LayoutParams(50,50));
                                imageView.setMaxHeight(25);
                                imageView.setMaxWidth(25);
                                if (projectMemberDataList.get(i).getProfilePhotoPath() != null){
                                    String urlImage = ApiClient.getStorage() + projectMemberDataList.get(i).getProfilePhotoPath();
                                    Glide.with(context).load(urlImage).diskCacheStrategy(DiskCacheStrategy.NONE).into(imageView);
                                    imageView.setBackgroundResource(R.drawable.circle);
                                    imageView.setClipToOutline(true);
                                } else {
                                    imageView.setImageDrawable(context.getDrawable(R.drawable.ic_account));
                                }

                                holder.llMember.addView(imageView);
                            }
                        }
                    } else {
                        widget.errorToast("Project Member " + response.body().getMessage(), projectFragment.requireActivity());
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ProjectMember> call, @NotNull Throwable t) {
                widget.noConnectToast("Project Member " + t.getMessage(), projectFragment.requireActivity());
            }
        });
    }

    @Override
    public int getItemCount() {
        return projectDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvStatus, tvDeadline, tvName, tvCreated, tvMore;
        LinearLayout llMember;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tvStatus = itemView.findViewById(R.id.statusProject);
            tvDeadline = itemView.findViewById(R.id.deadlineProject);
            tvName = itemView.findViewById(R.id.nameProject);
            tvCreated = itemView.findViewById(R.id.createdProject);
            tvMore = itemView.findViewById(R.id.moreMemberProject);
            llMember = itemView.findViewById(R.id.llImageMemberProject);
        }
    }
}
