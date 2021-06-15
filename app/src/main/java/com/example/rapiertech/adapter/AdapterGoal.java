package com.example.rapiertech.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.rapiertech.R;
import com.example.rapiertech.activity.SessionManager;
import com.example.rapiertech.api.ApiClient;
import com.example.rapiertech.api.ApiInterface;
import com.example.rapiertech.model.goal.GoalData;
import com.example.rapiertech.ui.goal.GoalFragment;
import com.example.rapiertech.widget.Widget;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AdapterGoal extends RecyclerView.Adapter<AdapterGoal.ViewHolder> {

    private final Context context;
    private SessionManager sessionManager;
    private Widget widget;
    private ApiInterface apiInterface;
    private List<GoalData> goalDataList;
    private GoalFragment goalFragment;

    public AdapterGoal(Context context, List<GoalData> goalDataList, GoalFragment goalFragment) {
        this.context = context;
        this.goalDataList = goalDataList;
        this.goalFragment = goalFragment;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_goal,parent,false);
        return new AdapterGoal.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        GoalData goalData = goalDataList.get(position);
        sessionManager = new SessionManager(context);
        widget = new Widget();
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        holder.tvEmpName.setText(widget.capitalizeText(goalData.getUserName()));
        holder.tvDeadline.setText(widget.changeDateFormat(goalData.getDueDate()));
        holder.tvGoalName.setText(widget.capitalizeText(goalData.getTitle()));
        holder.tvProgress.setText(goalData.getProgressPercent() + "% Complete");
        holder.progressGoal.setProgress(goalData.getProgressPercent());

        String urlImage = ApiClient.getStorage() + goalData.getProfilePhotoPath();
        Glide.with(context).load(urlImage).diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.imgEmp);
        holder.imgEmp.setClipToOutline(true);
    }

    @Override
    public int getItemCount() {
        return goalDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvEmpName, tvDeadline, tvGoalName, tvProgress;
        ImageView imgEmp;
        ProgressBar progressGoal;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tvEmpName = itemView.findViewById(R.id.nameEmpGoal);
            tvDeadline = itemView.findViewById(R.id.deadlineGoal);
            tvGoalName = itemView.findViewById(R.id.nameGoal);
            imgEmp = itemView.findViewById(R.id.imageGoal);
            progressGoal = itemView.findViewById(R.id.progressGoal);
            tvProgress = itemView.findViewById(R.id.tvProgressGoal);
        }
    }
}
