package com.CreateEvent;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ViewPagerContact.ViewPagerActivity;
import com.example.styledmap.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.WINDOW_SERVICE;


public class CreateEventTypeFragment extends Fragment
        implements View.OnClickListener, DatePickerDialog.OnDateSetListener{

    private static final String LIFECYCLE_ON_SAVE_EVENT_START = "event_start";

    private static final String LIFECYCLE_ON_SAVE_EVENT_END = "event_start";

    private static final String LIFECYCLE_ON_SAVE_IMG_LOCATION = "img_location";

    private static final String LIFECYCLE_ON_SAVE_IMG_URI = "img_uri";

    private static final String CAPTURE_IMAGE_FILE_PROVIDER = "com.example.styledmap.android.fileprovider";

    private static final int RESULT_LOAD_IMG = 3;

    private static final int SAVED_IN_GALLERY = 1;

    private static final int SAVED_IN_MEMORY = 2;

    private static final int NOT_SAVED = 0;

    private static final String PHOTO_TAKEN_NAME = "event_photo.jpg";

    private EditText etEventName;

    private TextView tvCalendar, tvTime, tvEndCalendar,
            tvEndTime, tvPlace, tvDetails, tvInvite, tvCoorg;

    private Button bChooseImage, bCreateEvent;

    private ImageView eventBG;

    private OnFragmentInteractionListener mListener;

    private Calendar calendar = Calendar.getInstance();

    private SelectDateFragment dateStartFragment, dateEndFragment;

    private SelectTimeFragment timeStartFragment, timeEndFragment;

    private Calendar calendarStartEvent, calendarEndEvent;

    // 0 for no image, 1 for gallery, 2 for saved in internal memory after took from camera
    private int eventImageLocationSaved = NOT_SAVED;

    // used when the image is stored in gallery
    private Uri imageUri;

    // the image taken from camera is stored here
    private Uri photoUri;


    private Bitmap imageBitmap;

    public TimePickerDialog.OnTimeSetListener startTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            setStartTimeCalendar(hourOfDay, minute);
        }
    };

    public TimePickerDialog.OnTimeSetListener endTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            setEndTimeCalendar(hourOfDay, minute);
        }
    };


    public CreateEventTypeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        File path = new File(getContext().getFilesDir(), "images");
        if (!path.exists()) path.mkdirs();
        System.out.println(path.toString());
        File image = new File(path, PHOTO_TAKEN_NAME);

        photoUri = FileProvider.getUriForFile(getContext(), CAPTURE_IMAGE_FILE_PROVIDER, image);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {

        // Saving startEvent and endEvent if they are available
        if (calendarStartEvent != null) {
            outState.putLong(LIFECYCLE_ON_SAVE_EVENT_START, calendarStartEvent.getTimeInMillis());
        }
        if (calendarEndEvent != null) {
            outState.putLong(LIFECYCLE_ON_SAVE_EVENT_END, calendarEndEvent.getTimeInMillis());
        }
        if (imageUri != null) {
            outState.putParcelable(LIFECYCLE_ON_SAVE_IMG_URI, imageUri);
        }
        outState.putInt(LIFECYCLE_ON_SAVE_IMG_LOCATION, eventImageLocationSaved);

        super.onSaveInstanceState(outState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        WindowManager windowManager =  (WindowManager) getActivity().getSystemService(WINDOW_SERVICE);
        if (windowManager != null) {

            int rotation = windowManager.getDefaultDisplay().getRotation();
            if (rotation == Surface.ROTATION_0)
                // Inflate the layout for this fragment
                return inflater.inflate(R.layout.fragment_create_event_type, container, false);

            else if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270)
                // Inflate the layout for this fragment
                return inflater.inflate(R.layout.fragment_create_event_type_landscape, container, false);

            else
                // Inflate the layout for this fragment
                return inflater.inflate(R.layout.fragment_create_event_type, container, false);

        } else {

            return inflater.inflate(R.layout.fragment_create_event_type, container, false);
        }

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        View myView = getView();


        if (myView != null) {

            bChooseImage  = (Button)   myView.findViewById(R.id.open_image_button);
            bCreateEvent  = (Button)   myView.findViewById(R.id.button_create_event);
            eventBG       = (ImageView)myView.findViewById(R.id.event_bg_image);
            etEventName   = (EditText) myView.findViewById(R.id.editTextEventName);
            tvCalendar    = (TextView) myView.findViewById(R.id.textViewCalendar);
            tvTime        = (TextView) myView.findViewById(R.id.textViewTime);
            tvEndCalendar = (TextView) myView.findViewById(R.id.textViewEndCalendar);
            tvEndTime     = (TextView) myView.findViewById(R.id.textViewEndDatetime);
            tvPlace       = (TextView) myView.findViewById(R.id.textViewPlace);
            tvDetails     = (TextView) myView.findViewById(R.id.textViewDetails);
            tvInvite      = (TextView) myView.findViewById(R.id.textViewInvite);
            tvCoorg       = (TextView) myView.findViewById(R.id.textViewCoorganizers);


            String currentTime = getFormatTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

            tvTime.setText(currentTime);
            tvCalendar.setText(R.string.today);

            restoreInstState(savedInstanceState);

            // Then creates the fragments that will be used to display the Calendar and Time Pickers.
            // This values are saved in local attribute.
            createFragments();

            // Finally Sets the onClickListeners to open the Calendar and Time Pickers after
            // the corresponding TextView is pressed.
            // After the listeners are set, the Pickers can be called, and so the
            // "getStartCalendarListener" etc., that's the reason why those values must be set
            // before this call.
            setPickerListeners();


            setButtonListener();


        }
        super.onViewCreated(view, savedInstanceState);
    }


    private void restoreInstState(@Nullable Bundle savedInstanceState) {

        // Restore values of event if necessary and restore views
        if(savedInstanceState != null) {

            if (savedInstanceState.containsKey(LIFECYCLE_ON_SAVE_EVENT_START)) {

                calendarStartEvent = new GregorianCalendar();
                calendarStartEvent.setTimeInMillis(
                        savedInstanceState.getLong(LIFECYCLE_ON_SAVE_EVENT_START)
                );
            }

            if (savedInstanceState.containsKey(LIFECYCLE_ON_SAVE_EVENT_END)) {

                calendarEndEvent = new GregorianCalendar();
                calendarEndEvent.setTimeInMillis(
                        savedInstanceState.getLong(LIFECYCLE_ON_SAVE_EVENT_END)
                );
            }

            if (savedInstanceState.containsKey(LIFECYCLE_ON_SAVE_IMG_LOCATION)) {

                eventImageLocationSaved = savedInstanceState.getInt(
                        LIFECYCLE_ON_SAVE_IMG_LOCATION
                );
            }

            if (savedInstanceState.containsKey(LIFECYCLE_ON_SAVE_IMG_URI)) {

                imageUri = savedInstanceState.getParcelable(
                        LIFECYCLE_ON_SAVE_IMG_URI
                );
            }

            if (eventImageLocationSaved == SAVED_IN_GALLERY && imageUri != null) {

                try {
                    // Extract Bitmap Image from the URI
                    loadImageFromUri(imageUri);
                    setEventImageView();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (eventImageLocationSaved == SAVED_IN_MEMORY && photoUri != null) {

                try {
                    // Extract Bitmap Image from the URI
                    loadImageFromUri(photoUri);
                    setEventImageView();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void createFragments() {

        dateStartFragment = new SelectDateFragment();
        dateEndFragment = new SelectDateFragment();

        // True for start time, false for end timer. This is necessary cause of the lack of
        // TimePickerDialog.getTimePicker(). Thanks Android.
        timeStartFragment = SelectTimeFragment.newInstance(true);
        timeEndFragment = SelectTimeFragment.newInstance(false);
    }


    private void setPickerListeners() {

        tvCalendar.setOnClickListener(this);
        tvTime.setOnClickListener(this);
        tvEndCalendar.setOnClickListener(this);
        tvEndTime.setOnClickListener(this);
    }


    private void setButtonListener() {

        eventBG.setOnClickListener(this);
        bChooseImage.setOnClickListener(this);
        bCreateEvent.setOnClickListener(this);
    }


    @NonNull
    private String getFormatDate(int year, int month, int day) {

        Calendar cal = new GregorianCalendar();
        cal.set(year, month, day);
        Date date_event = cal.getTime();
        String choose_day =
                new SimpleDateFormat("EEEE", Locale.getDefault()).format(date_event.getTime());

        // Control if the event is planned this week, and if it is spell the name of the
        // week instead of the date format
        long diffDays = (cal.getTimeInMillis() / (1000 * 60 * 60 * 24)) -
                        (calendar.getTimeInMillis() / (1000 * 60 * 60 * 24));

        if (diffDays == 0)
            return getString(R.string.today);
        else if (diffDays == 1)
            return getString(R.string.tomorrow);
        else if (diffDays < 8)
            return choose_day;

        return day + "/" + (month+1) + "/" + year;
    }


    @NonNull
    private String getFormatTime(int hour, int minute) {

        String h = hour + "", m = minute + "";

        if (hour < 10) h = "0" + hour;
        if (minute < 10) m = "0" + minute;
        return h + ":" + m;
    }


    private void updateViews(TextView tvCal, TextView tvTime, Calendar cal) {
        if (cal == null) return;

        if (tvCal != null)
            tvCal.setText(
                    getFormatDate(
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)
                    )
            );

        if (tvTime != null)
            tvTime.setText(
                    getFormatTime(
                            cal.get(Calendar.HOUR_OF_DAY),
                            cal.get(Calendar.MINUTE)
                    )
            );
    }


    private void assureConsistency() {

        if (calendarEndEvent != null && calendarStartEvent != null) {

            if (calendarEndEvent.getTimeInMillis() < calendarStartEvent.getTimeInMillis()) {

                // Show a Toast which tells the user that the even must start before it ends
                Toast.makeText(getActivity(), R.string.start_before_event_error,
                        Toast.LENGTH_LONG).show();

                // Sets the end of the event 3 hours after the start of it
                calendarEndEvent.setTimeInMillis(calendarStartEvent.getTimeInMillis() +
                        (1000 * 60 * 60 * 3));

                // Updates textview of End Calandar Event
                updateViews(tvEndCalendar, tvEndTime, calendarEndEvent);
            }
        }
    }


    private void getImageFromAlbum() {

        // Creates gallery pick intent, and mark it as GALLERY_IMAGE
        Intent galleryPhotoPickerIntent = new Intent(Intent.ACTION_PICK);
        galleryPhotoPickerIntent.setType("image/*");

        Intent chooserIntent =
                Intent.createChooser(galleryPhotoPickerIntent, getString(R.string.select_image));

        if (photoUri != null) {
            // Creates photo pick intent, and mark it as TAKE_PHOTO
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {takePictureIntent});
        }

        startActivityForResult(chooserIntent, RESULT_LOAD_IMG);

    }


    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (reqCode == RESULT_LOAD_IMG && resultCode == RESULT_OK) {
            try {

                imageBitmap = null;

                // if the Extras value is null, or more precisely the Extras key "data" is null,
                // then photo is from gallery, otherwise is taken from memory
                if (data != null) {

                    imageUri = data.getData();

                    if (imageUri != null) {

                        loadImageFromUri(imageUri);

                        // Sets this variable to 1 for saveInstanceState, so it knows that the event
                        // photo is stored in gallery
                        eventImageLocationSaved = SAVED_IN_GALLERY;
                    }

                } else if (photoUri != null){

                    loadImageFromUri(photoUri);

                    // Sets this variable to 2 for saveInstanceState, so it knows that the event
                    // photo is stored in internal memory
                    eventImageLocationSaved = SAVED_IN_MEMORY;

                }

                setEventImageView();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), R.string.something_wrong, Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), R.string.something_wrong, Toast.LENGTH_LONG).show();
            }
        }
    }


    private void loadImageFromUri(Uri mUri) throws IOException{

        // Extract Bitmap Image from the URI
        final InputStream imageStream =
                getContext().getContentResolver().openInputStream(mUri);

        imageBitmap = BitmapFactory.decodeStream(imageStream);
    }


    private void setEventImageView() {

        if (imageBitmap != null) {

            // Set the bgImage to the chosen Image
            eventBG.setScaleType(ImageView.ScaleType.CENTER);
            eventBG.setImageBitmap(imageBitmap);
            eventBG.setVisibility(ImageView.VISIBLE);
            bChooseImage.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onClick(View v) {

        if (v == eventBG || v == bChooseImage) {
            getImageFromAlbum();

        } else if (v == bCreateEvent) {
            Intent joinEventActivity = new Intent(getContext(), ViewPagerActivity.class);
            startActivity(joinEventActivity);

        } else if (v == tvCalendar) {
            dateStartFragment.show(getFragmentManager(), "DatePicker1");

        } else if (v == tvTime) {
            timeStartFragment.show(getFragmentManager(), "TimePicker1");

        } else if (v == tvEndCalendar) {
            dateEndFragment.show(getFragmentManager(), "DatePicker2");

        } else if (v == tvEndTime) {
            timeEndFragment.show(getFragmentManager(), "TimePicker2");

        }

    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {

        if (dateStartFragment.datePickerDialog != null &&
                view == dateStartFragment.datePickerDialog.getDatePicker()) {

            setStartDateCalendar(year, month, day);

        } else if (dateEndFragment.datePickerDialog != null &&
                view == dateEndFragment.datePickerDialog.getDatePicker()) {

            setEndDateCalendar(year, month, day);
        }

    }


    private void setStartDateCalendar(int year, int month, int day){

        // If it already exist a calendar, extract from it the hour and minute
        if (calendarStartEvent != null)
            calendarStartEvent.set(
                    year, month, day,
                    calendarStartEvent.get(Calendar.HOUR_OF_DAY),
                    calendarStartEvent.get(Calendar.MINUTE)
            );
        else calendarStartEvent =
                new GregorianCalendar(
                        year, month, day,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE));


        // controls that the end time of the event is AFTER the start of it
        assureConsistency();

        // Updates textview of Start Calendar
        updateViews(tvCalendar, null, calendarStartEvent);
    }


    private void setEndDateCalendar(int year, int month, int day){

        if (calendarStartEvent == null) {
            calendarStartEvent = new GregorianCalendar();
        }
        // If it already exist a calendar, extract from it the hour and minute
        if (calendarEndEvent != null)
            calendarEndEvent.set(
                    year, month, day,
                    calendarEndEvent.get(Calendar.HOUR_OF_DAY),
                    calendarEndEvent.get(Calendar.MINUTE)
            );

        else calendarEndEvent = new GregorianCalendar(year, month, day);

        assureConsistency();

        // Updates textview of End Calendar
        updateViews(tvEndCalendar, null, calendarEndEvent);
    }


    private void setStartTimeCalendar(int hourOfDay, int minute){

        if (calendarStartEvent != null)
            calendarStartEvent.set(
                    calendarStartEvent.get(Calendar.YEAR),
                    calendarStartEvent.get(Calendar.MONTH),
                    calendarStartEvent.get(Calendar.DAY_OF_MONTH),
                    hourOfDay,
                    minute
            );
        else calendarStartEvent =
                new GregorianCalendar(
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH),
                        hourOfDay,
                        minute
                );

        assureConsistency();

        updateViews(null, tvTime, calendarStartEvent);
    }


    private void setEndTimeCalendar(int hourOfDay, int minute){

        if (calendarStartEvent == null) {
            calendarStartEvent = new GregorianCalendar();
        }
        if (calendarEndEvent != null)
            calendarEndEvent.set(
                    calendarEndEvent.get(Calendar.YEAR),
                    calendarEndEvent.get(Calendar.MONTH),
                    calendarEndEvent.get(Calendar.DAY_OF_MONTH),
                    hourOfDay,
                    minute
            );
        else calendarEndEvent =
                new GregorianCalendar(
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH),
                        hourOfDay,
                        minute
                );

        assureConsistency();

        updateViews(tvEndCalendar, tvEndTime, calendarEndEvent);
    }



    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
