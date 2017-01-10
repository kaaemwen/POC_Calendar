package com.adneom.kdevillard.poc_calendar;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    private LinearLayout linearResponse;
    private Button btnBottomDown;

    private ContentValues values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addingEventCalendar();

        Button btnCalendar = (Button) findViewById(R.id.btnAddCalendar);
        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra("beginTime", cal.getTimeInMillis());
                intent.putExtra("allDay", true);
                intent.putExtra("rrule", "FREQ=YEARLY");
                intent.putExtra("endTime", cal.getTimeInMillis() + 60 * 60 * 1000);
                intent.putExtra("title", "A Test Event from android app");
                startActivity(intent);
            }
        });

        linearResponse = (LinearLayout) findViewById(R.id.linearResponse);
        btnBottomDown = (Button) findViewById(R.id.btnBottomDown);
        btnBottomDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
                if (linearResponse.getVisibility() == View.GONE) {
                    linearResponse.startAnimation(slideDown);
                    linearResponse.setVisibility(View.VISIBLE);
                } else {
                    linearResponse.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * THIS METHOD ALLOWS TO ADD DIRECTLY AN EVENT TO CALENDAR
     * CHECK IF GRANTED PERMISSIONS FOR CALENDAR
     */
    private void addingEventCalendar() {

        //insert a calendar
        long calID = 1;
        ContentValues values = new ContentValues();
       // The new display name for the calendar
        values.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, "Evenement (KVGT'S CALENDAR)");
        Uri updateUri = ContentUris.withAppendedId(CalendarContract.Calendars.CONTENT_URI, calID);

        Calendar cal = Calendar.getInstance();
        //cal.setTimeZone(TimeZone.getTimeZone("GMT-1"));
        Date dateBg = null;
        Date dateEd = null;
        try {
            dateBg = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse("14/01/2017 17:00");
            dateEd = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse("14/01/2017 19:35");

            Calendar beginTime = Calendar.getInstance();
            cal.setTime(dateBg);

            beginTime.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                    cal.get(Calendar.DATE), cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE));

            Calendar endTime = Calendar.getInstance();
            cal.setTime(dateEd);

            // endTime.set(year, month, day, hourOfDay, minute);
            endTime.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                    cal.get(Calendar.DATE), cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE));

            ContentResolver cr = getContentResolver();
            values = new ContentValues();

            values.put(CalendarContract.Events.DTSTART, beginTime.getTimeInMillis());
            values.put(CalendarContract.Events.DTEND, endTime.getTimeInMillis());
            values.put(CalendarContract.Events.TITLE, "my event test ");
            values.put(CalendarContract.Events.DESCRIPTION, "my description !!! ");
            values.put(CalendarContract.Events.CALENDAR_ID, calID);
            values.put(CalendarContract.Events.ORGANIZER,"gaeltshilombo@gmail.com");
            values.put(CalendarContract.Events.EVENT_LOCATION,"Kinepolis Bruxelles, Boulevard du Centenaire, Bruxelles");
            values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());

            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
            Log.i("Adneom"," added event ---- ");

            long eventID = Long.parseLong(uri.getLastPathSegment());
            ContentValues valuesAttendee = new ContentValues();
            valuesAttendee.put(CalendarContract.Attendees.ATTENDEE_NAME, "Alice");
            valuesAttendee.put(CalendarContract.Attendees.ATTENDEE_EMAIL, "alice@example.com");
            valuesAttendee.put(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP, CalendarContract.Attendees.RELATIONSHIP_ATTENDEE);
            valuesAttendee.put(CalendarContract.Attendees.ATTENDEE_TYPE, CalendarContract.Attendees.TYPE_OPTIONAL);
            valuesAttendee.put(CalendarContract.Attendees.ATTENDEE_STATUS, CalendarContract.Attendees.ATTENDEE_STATUS_INVITED);
            valuesAttendee.put(CalendarContract.Attendees.EVENT_ID, eventID);
            Uri uriAttendee = cr.insert(CalendarContract.Attendees.CONTENT_URI, valuesAttendee);
            Log.i("Adneom"," added attendee ---- ");
        } catch (ParseException e) {
            e.printStackTrace();
            Log.i("Adneom","error is "+e.getMessage());
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            //request the missing permissions
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CALENDAR,Manifest.permission.WRITE_CALENDAR},1);
        }
    }

    /**
     * to handle the case where the user grants the permission. See the documentation
     *
     * @param requestCode matches to request code setted in requestPermissions
     * @param permissions list of asked permissions
     * @param grantResults indicates if permissions are granted, if length si null the permissions are not granted
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case 1 :
                // If request is cancelled, the result arrays are empty.
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // permission was granted

                }else{
                    // permission denied, Disable the functionality that depends on this permission.
                }
                break;
        }
    }
}
