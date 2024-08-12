package com.atsuite.subletapp;

public class SubletModel {

    public String title, type, desc, startDate, endDate, price, address, selectedLocation;

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
}
