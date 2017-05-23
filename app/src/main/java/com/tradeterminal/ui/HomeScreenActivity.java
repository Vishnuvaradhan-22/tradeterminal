package com.tradeterminal.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.RectF;

import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;


import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.alamkanak.weekview.WeekViewLoader;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.telerik.widget.calendar.CalendarDisplayMode;
import com.telerik.widget.calendar.CalendarElement;
import com.telerik.widget.calendar.RadCalendarView;
import com.telerik.widget.calendar.events.Event;
import com.tradeterminal.R;
import com.tradeterminal.model.Booking;
import com.tradeterminal.model.Business;
import com.tradeterminal.model.BusinessLocation;
import com.tradeterminal.model.Category;
import com.tradeterminal.model.Service;
import com.tradeterminal.model.Staff;
import com.tradeterminal.model.User;


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
import java.util.Map;

public class HomeScreenActivity extends MenuLoader {

    private User user;
    private String getAllBookingUrl = "http://api.tradeterminal.com.au/api/Booking/GetAll/";
    private WeekView weekView;
    private  HashMap<String,List<Booking>> bookingMap;
    private boolean loadedWebServiceData = false;
    private SharedPreferences msharedPreferences;
    private Spinner locationSpinner;
    private Spinner staffSpinner;
    private Spinner categorySpinner;
    private Spinner serviceSpinner;

    private List<BusinessLocation> businessLocations;
    private List<Category> availableCategories;
    private List<Service> availableServices;
    private List<Staff> availableStaffs;
    private List<String> locationList;
    private List<String> staffList;
    private List<String> categoryList;
    private List<String> serviceList;
    private String baseUrl = "http://api.tradeterminal.com.au/api/";

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

        msharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean isLoggedIn = loadUserData();

