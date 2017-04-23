package com.tradeterminal.ui;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.alamkanak.weekview.MonthLoader.MonthChangeListener;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.tradeterminal.R;
import com.tradeterminal.utility.DataLoader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeScreenActivity extends AppCompatActivity implements DataLoader{

    private WeekView weekView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        initializeUi();
    }

    private void initializeUi(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.action_menu_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        weekView = (WeekView)findViewById(R.id.cal_week_view);
        Calendar date = Calendar.getInstance();
        Log.d("Date",date.getTime().toString());

        weekView.setMonthChangeListener(monthChangeListener);
        weekView.setEmptyViewClickListener(emptyViewListener);
    }

    private MonthChangeListener monthChangeListener = new MonthChangeListener() {
        @Override
        public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
            List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
            WeekViewEvent event = new WeekViewEvent();
            Calendar startTime = Calendar.getInstance();
            startTime.set(Calendar.HOUR_OF_DAY,9);
            startTime.set(Calendar.MINUTE,30);
            startTime.set(Calendar.SECOND,0);
            Calendar endTime = Calendar.getInstance();
            endTime.set(Calendar.HOUR_OF_DAY,10);
            endTime.set(Calendar.MINUTE,30);
            endTime.set(Calendar.SECOND,0);
            event.setStartTime(startTime);
            event.setEndTime(endTime);
            event.setName("Test Event");
            event.setColor(Color.RED);
            events.add(event);
            return events;
        }
    };

    private WeekView.EmptyViewClickListener emptyViewListener = new WeekView.EmptyViewClickListener() {
        @Override
        public void onEmptyViewClicked(Calendar time) {
            Log.d("Time Clicked",time.getTime().toString());
        }
    };
    @Override
    public void loadData(String jsonData) {

    }
}
