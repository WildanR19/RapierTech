package com.example.rapiertech.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.rapiertech.R;
import com.example.rapiertech.model.event.EventData;
import com.example.rapiertech.ui.holiday.EventFragment;
import com.example.rapiertech.widget.Widget;

import java.util.List;

public class AdapterEvent extends ArrayAdapter<EventData> {
    private final Context context;
    private final List<EventData> eventDataList;
    private final EventFragment eventFragment;
    private Widget widget;

    public AdapterEvent(@NonNull Context context, int resource, List<EventData> eventDataList, EventFragment basicSalaryFragment) {
        super(context, resource, eventDataList);
        this.context = context;
        this.eventDataList = eventDataList;
        this.eventFragment = basicSalaryFragment;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView==null){
            LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_event,null,true);
        }

        widget = new Widget();
        EventData eventData = eventDataList.get(position);
        TextView tvEvent = convertView.findViewById(R.id.event_holiday);
        TextView tvDateEvent = convertView.findViewById(R.id.date_event_holiday);
        ImageView iconEvent = convertView.findViewById(R.id.icon_list_holiday);

        tvEvent.setText(widget.capitalizeText(eventData.getTitle()));
        tvDateEvent.setText(widget.changeDateFormat(eventData.getStart()));

        return convertView;
    }
}
