package com.example.rapiertech.adapter;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.rapiertech.R;
import com.example.rapiertech.activity.SessionManager;
import com.example.rapiertech.api.ApiClient;
import com.example.rapiertech.api.ApiInterface;
import com.example.rapiertech.model.project.ProjectActivity;
import com.example.rapiertech.model.project.ProjectActivityData;
import com.example.rapiertech.ui.project.ProjectDetailFragment;
import com.example.rapiertech.widget.Widget;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterProjectActivity extends RecyclerView.Adapter<AdapterProjectActivity.ViewHolder> {

    private Context context;
    private SessionManager sessionManager;
    private Widget widget;
    private ApiInterface apiInterface;
    private ProjectDetailFragment projectDetailFragment;
    private List<ProjectActivityData> projectActivityDataList;

    public AdapterProjectActivity(Context context, List<ProjectActivityData> projectActivityDataList, ProjectDetailFragment projectDetailFragment){
        this.context = context;
        this.projectActivityDataList = projectActivityDataList;
        this.projectDetailFragment = projectDetailFragment;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_project_detail, parent, false);
        return new AdapterProjectActivity.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        ProjectActivityData projectActivityData = projectActivityDataList.get(position);
        sessionManager = new SessionManager(context);
        widget = new Widget();
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        holder.tvNameEmp.setText(widget.capitalizeText(projectActivityData.getUserName()));
        holder.tvDate.setText(widget.changeDateFormatToDateTime(projectActivityData.getUpdatedAt()));
        holder.tvComment.setText(projectActivityData.getComment());
        String urlImage = ApiClient.getStorage() + projectActivityData.getProfilePhotoPath();
        Glide.with(context).load(urlImage).diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.imgEmp);

        if (projectActivityData.getFile() != null){
            holder.tvDownload.setOnClickListener(v -> downloadFile(projectActivityData.getFile()));
        } else {
            holder.llDownload.setVisibility(View.GONE);
        }

        if (sessionManager.getRoleId().equals("1") || sessionManager.getUserDetail().get(SessionManager.USER_ID).equals(projectActivityData.getUserId())){
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    BottomSheetMaterialDialog mDialog = new BottomSheetMaterialDialog.Builder((Activity) context)
                            .setTitle("Delete?")
                            .setMessage("Are you sure want to delete this data?")
                            .setCancelable(false)
                            .setPositiveButton("Delete", R.drawable.ic_delete_, (dialogInterface, which) -> {
                                deleteData(projectActivityData.getId());
                                dialogInterface.dismiss();
                            })
                            .setNegativeButton("Cancel", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss())
                            .build();
                    mDialog.show();
                    return false;
                }
            });
        }
    }

    private void deleteData(int id) {
        Call<ProjectActivity> deleteData = apiInterface.projectActivityDeleteData(id);
        deleteData.enqueue(new Callback<ProjectActivity>() {
            @Override
            public void onResponse(Call<ProjectActivity> call, Response<ProjectActivity> response) {
                if (response.body() != null) {
                    if (response.isSuccessful() && response.body().isStatus()) {
                        widget.successToast(response.body().getMessage(), projectDetailFragment.requireActivity());
                        projectDetailFragment.retrieveData();
                    } else {
                        widget.errorToast(response.body().getMessage(), projectDetailFragment.requireActivity());
                    }
                }
            }

            @Override
            public void onFailure(Call<ProjectActivity> call, Throwable t) {
                widget.noConnectToast(t.getMessage(), projectDetailFragment.requireActivity());
            }
        });
    }

    private void downloadFile(String file) {
        String url = ApiClient.getStorage() + file;

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        String title = URLUtil.guessFileName(url, null, null);
        request.setTitle(title);
        request.setDescription("Downloading File Please Wait......");
        String cookie = CookieManager.getInstance().getCookie(url);
        request.addRequestHeader("cookie", cookie);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title);

        DownloadManager downloadManager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
        Toast.makeText(projectDetailFragment.requireActivity(), "Downloading Started", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return projectActivityDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvNameEmp, tvComment, tvDownload, tvDate;
        ImageView imgEmp;
        LinearLayout llDownload;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            imgEmp = itemView.findViewById(R.id.imageProjectUpdate);
            tvNameEmp = itemView.findViewById(R.id.nameProjectUpdate);
            tvDate = itemView.findViewById(R.id.updatedProjectUpdate);
            tvComment = itemView.findViewById(R.id.commentProjectUpdate);
            tvDownload = itemView.findViewById(R.id.downloadFileProjectUpdate);
            llDownload = itemView.findViewById(R.id.ll_fileDownloadProjectUpdate);
        }
    }
}
