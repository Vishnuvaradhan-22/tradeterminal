package com.tradeterminal.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Vish on 23/04/2017.
 */
public class Service implements Serializable {
    private long id;
    private String serviceName;
    private String categoryName;
    private long categoryId;
    private String color;
    private int duration;
    private List<Staff> staffs;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<Staff> getStaffs() {
        return staffs;
    }

    public void setStaffs(List<Staff> staffs) {
        this.staffs = staffs;
    }

    public boolean addStaff(Staff staff){
        if(!this.staffs.contains(staff)){
            this.staffs.add(staff);
            return true;
        }
        else
            return false;
    }
}
