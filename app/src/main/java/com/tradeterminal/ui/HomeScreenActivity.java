package com.tradeterminal.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;


import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.alamkanak.weekview.WeekViewLoader;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.telerik.widget.calendar.CalendarDisplayMode;
import com.telerik.widget.calendar.RadCalendarView;
import com.telerik.widget.calendar.events.Event;
import com.tradeterminal.R;
import com.tradeterminal.model.Booking;
import com.tradeterminal.model.User;
import com.tradeterminal.utility.DataLoader;
import com.tradeterminal.utility.WebServiceGateway;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class HomeScreenActivity extends MenuLoader {

    private User user;
    private String getAllBookingUrl = "http://api.tradeterminal.com.au/api/Booking/GetAll/";
    private WeekView weekView;
    private HashMap<String,List<Booking>> bookingMap;
    private boolean loadedWebServiceData = false;
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


        Bundle savedInstance = getIntent().getBundleExtra("UserBundle");
        user = (User)savedInstance.getSerializable("User");
        bookingMap = new HashMap<String,List<Booking>>();

        weekView = (WeekView)findViewById(R.id.cal_week_view);
        weekView.setMonthChangeListener(monthChangeListener);
        weekView.setEmptyViewClickListener(emptyViewListener);
        weekView.setOverlappingEventGap(5);
        weekView.setOnEventClickListener(eventClickListener);

        String[] requestBooking = new String[3];
        requestBooking[0] = "get";
        requestBooking[1] = getAllBookingUrl+user.getToken();


    }

    private WeekView.EventClickListener eventClickListener = new WeekView.EventClickListener() {
        @Override
        public void onEventClick(WeekViewEvent event, RectF eventRect) {
            Log.d("Event",event.getId()+"");
            Booking eventClicked = findBookingById(event.getId(),event.getStartTime());
            Bundle bookingBundle = new Bundle();
            bookingBundle.putSerializable("Booking",eventClicked);
            bookingBundle.putSerializable("User",user);
            Intent intent = new Intent();
            intent.putExtra("BookingBundle",bookingBundle);
            //intent.setClass(HomeScreenActivity.this,BookingDetails.class);
            //startActivity(intent);
        }
    };
    private MonthLoader.MonthChangeListener monthChangeListener = new MonthLoader.MonthChangeListener() {
        @Override
        public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
            List<Booking> bookingEmptyList = new ArrayList<>();
            if(!loadedWebServiceData){
                RequestQueue queue = Volley.newRequestQueue(HomeScreenActivity.this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,getAllBookingUrl+user.getToken(),null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadData(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
                queue.add(jsonObjectRequest);
                loadedWebServiceData = true;
            }

            String bookingKey = newMonth+"-"+newYear;
            if(bookingMap.containsKey(bookingKey))
                return bookingMap.get(bookingKey);
            else
                return bookingEmptyList;
        }
    };

    private WeekView.EmptyViewClickListener emptyViewListener = new WeekView.EmptyViewClickListener() {
        @Override
        public void onEmptyViewClicked(Calendar time) {
            Log.d("Time Clicked",time.getTime().toString());
        }
    };


    private Booking findBookingById(long id,Calendar start){
        String bookingKey = start.get(Calendar.MONTH)+"-"+start.get(Calendar.YEAR);
        List<Booking> bookings = bookingMap.get(bookingKey);
        for(Booking booking : bookings ){
            if(booking.getId() == id)
                return booking;
        }
        return null;
    }
    public void loadData(JSONObject responseJson) {
        try {
            boolean availability = Boolean.parseBoolean(responseJson.getString("IsSuccess"));
            List<Event> events = new ArrayList<Event>();
            if(availability) {
                JSONArray bookingArray = responseJson.getJSONArray("ReturnModel");
                for( int i =0; i<bookingArray.length();i++){
                    JSONObject bookingObject = bookingArray.getJSONObject(i);
                    Booking booking = new Booking();
                    booking.setName(bookingObject.getString("ServiceName"));
                    booking.setColor(Color.parseColor(bookingObject.getString("ServiceColor")));
                    String startTime = bookingObject.getString("StartDate");
                    String eendTime = bookingObject.getString("EndDate");
                    String pattern = "yyyy-MM-dd'T'HH:mm:ss";
                    Date start = new SimpleDateFormat(pattern).parse(startTime);
                    Date end = new SimpleDateFormat(pattern).parse(eendTime);
                    Calendar calendar = Calendar.getInstance();
                    Calendar calendar1 = (Calendar)calendar.clone();
                    calendar.setTime(start);
                    calendar1.setTime(end);
                    Calendar eventStartTime = Calendar.getInstance();
                    eventStartTime.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
                    eventStartTime.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
                    eventStartTime.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH));
                    eventStartTime.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
                    eventStartTime.set(Calendar.YEAR,calendar.get(Calendar.YEAR) );
                    Calendar endTime = Calendar.getInstance();
                    endTime.set(Calendar.HOUR_OF_DAY, calendar1.get(Calendar.HOUR_OF_DAY));
                    endTime.set(Calendar.MINUTE, calendar1.get(Calendar.MINUTE));
                    endTime.set(Calendar.DAY_OF_MONTH,calendar1.get(Calendar.DAY_OF_MONTH));
                    endTime.set(Calendar.MONTH, calendar1.get(Calendar.MONTH));
                    endTime.set(Calendar.YEAR,calendar1.get(Calendar.YEAR));
                    booking.setStartTime(eventStartTime);

                    booking.setEndTime(endTime);
                    booking.setUniqueId(bookingObject.getString("UniqueId"));
                    booking.setId(Long.parseLong(bookingObject.getString("Id")));
                    String bookingKey = calendar.get(Calendar.MONTH) +"-" +calendar.get(Calendar.YEAR);

                    if(bookingMap.containsKey(bookingKey)){
                        bookingMap.get(bookingKey).add(booking);
                    }
                    else{
                        List<Booking> eventList = new ArrayList<>();
                        eventList.add(booking);
                        bookingMap.put(bookingKey,eventList);
                    }

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        weekView.notifyDatasetChanged();

    }

    private void loadBooking(){

    }
}
