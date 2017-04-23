package com.tradeterminal.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Vish on 23/04/2017.
 */
public class Staff implements Serializable {
    private long staffId;
    private String staffName;
    private String appointmentColor;
    private long serviceId;
    private String dayOff;
    private List<Calendar> leaveDate;

    public long getStaffId() {
        return staffId;
    }

    public void setStaffId(long staffId) {
        this.staffId = staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getAppointmentColor() {
        return appointmentColor;
    }

    public void setAppointmentColor(String appointmentColor) {
        this.appointmentColor = appointmentColor;
    }

    public long getServiceId() {
        return serviceId;
    }

    public void setServiceId(long serviceId) {
        this.serviceId = serviceId;
    }

    public String getDayOff() {
        return dayOff;
    }

    public void setDayOff(String dayOff) {
        this.dayOff = dayOff;
    }

    public List<Calendar> getLeaveDate() {
        return leaveDate;
    }

    public void setLeaveDate(List<Calendar> leaveDate) {
        this.leaveDate = leaveDate;
    }
}
