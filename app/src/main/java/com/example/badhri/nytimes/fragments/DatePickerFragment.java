package com.example.badhri.nytimes.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.example.badhri.nytimes.parcels.DateObj;

import org.parceler.Parcels;

/**
 * Created by badhri on 10/23/16.
 */
public class DatePickerFragment extends DialogFragment {
    private static final String TAG = "DatePickerFragment";

    private int year;
    private int month;
    private int day;

    public DatePickerFragment() {
    }


    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        DateObj dateParcel = Parcels.unwrap(args.getParcelable("date"));
        year = dateParcel.getYear();
        month = dateParcel.getMonth();
        day = dateParcel.getDay();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                // here is the place to find the right element
                EditDateDialogListener listener = (EditDateDialogListener) getTargetFragment();
                String date = String.format("%d/%d/%d", monthOfYear + 1, dayOfMonth, year);
                listener.onFinishEditDialog(date);
            }
        };
        return new DatePickerDialog(getActivity(), ondate, year, month, day);
    }


    public interface EditDateDialogListener {
        void onFinishEditDialog(String inputText);
    }

}
