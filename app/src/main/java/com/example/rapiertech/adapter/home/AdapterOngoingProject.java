package com.example.rapiertech.adapter.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.rapiertech.R;
import com.example.rapiertech.api.ApiClient;
import com.example.rapiertech.api.ApiInterface;
import com.example.rapiertech.model.project.ProjectData;
import com.example.rapiertech.model.project.ProjectMember;
import com.example.rapiertech.model.project.ProjectMemberData;
import com.example.rapiertech.ui.home.HomeFragment;
import com.example.rapiertech.widget.Widget;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterOngoingProject extends RecyclerView.Adapter<AdapterOngoingProject.ViewHolder> {
    private final Context context;
    private final List<ProjectData> projectDataList;
    private List<ProjectMemberData> projectMemberDataList;
    private final HomeFragment homeFragment;
    private Widget widget;
    private ApiInterface apiInterface;

    public AdapterOngoingProject(Context context, List<ProjectData> projectDataList, HomeFragment homeFragment) {
        this.context = context;
        this.projectDataList = projectDataList;
        this.homeFragment = homeFragment;
    }

    @NonNull
    @NotNull
    @Override
    public AdapterOngoingProject.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_project,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterOngoingProject.ViewHolder holder, int position) {
        ProjectData projectData = projectDataList.get(position);
        widget = new Widget();
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        holder.tvStatus.setText(widget.capitalizeText(projectData.getStatus()));
        holder.tvStatus.setTextColor(context.getColor(R.color.light_blue));
        holder.tvDeadline.setText(widget.changeDateFormat(projectData.getDeadline()));
        holder.tvName.setText(widget.capitalizeText(projectData.getProjectName()));
        holder.tvCreated.setText(widget.changeDateFormat(projectData.getCreatedAt()));
        getTeamMeber(holder, projectData.getId());
    }

    private void getTeamMeber(AdapterOngoingProject.ViewHolder holder, int id) {
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
                        widget.errorToast("Project Member " + response.body().getMessage(), homeFragment.requireActivity());
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ProjectMember> call, @NotNull Throwable t) {
                widget.noConnectToast("Project Member " + t.getMessage(), homeFragment.requireActivity());
            }
        });
    }

    @Override
    public int getItemCount() {
        int limit = 3;
        return Math.min(projectDataList.size(), limit);
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
