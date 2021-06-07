package com.example.rapiertech.ui.holiday;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;

import com.example.rapiertech.R;
import com.example.rapiertech.activity.SessionManager;
import com.example.rapiertech.adapter.AdapterEvent;
import com.example.rapiertech.api.ApiClient;
import com.example.rapiertech.api.ApiInterface;
import com.example.rapiertech.model.event.Event;
import com.example.rapiertech.model.event.EventData;
import com.example.rapiertech.widget.Widget;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventFragment extends Fragment {

    private ApiInterface apiInterface;
    private String start, end, title;
    private Widget widget;
    private Calendar calendar;
    private ProgressBar loading;
    private List<EventData> eventDataList = new ArrayList<>();
    private ListView listView;
    private int monthCalendar;
    private MaterialCalendarView materialCalendarView;
    private SimpleDateFormat sdf;
    private AdapterEvent adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        widget = new Widget();
        calendar = Calendar.getInstance();
        sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        materialCalendarView = view.findViewById(R.id.calendarViewHoliday);
        loading = view.findViewById(R.id.loadingHoliday);
        listView = view.findViewById(R.id.listHoliday);

        calendarListener();

        SessionManager sessionManager = new SessionManager(getContext());
        if (sessionManager.getRoleId().equals("1")){
            createEvent();
            deleteEvent();
        }

        return view;
    }

    private void calendarListener() {
        materialCalendarView.setDateSelected(CalendarDay.today(), true);
        materialCalendarView.setOnMonthChangedListener((widget, date) -> {
            monthCalendar = date.getMonth();
            showCalendarData(monthCalendar);
        });
    }

    private void createEvent(){
        materialCalendarView.setOnDateChangedListener((widget, date, selected) -> {
            start = date.getYear() + "-" + date.getMonth() + "-" + date.getDay();
            monthCalendar = date.getMonth();
            sdf.setTimeZone(TimeZone.getTimeZone("UTC+7"));
            try {
                calendar.setTime(Objects.requireNonNull(sdf.parse(start)));
                calendar.add(Calendar.DATE, 1);
                end = sdf.format(calendar.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dialogAdd();
        });
    }

    private void dialogAdd() {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_deptjob, null);
        EditText etEvent = view.findViewById(R.id.add_deptJobName);
        TextInputLayout tilTitle = view.findViewById(R.id.name_text_field);
        tilTitle.setHint("Event Title");

        MaterialAlertDialogBuilder mDialog = new MaterialAlertDialogBuilder(requireActivity());
        mDialog.setView(view)
                .setTitle(R.string.add_event)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    title = etEvent.getText().toString();
                    createData();
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void createData() {
        Call<Event> createData = apiInterface.eventCreateData(start, end, title);
        createData.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NotNull Call<Event> call, @NotNull Response<Event> response) {
                if (response.body() != null){
                    if (response.isSuccessful() && response.body().isStatus()){
                        widget.successToast(response.body().getMessage(), requireActivity());
                        showCalendarData(monthCalendar);
                    } else {
                        widget.errorToast(response.body().getMessage(), requireActivity());
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<Event> call, @NotNull Throwable t) {
                widget.noConnectToast(t.getMessage(), requireActivity());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        DateFormat dateFormat = new SimpleDateFormat("MM", Locale.ENGLISH);
        Date today = new Date();
        monthCalendar = Integer.parseInt(dateFormat.format(today));

        showCalendarData(monthCalendar);
    }

    public void showCalendarData(int month) {
        loading.setVisibility(View.VISIBLE);

        Call<Event> showData = apiInterface.eventList(month);
        showData.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NotNull Call<Event> call, @NotNull Response<Event> response) {
                if (response.body() != null) {
                    if (response.isSuccessful() && response.body().isStatus()) {
                        eventDataList = response.body().getData();
                        adapter = new AdapterEvent(requireActivity(), R.layout.list_basic_salaries, eventDataList, EventFragment.this);
                        listView.setAdapter(adapter);
                    } else {
                        widget.errorToast(response.body().getMessage(), requireActivity());
                    }
                }
                loading.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(@NotNull Call<Event> call, @NotNull Throwable t) {
                widget.noConnectToast(t.getMessage(), requireActivity());
                loading.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void deleteEvent(){
        listView.setOnItemClickListener((parent, view, position, id) -> {
            BottomSheetMaterialDialog mDialog = new BottomSheetMaterialDialog.Builder(requireActivity())
                    .setTitle("Delete?")
                    .setMessage("Are you sure want to delete this data?")
                    .setCancelable(false)
                    .setPositiveButton("Delete", R.drawable.ic_delete_, (dialogInterface, which) -> {
                        Call<Event> deleteEvent = apiInterface.eventDeleteData(adapter.getItem(position).getId());
                        deleteEvent.enqueue(new Callback<Event>() {
                            @Override
                            public void onResponse(@NotNull Call<Event> call, @NotNull Response<Event> response) {
                                if (response.body() != null) {
                                    if (response.isSuccessful() && response.body().isStatus()) {
                                        widget.successToast(response.body().getMessage(), requireActivity());
                                        showCalendarData(monthCalendar);
                                    } else {
                                        widget.errorToast(response.body().getMessage(), requireActivity());
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NotNull Call<Event> call, @NotNull Throwable t) {
                                widget.noConnectToast(t.getMessage(), requireActivity());
                            }
                        });
                        dialogInterface.dismiss();
                    })
                    .setNegativeButton("Cancel", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss())
                    .build();
            mDialog.show();
        });
    }
}