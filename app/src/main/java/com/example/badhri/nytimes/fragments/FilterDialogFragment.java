package com.example.badhri.nytimes.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.example.badhri.nytimes.R;
import com.example.badhri.nytimes.parcels.DateObj;

import org.parceler.Parcels;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by badhri on 10/23/16.
 */
public class FilterDialogFragment extends DialogFragment implements
        DatePickerFragment.EditDateDialogListener{
    @BindView(R.id.tvTitle) TextView tvTitle;
    @BindView(R.id.etBeginDate)EditText etDate;
    @BindView(R.id.btnOK)Button btnOK;
    @BindView(R.id.btnCancel)Button btnCancel;
    @BindView(R.id.spinner)Spinner spnSort;
    @BindView(R.id.cbsports)CheckBox chkSports;
    @BindView(R.id.cbFashion)CheckBox chkFashion;
    @BindView(R.id.cbArts)CheckBox chkArts;

    private Unbinder unbinder;
    Context context;

    public FilterDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static FilterDialogFragment newInstance(String title, Context context) {
        FilterDialogFragment frag = new FilterDialogFragment();
        frag.context = context;
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dailog_filter, container);
        unbinder = ButterKnife.bind(this, view);
        InputMethodManager im = (InputMethodManager)context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(etDate.getWindowToken(), 0);

        etDate.requestFocus();
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    showDatePicker();
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FilterDialogListener listener = (FilterDialogListener) getActivity();
                listener.onFinishFilterDialog(etDate.getText().toString(),
                        spnSort.getSelectedItem().toString(),
                        chkSports.isChecked(), chkFashion.isChecked(), chkArts.isChecked());
                getDialog().dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void setSpinnerToValue(Spinner spinner, String value) {
        int index = 0;
        SpinnerAdapter adapter = spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(value)) {
                index = i;
                break; // terminate loop
            }
        }
        spinner.setSelection(index);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvTitle = (TextView)view.findViewById(R.id.tvTitle);
        String title = getArguments().getString("title", "Filter Settings");
        tvTitle.setText(title);

        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(context);
        if (!pref.getString("beginDate", "n/a").contentEquals("n/a")) {
            etDate.setText(Integer.toString(pref.getInt("month", 1)) + "/" +
                    Integer.toString(pref.getInt("date", 1)) + "/" +
                    Integer.toString(pref.getInt("year", 2000)));
        }

        if (!pref.getString("sortOrder", "n/a").contentEquals("n/a")) {
            setSpinnerToValue(spnSort, pref.getString("sortOrder", "n/a"));
        }

        chkArts.setChecked(pref.getBoolean("arts", false));
        chkSports.setChecked(pref.getBoolean("sports", false));
        chkFashion.setChecked(pref.getBoolean("fashion", false));

    }

    private void showDatePicker() {
        DatePickerFragment date = new DatePickerFragment();

        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(context);

        int setYear, setMonth, setDate;
        if (!etDate.getText().toString().isEmpty()) {
            String[] units = etDate.getText().toString().split("/");
            setYear = Integer.parseInt(units[2]);
            setMonth = Integer.parseInt(units[0]) - 1;
            setDate = Integer.parseInt(units[1]);
        } else if (pref.getString("beginDate", "n/a").contentEquals("n/a")) {
            // set date of today
            Calendar calender = Calendar.getInstance();
            setYear = calender.get(Calendar.YEAR);
            setMonth = calender.get(Calendar.MONTH);
            setDate = calender.get(Calendar.DAY_OF_MONTH);
        } else {
            setYear = pref.getInt("year", 2000);
            setMonth = pref.getInt("month", 1) - 1;
            setDate = pref.getInt("date", 1);
        }

        Bundle args = new Bundle();
        DateObj dateParcel = new DateObj(setDate, setMonth, setYear);
        args.putParcelable("date", Parcels.wrap(dateParcel));
        date.setArguments(args);

        date.setTargetFragment(FilterDialogFragment.this, 300);
        date.show(getFragmentManager(), "Date Picker");
    }

    public void onFinishEditDialog(String inputText) {
        etDate.setText(inputText);
    }

    public interface FilterDialogListener {
        void onFinishFilterDialog(String date, String sort_order, boolean sports, boolean fashion,
                                  boolean arts);
    }

}
