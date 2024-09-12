package com.atsuite.subletapp;

public class SubletModel {

    public String title, type, desc, startDate, endDate, price, address, selectedLocation, subletUid, imageUrl;

    public SubletModel() {
    }

    public SubletModel(String title, String type, String desc, String startDate, String endDate, String price, String address, String selectedLocation) {
        this.title = title;
        this.type = type;
        this.desc = desc;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
        this.address = address;
        this.selectedLocation = selectedLocation;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSelectedLocation() {
        return selectedLocation;
    }

    public void setSelectedLocation(String selectedLocation) {
        this.selectedLocation = selectedLocation;
    }

    public String getSubletUid() {
        return subletUid;
    }

    public void setSubletUid(String subletUid) {
        this.subletUid = subletUid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
