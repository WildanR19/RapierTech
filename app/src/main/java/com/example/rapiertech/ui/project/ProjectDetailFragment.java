package com.example.rapiertech.ui.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.rapiertech.R;
import com.example.rapiertech.activity.SessionManager;
import com.example.rapiertech.adapter.AdapterProjectActivity;
import com.example.rapiertech.api.ApiClient;
import com.example.rapiertech.api.ApiInterface;
import com.example.rapiertech.model.project.ProjectActivity;
import com.example.rapiertech.model.project.ProjectActivityData;
import com.example.rapiertech.model.project.ProjectMember;
import com.example.rapiertech.model.project.ProjectMemberData;
import com.example.rapiertech.widget.Widget;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProjectDetailFragment extends Fragment {

    private View view;
    private RecyclerView rvData;
    private RecyclerView.Adapter adData;
    private SwipeRefreshLayout srlData;
    private ProgressBar loading;
    private ApiInterface apiInterface;
    private SessionManager sessionManager;
    private Widget widget;
    private FragmentManager fragmentManager;
    private List<ProjectActivityData> projectActivityDataList;
    private List<ProjectMemberData> projectMemberDataList;
    private TextView tvStatus, tvDeadline, tvName, tvSubmitted, tvSummary, tvNote, tvTotalMember, tvTotalUpdate, tvMore;
    private ImageView imgSubmitted;
    private MaterialButton mButtonSubmit;
    private LinearLayout llMember;
    private int projectId, categoryId;
    private String projectName, deadline, status, summary, note, submittedByName;
    private EditText etComment;
    private Button btnFile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (getArguments() != null) {
            projectId = bundle.getInt("id");
            projectName = bundle.getString("projectName");
            categoryId = bundle.getInt("categoryId");
            deadline = bundle.getString("deadline");
            status = bundle.getString("status");
            summary = bundle.getString("summary");
            note = bundle.getString("note");
            submittedByName = bundle.getString("submittedName");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_project_detail, container, false);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        widget = new Widget();
        fragmentManager = requireActivity().getSupportFragmentManager();
        sessionManager = new SessionManager(getContext());

        findView();
        initProjectDetail();

        srlData.setOnRefreshListener(() -> {
            srlData.setRefreshing(true);
            retrieveData();
            srlData.setRefreshing(false);
        });

        submitData();
        return view;
    }

    private void submitData() {
        mButtonSubmit.setOnClickListener(v -> {
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View vi = inflater.inflate(R.layout.dialog_project_activity, null);
            etComment = vi.findViewById(R.id.etCommentProjectActivity);
            btnFile = vi.findViewById(R.id.btnSelectFile);

            btnFile.setOnClickListener(viw -> {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, 21);

            });

            MaterialAlertDialogBuilder mDialog = new MaterialAlertDialogBuilder(requireActivity());
            mDialog.setView(vi)
                    .setTitle(R.string.submit_an_update)
                    .setPositiveButton(R.string.save, (dialog, which) -> {
                        createData();
                        dialog.dismiss();
                    })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void createData() {
        String filePath = "";
        File file = new File(filePath);

        int userId = Integer.parseInt(sessionManager.getUserDetail().get(SessionManager.USER_ID));



        RequestBody userIdPart = RequestBody.create(MultipartBody.FORM, String.valueOf(userId));
        RequestBody projectIdPart = RequestBody.create(MultipartBody.FORM, String.valueOf(projectId));
        RequestBody commentPart = RequestBody.create(MultipartBody.FORM, etComment.getText().toString());
        RequestBody filePart = RequestBody.create(MediaType.parse("file/*"), file);
        MultipartBody.Part parts = MultipartBody.Part.createFormData("newFile", file.getName(), filePart);

        Call<ProjectActivity> submitUpdate = apiInterface.projectActivitySubmitData(userIdPart, projectIdPart, commentPart, parts);
        submitUpdate.enqueue(new Callback<ProjectActivity>() {
            @Override
            public void onResponse(@NotNull Call<ProjectActivity> call, @NotNull Response<ProjectActivity> response) {

            }

            @Override
            public void onFailure(@NotNull Call<ProjectActivity> call, @NotNull Throwable t) {

            }
        });
    }

    public void retrieveData() {
        loading.setVisibility(View.VISIBLE);

        Call<ProjectActivity> retrieveData = apiInterface.projectActivityRetrieveData(projectId);
        retrieveData.enqueue(new Callback<ProjectActivity>() {
            @Override
            public void onResponse(@NotNull Call<ProjectActivity> call, @NotNull Response<ProjectActivity> response) {
                if (response.body() != null) {
                    if (response.isSuccessful() && response.body().isStatus()) {
                        projectActivityDataList = response.body().getData();
                        adData = new AdapterProjectActivity(requireActivity(), projectActivityDataList, ProjectDetailFragment.this);
                        rvData.setAdapter(adData);
                        adData.notifyDataSetChanged();
                        tvTotalUpdate.setText(Integer.toString(projectActivityDataList.size()));
                    } else {
                        widget.errorToast(response.body().getMessage(), requireActivity());
                    }
                    loading.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ProjectActivity> call, @NotNull Throwable t) {
                widget.noConnectToast(t.getMessage(), requireActivity());
                loading.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void initProjectDetail() {
        // Card Atas
        tvStatus.setText(widget.capitalizeText(status));
        if (status.equalsIgnoreCase("in progress")){
            tvStatus.setTextColor(requireActivity().getColor(R.color.light_blue));
        } else if (status.equalsIgnoreCase("not started")){
            tvStatus.setTextColor(requireActivity().getColor(R.color.text));
        } else if (status.equalsIgnoreCase("on hold")){
            tvStatus.setTextColor(requireActivity().getColor(R.color.light_yellow));
        } else if (status.equalsIgnoreCase("canceled")){
            tvStatus.setTextColor(requireActivity().getColor(R.color.light_red));
        } else {
            tvStatus.setTextColor(requireActivity().getColor(R.color.light_green));
        }
        tvDeadline.setText(widget.changeDateFormat(deadline));
        tvName.setText(widget.capitalizeText(projectName));
        tvSubmitted.setText(widget.capitalizeText(submittedByName));
        tvSummary.setText(summary);
        tvNote.setText(note);
        String urlImage = ApiClient.getStorage() + sessionManager.getUserDetail().get(SessionManager.PHOTO_PATH);
        Glide.with(requireActivity()).load(urlImage).diskCacheStrategy(DiskCacheStrategy.NONE).into(imgSubmitted);
        getTeamMember();
    }

    private void getTeamMember() {
        Call<ProjectMember> memberShowData = apiInterface.projectMemberRetrieveData(projectId);
        memberShowData.enqueue(new Callback<ProjectMember>() {
            @Override
            public void onResponse(@NotNull Call<ProjectMember> call, @NotNull Response<ProjectMember> response) {
                if (response.body() != null) {
                    if (response.isSuccessful() && response.body().isStatus()) {
                        projectMemberDataList = response.body().getData();
                        if (projectMemberDataList.size() > 4){
                            int moreMember = projectMemberDataList.size()-4;
                            tvMore.setVisibility(View.VISIBLE);
                            tvMore.setText(" and "+moreMember+" more");
                        }
                        for (int i = 0; i < projectMemberDataList.size(); i++){
                            if (i < 4){
                                ImageView imageView = new ImageView(requireActivity());
                                imageView.setLayoutParams(new ViewGroup.LayoutParams(50,50));
                                imageView.setMaxHeight(25);
                                imageView.setMaxWidth(25);

                                if (projectMemberDataList.get(i).getProfilePhotoPath() != null){
                                    String urlImage = ApiClient.getStorage() + projectMemberDataList.get(i).getProfilePhotoPath();
                                    Glide.with(requireActivity()).load(urlImage).diskCacheStrategy(DiskCacheStrategy.NONE).into(imageView);
                                    imageView.setBackgroundResource(R.drawable.circle);
                                    imageView.setClipToOutline(true);
                                } else {
                                    imageView.setImageDrawable(requireActivity().getDrawable(R.drawable.ic_account));
                                }

                                llMember.addView(imageView);
                            }
                        }
                        tvTotalMember.setText(Integer.toString(projectMemberDataList.size()));
                    } else {
                        widget.errorToast("Project Member " + response.body().getMessage(), requireActivity());
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ProjectMember> call, @NotNull Throwable t) {
                widget.noConnectToast("Project Member " + t.getMessage(), requireActivity());
            }
        });
    }

    private void findView() {
        tvStatus = view.findViewById(R.id.statusProjectDetail);
        tvDeadline = view.findViewById(R.id.deadlineProjectDetail);
        tvName = view.findViewById(R.id.nameProjectDetail);
        tvSubmitted = view.findViewById(R.id.submittedProjectDetail);
        imgSubmitted = view.findViewById(R.id.imageProjectDetail);
        tvSummary = view.findViewById(R.id.summaryProjectDetail);
        tvNote = view.findViewById(R.id.noteProjectDetail);
        tvTotalMember = view.findViewById(R.id.totalMemberProjectDetail);
        llMember = view.findViewById(R.id.llImageMemberProjectDetail);
        tvMore = view.findViewById(R.id.moreMemberProjectDetail);
        tvTotalUpdate = view.findViewById(R.id.totalUpdateProjectDetail);

        rvData = view.findViewById(R.id.rvDataProjectDetail);
        RecyclerView.LayoutManager lmData = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        rvData.setLayoutManager(lmData);

        loading = view.findViewById(R.id.loadingProjectDetail);
        srlData = view.findViewById(R.id.srlDataProjectDetail);
        mButtonSubmit = view.findViewById(R.id.btnSubmitUpdateProject);
    }

    @Override
    public void onResume() {
        super.onResume();

        retrieveData();
        requireActivity().setTitle("Project Details");
    }
}