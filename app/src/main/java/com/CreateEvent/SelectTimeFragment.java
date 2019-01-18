package com.CreateEvent;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.view.View;

import com.example.styledmap.R;

import java.util.Calendar;

public class SelectTimeFragment extends DialogFragment {

    private final static String EXTRA_ARGUMENTS_LISTENER = "listener";

    TimePickerDialog.OnTimeSetListener listener;


    public static SelectTimeFragment newInstance(boolean isStartTimeDialog) {
        SelectTimeFragment fragment = new SelectTimeFragment();
        Bundle args = new Bundle();
        args.putBoolean(EXTRA_ARGUMENTS_LISTENER, isStartTimeDialog);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // That's a trick to get the right listener from the CreateEventFragment
        if (getArguments().getBoolean(EXTRA_ARGUMENTS_LISTENER)) {
            listener = ((CreateEventTypeFragment)
                    getActivity()
                            .getSupportFragmentManager()
                            .findFragmentById(R.id.create_event_fragment)).startTimeListener;
        } else {
            listener = ((CreateEventTypeFragment)
                    getActivity()
                            .getSupportFragmentManager()
                            .findFragmentById(R.id.create_event_fragment)).endTimeListener;
        }

    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(),
                listener,
                hour,
                minute,
                true);
    }

}
