package com.example.rapiertech.ui.project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.rapiertech.R;
import com.example.rapiertech.activity.SessionManager;
import com.example.rapiertech.adapter.AdapterProjectCategory;
import com.example.rapiertech.api.ApiClient;
import com.example.rapiertech.api.ApiInterface;
import com.example.rapiertech.model.employee.Employee;
import com.example.rapiertech.model.employee.EmployeeData;
import com.example.rapiertech.model.project.Project;
import com.example.rapiertech.model.project.ProjectCategory;
import com.example.rapiertech.model.project.ProjectCategoryData;
import com.example.rapiertech.model.project.ProjectMember;
import com.example.rapiertech.model.project.ProjectMemberData;
import com.example.rapiertech.widget.Widget;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProjectEditorFragment extends Fragment {

    private View view;
    private ApiInterface apiInterface;
    private Menu actionMenu;
    private FragmentManager fm;
    private Widget widget;
    private SessionManager sessionManager;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private EditText etName, etStartDate, etDeadline, etSummary, etNote;
    private EditText[] etList;
    private AutoCompleteTextView acCategory, acStatus;
    private MultiAutoCompleteTextView macMember;
    private AutoCompleteTextView[] actvList;
    private TextInputLayout tilStatus;
    private ImageButton btnAddCategory;
    private int projectId, categoryId;
    private String projectName, startDate, deadline, status, summary, note;
    private List<ProjectCategoryData> projectCategoryDataList = new ArrayList<>();
    private List<EmployeeData> employeeDataList = new ArrayList<>();
    private final String[] statusList = {"Not Started","In Progress","On Hold","Canceled","Finished"};
    private ListView listView;
    private final List<Integer> memberId = new ArrayList<>();
    private boolean isEditable = true;

    public ProjectEditorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (getArguments() != null) {
            projectId = bundle.getInt("id");
            projectName = bundle.getString("projectName");
            categoryId = bundle.getInt("categoryId");
            startDate = bundle.getString("startDate");
            deadline = bundle.getString("deadline");
            status = bundle.getString("status");
            summary = bundle.getString("summary");
            note = bundle.getString("note");
            isEditable = bundle.getBoolean("isEditable");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_project_editor, container, false);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        widget = new Widget();
        fm = requireActivity().getSupportFragmentManager();
        sessionManager = new SessionManager(getContext());

        calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC+7"));
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC+7"));

        setHasOptionsMenu(true);
        findView();
        initUi();
        return view;
    }

    private void initUi() {
        etList = new EditText[]{etName, etStartDate, etDeadline, etSummary, etNote};
        actvList = new AutoCompleteTextView[]{acCategory, acStatus};

        for (AutoCompleteTextView autoCompleteTextView : actvList){
            autoCompleteTextView.setFocusable(false);
            widget.setEnableAutoCompleteTextView(autoCompleteTextView, isEditable);
        }
        for (EditText editText : etList){
            widget.setEnableEditText(editText, isEditable);
        }
        widget.setEnableMultiAutoCompleteTextView(macMember, isEditable);

        if (projectId == 0){
            tilStatus.setVisibility(View.GONE);
        } else {
            etName.setText(projectName);
            etStartDate.setText(widget.changeDateFormat(startDate));
            etDeadline.setText(widget.changeDateFormat(deadline));
            acStatus.setText(widget.capitalizeText(status));
            etSummary.setText(summary);
            etNote.setText(note);
            retrieveDataProjectMember();
            if (!isEditable){
                btnAddCategory.setVisibility(View.GONE);
            }
        }

        retrieveDataCategory();
        retrieveDataStatus();
        retrieveDataMember();

        MaterialDatePicker.Builder mDateBuilder = MaterialDatePicker.Builder.datePicker().setTitleText("Select a Date");
        etStartDate.setOnClickListener(v -> {
            Long time = null;
            if (startDate != null){
                try {
                    Date date = dateFormat.parse(startDate);
                    time = date.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                mDateBuilder.setSelection(time);
            }
            MaterialDatePicker<Long> mDatePicker = mDateBuilder.build();
            mDatePicker.show(requireActivity().getSupportFragmentManager(), null);
            mDatePicker.addOnPositiveButtonClickListener(selection -> {
                calendar.setTimeInMillis(selection);
                etStartDate.setText(mDatePicker.getHeaderText());
                startDate = dateFormat.format(calendar.getTime());
            });
        });
        etStartDate.setFocusable(false);

        etDeadline.setOnClickListener(v -> {
            Long time = null;
            if (deadline != null){
                try {
                    Date date = dateFormat.parse(deadline);
                    time = date.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                mDateBuilder.setSelection(time);
            }
            MaterialDatePicker<Long> mDatePicker = mDateBuilder.build();
            mDatePicker.show(requireActivity().getSupportFragmentManager(), null);
            mDatePicker.addOnPositiveButtonClickListener(selection -> {
                calendar.setTimeInMillis(selection);
                etDeadline.setText(mDatePicker.getHeaderText());
                deadline = dateFormat.format(calendar.getTime());
            });
        });
        etDeadline.setFocusable(false);

        btnAddCategory.setOnClickListener(v -> showDialog());
    }

    private void retrieveDataProjectMember() {
        Call<ProjectMember> getProjectMemberData = apiInterface.projectMemberRetrieveData(projectId);
        getProjectMemberData.enqueue(new Callback<ProjectMember>() {
            @Override
            public void onResponse(Call<ProjectMember> call, Response<ProjectMember> response) {
                if (response.body() != null) {
                    if (response.isSuccessful() && response.body().isStatus()) {
                        List<ProjectMemberData> memberIdList = response.body().getData();
                        for (int i = 0; i < memberIdList.size(); i++){
                            memberId.add(memberIdList.get(i).getUserId());
                        }
                    } else {
                        widget.errorToast("Project Member "+response.body().getMessage(), requireActivity());
                    }
                }
            }

            @Override
            public void onFailure(Call<ProjectMember> call, Throwable t) {
                widget.noConnectToast("Project Member "+t.getMessage(), requireActivity());
            }
        });
    }

    private void retrieveDataMember() {
        Call<Employee> empData = apiInterface.employeeRetrieveData();
        empData.enqueue(new Callback<Employee>() {
            @Override
            public void onResponse(@NotNull Call<Employee> call, @NotNull Response<Employee> response) {
                if (response.body() != null) {
                    if (response.isSuccessful() && response.body().isStatus()) {
                        employeeDataList = response.body().getData();
                        List<String> list = new ArrayList<>();
                        MultiAutoCompleteTextView.CommaTokenizer tokenizer=new MultiAutoCompleteTextView.CommaTokenizer();

                        for (int i = 0; i < employeeDataList.size(); i++){
                            if (!memberId.isEmpty()){
                                for (int m = 0; m < memberId.size(); m++){
                                    if (memberId.get(m) == employeeDataList.get(i).getId()){
                                        macMember.setText(macMember.getText().toString() + employeeDataList.get(i).getName()+ ", ");
                                        tokenizer.terminateToken(employeeDataList.get(i).getName());
                                    }
                                }
                            }
                            list.add(widget.capitalizeText(employeeDataList.get(i).getName()));
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), R.layout.support_simple_spinner_dropdown_item, list);
                        macMember.setAdapter(adapter);
                        macMember.setTokenizer(tokenizer);
                        macMember.setOnItemClickListener((parent, view, position, id) -> {
//                            String[] members = macMember.getText().toString().trim().split("\\s*,\\s*");
//                            if (members[position].equalsIgnoreCase(employeeDataList.get(position).getName())){
//                                list.remove(members[position]);
//                            }
                            memberId.add(employeeDataList.get(position).getId());
                        });
                        adapter.notifyDataSetChanged();
                    } else {
                        widget.errorToast("Employee "+response.body().getMessage(), requireActivity());
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<Employee> call, @NotNull Throwable t) {
                widget.noConnectToast("Employee "+t.getMessage(), requireActivity());
            }
        });
    }

    private void retrieveDataStatus() {
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(requireActivity(), R.layout.support_simple_spinner_dropdown_item, statusList);
        acStatus.setAdapter(statusAdapter);
    }

    private void showDialog() {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_category, null);
        EditText etNameCat = dialogView.findViewById(R.id.etNameCategory);
        listView = dialogView.findViewById(R.id.listCategory);

        retrieveDataCategoryDialog();

        MaterialAlertDialogBuilder mDialog = new MaterialAlertDialogBuilder(requireActivity());
        mDialog.setView(dialogView)
                .setTitle(R.string.add_project_category)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    String nameCat = etNameCat.getText().toString();
                    createCategory(nameCat);
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .show();
    }

    public void retrieveDataCategoryDialog() {
        Call<ProjectCategory> categoryData = apiInterface.projectCategoryRetrieveData();
        categoryData.enqueue(new Callback<ProjectCategory>() {
            @Override
            public void onResponse(Call<ProjectCategory> call, Response<ProjectCategory> response) {
                if (response.body() != null) {
                    if (response.isSuccessful() && response.body().isStatus()) {
                        AdapterProjectCategory adapterCat = new AdapterProjectCategory(requireActivity(), R.layout.list_category, projectCategoryDataList, ProjectEditorFragment.this);
                        listView.setAdapter(adapterCat);
                    } else {
                        widget.errorToast(response.body().getMessage(), requireActivity());
                    }
                }
            }

            @Override
            public void onFailure(Call<ProjectCategory> call, Throwable t) {
                widget.noConnectToast(t.getMessage(), requireActivity());
            }
        });
    }

    private void createCategory(String nameCat) {
        Call<ProjectCategory> createCategory = apiInterface.projectCategoryCreateData(nameCat);
        createCategory.enqueue(new Callback<ProjectCategory>() {
            @Override
            public void onResponse(Call<ProjectCategory> call, Response<ProjectCategory> response) {
                if (response.body() != null) {
                    if (response.isSuccessful() && response.body().isStatus()) {
                        widget.successToast(response.body().getMessage(), requireActivity());
                        retrieveDataCategory();
                    } else {
                        widget.errorToast(response.body().getMessage(), requireActivity());
                    }
                }
            }

            @Override
            public void onFailure(Call<ProjectCategory> call, Throwable t) {
                widget.noConnectToast(t.getMessage(), requireActivity());
            }
        });
    }

    public void retrieveDataCategory(){
        Call<ProjectCategory> categoryData = apiInterface.projectCategoryRetrieveData();
        categoryData.enqueue(new Callback<ProjectCategory>() {
            @Override
            public void onResponse(Call<ProjectCategory> call, Response<ProjectCategory> response) {
                if (response.body() != null) {
                    if (response.isSuccessful() && response.body().isStatus()) {
                        projectCategoryDataList = response.body().getData();
                        List<String> list = new ArrayList<>();
                        for (int i = 0; i < projectCategoryDataList.size(); i++){
                            if (categoryId == projectCategoryDataList.get(i).getId()){
                                acCategory.setText(widget.capitalizeText(projectCategoryDataList.get(i).getCategoryName()));
                            }
                            list.add(widget.capitalizeText(projectCategoryDataList.get(i).getCategoryName()));
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), R.layout.support_simple_spinner_dropdown_item, list);
                        acCategory.setAdapter(adapter);
                        acCategory.setOnItemClickListener((parent, view, position, id) -> categoryId = projectCategoryDataList.get(position).getId());
                        adapter.notifyDataSetChanged();
                    } else {
                        widget.errorToast("Category "+response.body().getMessage(), requireActivity());
                    }
                }
            }

            @Override
            public void onFailure(Call<ProjectCategory> call, Throwable t) {
                widget.noConnectToast("Category "+t.getMessage(), requireActivity());
            }
        });
    }

    private void findView() {
        etName = view.findViewById(R.id.etNameProject);
        acCategory = view.findViewById(R.id.tvCategoryProject);
        etStartDate = view.findViewById(R.id.etStartDateProject);
        etDeadline = view.findViewById(R.id.etDeadlineProject);
        macMember = view.findViewById(R.id.tvMemberProject);
        acStatus = view.findViewById(R.id.tvStatusProject);
        etSummary = view.findViewById(R.id.etProjectSummary);
        etNote = view.findViewById(R.id.etNoteProject);
        tilStatus = view.findViewById(R.id.tfStatusProject);
        btnAddCategory = view.findViewById(R.id.btnAddCategoryProject);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_update, menu);
        actionMenu = menu;

        if (projectId != 0){
            setMenu(false, false, false, isEditable, false);
        } else {
            setMenu(false, false, true, false, false);
        }
    }

    private void setMenu(boolean bEdit, boolean bDelete, boolean bSave, boolean bUpdate, boolean bCancel) {
        actionMenu.findItem(R.id.menu_edit).setVisible(bEdit);
        actionMenu.findItem(R.id.menu_delete).setVisible(bDelete);
        actionMenu.findItem(R.id.menu_save).setVisible(bSave);
        actionMenu.findItem(R.id.menu_update).setVisible(bUpdate);
        actionMenu.findItem(R.id.menu_cancel).setVisible(bCancel);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        projectName = etName.getText().toString();
        int userId = Integer.parseInt(sessionManager.getUserDetail().get(SessionManager.USER_ID));
        summary = etSummary.getText().toString();
        note = etNote.getText().toString();
        status = acStatus.getText().toString();

        int itemId = item.getItemId();
        if (itemId == R.id.menu_update) {
            updateData();
            return true;
        } else if (itemId == R.id.menu_save) {
            createData(userId);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateData() {
        Call<Project> projectUpdateData = apiInterface.projectUpdateData(projectId, projectName,
                categoryId, startDate, deadline, memberId, summary, note, status);
        projectUpdateData.enqueue(new Callback<Project>() {
            @Override
            public void onResponse(Call<Project> call, Response<Project> response) {
                if (response.body() != null) {
                    if (response.isSuccessful() && response.body().isStatus()) {
                        widget.successToast(response.body().getMessage(), requireActivity());
                        fm.popBackStack();
                    } else {
                        widget.errorToast(response.body().getMessage(), requireActivity());
                    }
                }
            }

            @Override
            public void onFailure(Call<Project> call, Throwable t) {
                widget.noConnectToast(t.getMessage(), requireActivity());
            }
        });
    }

    private void createData(int userId) {
        Call<Project> projectCreateData = apiInterface.projectCreateData(userId, projectName, 
                categoryId, startDate, deadline, memberId, summary, note);
        projectCreateData.enqueue(new Callback<Project>() {
            @Override
            public void onResponse(Call<Project> call, Response<Project> response) {
                if (response.body() != null) {
                    if (response.isSuccessful() && response.body().isStatus()) {
                        widget.successToast(response.body().getMessage(), requireActivity());
                        fm.popBackStack();
                    } else {
                        widget.errorToast(response.body().getMessage(), requireActivity());
                    }
                }
            }

            @Override
            public void onFailure(Call<Project> call, Throwable t) {
                widget.noConnectToast(t.getMessage(), requireActivity());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}