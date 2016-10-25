package com.example.badhri.nytimes.parcels;

import org.parceler.Parcel;

/**
 * Created by badhri on 10/24/16.
 */

@Parcel
public class DateObj {
    // fields must be public
    int year;

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    int month;
    int day;

    // empty constructor needed by the Parceler library
    public DateObj() {
    }

    public DateObj(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }
}