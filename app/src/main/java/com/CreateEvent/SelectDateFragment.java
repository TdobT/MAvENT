package com.CreateEvent;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.example.styledmap.R;

import java.util.Calendar;


public class SelectDateFragment extends DialogFragment {

    public DatePickerDialog datePickerDialog;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                ((CreateEventTypeFragment)
                        getActivity()
                                .getSupportFragmentManager()
                                .findFragmentById(R.id.create_event_fragment)),
                yy,
                mm,
                dd);

        dpd.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog = dpd;

        return dpd;
    }

}