        if(isLoggedIn){
            bookingMap = new HashMap<String,List<Booking>>();

            weekView = (WeekView)findViewById(R.id.cal_week_view);
            weekView.setMonthChangeListener(monthChangeListener);
            weekView.setEmptyViewClickListener(emptyViewListener);
            weekView.setOverlappingEventGap(5);
            weekView.setOnEventClickListener(eventClickListener);

            locationList = new ArrayList<>();
            staffList = new ArrayList<>();
            businessLocations = new ArrayList<>();
            availableServices = new ArrayList<>();
            availableCategories = new ArrayList<>();
            availableStaffs = new ArrayList<>();
            categoryList = new ArrayList<>();
            serviceList = new ArrayList<>();

            locationSpinner = (Spinner)findViewById(R.id.sp_location);
            staffSpinner = (Spinner)findViewById(R.id.sp_staff);
            categorySpinner = (Spinner)findViewById(R.id.sp_category);
            serviceSpinner = (Spinner)findViewById(R.id.sp_service);

        }
        else{
            Intent intent = new Intent();
            intent.setClass(this,LoginActivity.class);
            startActivity(intent);
        }

    }

    private boolean loadUserData(){
        Gson gson = new Gson();
        if(msharedPreferences.contains("User")){
            String userString = msharedPreferences.getString("User","");
            user = (User)gson.fromJson(userString,User.class);
            return true;
        }
        return false;

    }

    private void getLocations(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String locationUrl = baseUrl + "Location/GetAll/"+user.getToken();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, locationUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray locations = response.getJSONArray("ReturnModel");
                    for(int i=0; i<locations.length();i++){
                        JSONObject locationJson = locations.getJSONObject(i);
                        BusinessLocation location = new BusinessLocation();
                        location.setId(Long.parseLong(locationJson.getString("Id")));
                        location.setBusinessId(Long.parseLong(locationJson.getString("BusinessId")));
                        location.setBusinessName(locationJson.getString("BusinessName"));
                        location.setBusinessLocationName(locationJson.getString("BusinessLocationName"));
                        location.setMainLocation(Boolean.parseBoolean(locationJson.getString("IsMainLocation")));
                        location.setAddressLine1(locationJson.getString("Address1"));
                        location.setAddressLine2(locationJson.getString("Address2"));
                        location.setSuburb(locationJson.getString("Suburb"));
                        location.setPostCode(locationJson.getString("Postcode"));
                        location.setPhoneNumber(locationJson.getString("PhoneNumber"));
                        if(locationJson.getString("StateName") != null)
                            location.setStateName(locationJson.getString("StateName"));
                        Map<String,ArrayList<Calendar>> businessHours = new HashMap<>();
                        JSONArray hoursArray = locationJson.getJSONArray("ListBusinessHours");
                        for(int j=0; j<hoursArray.length();j++){
                            JSONObject hoursObject = hoursArray.getJSONObject(j);
                            String startTime = hoursObject.getString("StartTime");
                            String eendTime = hoursObject.getString("EndTime");
                            String pattern = "yyyy-MM-dd'T'HH:mm:ss";
                            Date start = new SimpleDateFormat(pattern).parse(startTime);
                            Date end = new SimpleDateFormat(pattern).parse(eendTime);
                            Calendar calendar = Calendar.getInstance();
                            Calendar calendar1 = (Calendar)calendar.clone();
                            calendar.setTime(start);
                            calendar1.setTime(end);
                            ArrayList<Calendar> hours = new ArrayList<>();
                            hours.add(calendar);
                            hours.add(calendar1);
                            businessHours.put(hoursObject.getString("DayOfWeek"),hours);
                        }
                        location.setBusinessHours(businessHours);
                        locationList.add(location.getBusinessName());
                        businessLocations.add(location);
                        
                    }
                    ArrayAdapter<String> locationAdapter = new ArrayAdapter<String>(HomeScreenActivity.this,android.R.layout.simple_spinner_item,locationList);
                    locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    locationSpinner.setAdapter(locationAdapter);
                    locationSpinner.setOnItemSelectedListener(locationSelectedListener);

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                switch(networkResponse.statusCode){
                    case 403:
                        Toast.makeText(HomeScreenActivity.this,"Sorry, Something went wrong. Logout and try again",Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private void getCategory(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String categoryUrl = baseUrl + "Category/GetAll/"+user.getToken();
        JsonObjectRequest categoryRequest = new JsonObjectRequest(Request.Method.GET, categoryUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getString("IsSuccess").equals("true")){
                        JSONArray categories = (JSONArray)response.get("ReturnModel");
                        for(int i =0; i<categories.length(); i++){
                            JSONObject categoryJson = categories.getJSONObject(i);
                            Category category = new Category();
                            category.setId(Long.parseLong(categoryJson.getString("Id")));
                            category.setBusinessId(Long.parseLong(categoryJson.getString("BusinessId")));
                            category.setCategoryName(categoryJson.getString("ServiceCategoryName"));
                            category.setDescription(categoryJson.getString("Description"));
                            categoryList.add(category.getCategoryName());
                            availableCategories.add(category);
                        }

                        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(HomeScreenActivity.this,android.R.layout.simple_spinner_item,categoryList);
                        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        categorySpinner.setAdapter(categoryAdapter);
                        categorySpinner.setOnItemSelectedListener(categoryListener);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                switch(networkResponse.statusCode){
                    case 403:
                        Toast.makeText(HomeScreenActivity.this,"Sorry, Something went wrong. Logout and try again",Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });

        requestQueue.add(categoryRequest);
    }
    private AdapterView.OnItemSelectedListener locationSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };
    private AdapterView.OnItemSelectedListener categoryListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            String categoryName = (String)adapterView.getItemAtPosition(i);
            Category categorySelected = findCategoryByName(categoryName);
            getServices(categorySelected);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    private void getServices(Category categorySelected){
        RequestQueue requestQueue = Volley.newRequestQueue(HomeScreenActivity.this);
        String serviceUrl = baseUrl + "Service/GetAllByCategory/"+user.getToken()+"/"+categorySelected.getId();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, serviceUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getString("IsSuccess").equals("true")){
                        if(!serviceList.isEmpty())
                            serviceList.clear();
                        JSONArray servicesArray = (JSONArray)response.get("ReturnModel");
                        for(int i=0;i<servicesArray.length();i++){
                            JSONObject serviceJson = servicesArray.getJSONObject(i);
                            Service service = new Service();
                            service.setId(Long.parseLong(serviceJson.getString("Id")));
                            service.setCategoryId(Long.parseLong(serviceJson.getString("ServiceCategoryId")));
                            service.setServiceName(serviceJson.getString("ServiceName"));
                            service.setColor(serviceJson.getString("Color"));
                            service.setDescription(serviceJson.getString("Description"));
                            if(serviceJson.getString("Deposit")!=null)
                                service.setDeposit(serviceJson.getString("Deposit"));
                            service.setDuration(serviceJson.getString("DurationName"));
                            service.setPaymentType(serviceJson.getString("PaymentTypeName"));
                            service.setPaymentTypeId(Long.parseLong(serviceJson.getString("PaymentTypeId")));
                            service.setPrice(serviceJson.getDouble("Price")+"");
                            serviceList.add(service.getServiceName());
                            if(!availableServices.contains(service))
                                availableServices.add(service);
                        }
                        ArrayAdapter<String> serviceAdapter = new ArrayAdapter<String>(HomeScreenActivity.this,android.R.layout.simple_spinner_item,serviceList);
                        serviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        serviceSpinner.setAdapter(serviceAdapter);
                        serviceSpinner.setOnItemSelectedListener(serviceListener);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                switch(networkResponse.statusCode){
                    case 403:
                        Toast.makeText(HomeScreenActivity.this,"Sorry, Something went wrong. Logout and try again",Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
    private AdapterView.OnItemSelectedListener serviceListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            String serviceName = (String)adapterView.getItemAtPosition(i);
            Service serviceSelected = findServiceByName(serviceName);
            getStaffs(serviceSelected);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    private AdapterView.OnItemSelectedListener staffListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            String staffName = (String)adapterView.getItemAtPosition(i);
            Staff staff = findStaffByName(staffName);
            weekView.notifyDatasetChanged();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    private Category findCategoryByName(String name){
        Iterator iterator = availableCategories.iterator();
        while(iterator.hasNext()){
            Category category = (Category) iterator.next();
            if(category.getCategoryName().equals(name))
                return category;
        }
        return null;
    }

    private Service findServiceByName(String name){
        Iterator iterator = availableServices.iterator();
        while (iterator.hasNext()){
            Service service = (Service)iterator.next();
            if(service.getServiceName().equals(name))
                return service;
        }
        return null;
    }

    private Staff findStaffByName(String name){
        Iterator iterator = availableStaffs.iterator();
        while(iterator.hasNext()){
            Staff staff = (Staff)iterator.next();
            if(staff.getStaffName().equals(name))
                return staff;
        }
        return null;
    }
    private void getStaffs(Service service){
        RequestQueue requestQueue = Volley.newRequestQueue(HomeScreenActivity.this);
        String staffUrl = baseUrl + "Staff/GetAllByService/"+user.getToken()+"/"+service.getId();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, staffUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("IsSuccess").equals("true")) {
                        if(!staffList.isEmpty())
                            staffList.clear();
                        JSONArray staffArray = (JSONArray)response.get("ReturnModel");
                        for(int i=0; i<staffArray.length();i++){
                            JSONObject staffJson = staffArray.getJSONObject(i);
                            Staff staff = new Staff();
                            staff.setStaffId(Long.parseLong(staffJson.getString("Id")));
                            staff.setStaffName(staffJson.getString("Fullname"));
                            staff.setColor(staffJson.getString("Color"));
                            staff.setJobTitle(staffJson.getString("JobTitle"));
                            staffList.add(staff.getStaffName());
                            if(!availableStaffs.contains(staff))
                                availableStaffs.add(staff);
                        }
                        ArrayAdapter<String> staffAdapter = new ArrayAdapter<String>(HomeScreenActivity.this,android.R.layout.simple_spinner_item,staffList);
                        staffAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        staffSpinner.setAdapter(staffAdapter);
                        staffSpinner.setOnItemSelectedListener(staffListener);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(jsonObjectRequest);
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
                        getLocations();
                        getCategory();
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
            Log.d("BookingKey",bookingKey);
            if(bookingMap.containsKey(bookingKey)){
                String staff = (String)staffSpinner.getSelectedItem();
                Log.d("Staff",staff+" test");
                List<Booking> bookingForStaff = new ArrayList<>();
                if(staff!=null){
                    Staff staffSelected = findStaffByName(staff);
                    Log.d("StaffId",staffSelected.getStaffId()+"");
                    Log.d("List",bookingMap.get(bookingKey).size()+"");
                    List<Booking> bookingTempList = bookingMap.get(bookingKey);
                    Log.d("Bookings",bookingTempList.size()+"");
                    for(Booking booking : bookingTempList){
                        Log.d("Booking Staff",booking.getAssignedStaff().getStaffId()+" and "+staffSelected.getStaffId());
                        if(booking.getAssignedStaff().getStaffId() == staffSelected.getStaffId())
                            bookingForStaff.add(booking);
                            Log.d("Booking for Staff",bookingForStaff.size()+"");
                    }

                }
                return bookingForStaff;

            }

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
                    Staff staff = new Staff();
                    staff.setStaffId(Long.parseLong(bookingObject.getString("StaffId")));
                    booking.setAssignedStaff(staff);
                    String bookingKey = (booking.getStartTime().get(Calendar.MONTH) + 1 )+"-" +calendar.get(Calendar.YEAR);
                    Log.d("Key",bookingKey);
                    if(bookingMap.containsKey(bookingKey)){
                        bookingMap.get(bookingKey).add(booking);
                        Log.d("Event Key,Booking date",bookingKey +"\t"+booking.getStartTime().getTime());
                    }
                    else{
                        List<Booking> eventList = new ArrayList<>();
                        eventList.add(booking);
                        Log.d("Event Key,Booking date",bookingKey +"\t"+booking.getStartTime().getTime());
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