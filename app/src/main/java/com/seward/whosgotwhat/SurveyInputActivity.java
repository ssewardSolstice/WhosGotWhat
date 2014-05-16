package com.seward.whosgotwhat;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class SurveyInputActivity extends Activity implements LocationListener {

    private static final long VIBRATE_DUATION = 50;
    private static final LinkedList<DeviceSighting> sightingQueue = new LinkedList<DeviceSighting>();

    private int androidCount = 0;
    private int iPhoneCount = 0;
    private Vibrator buzzer = null;
    private LocationManager locationService = null;
    private Location lastKnownGPSLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.survey_input_view);
        locationService = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        buzzer = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationService.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationService.removeUpdates(this);
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lastKnownGPSLocation = location;
        saveQueuedSightings();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    public final void androidTapped(final View button) {
        recordDeviceSeen(new AndroidSighting());
        ++androidCount;
        setTapCountOnView(button, androidCount);
    }

    public final void iPhoneTapped(final View button) {
        recordDeviceSeen(new iPhoneSighting());
        ++iPhoneCount;
        setTapCountOnView(button, iPhoneCount);
    }

    private void saveQueuedSightings() {
        if (sightingQueue != null && sightingQueue.size() > 0) {
            DeviceSighting sighting;
            while ((sighting = sightingQueue.remove()) != null) {
                saveSighting(sighting);
            }
        }
    }

    private void saveSighting(final DeviceSighting sighting) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            final String fileOutputName = Environment.getExternalStorageDirectory().getPath() + "/WhosGotWhat/LastSighting.json";
            mapper.writeValue(new File(fileOutputName), sighting);
            Toast.makeText(this, "Writing sighting: " + fileOutputName, Toast.LENGTH_SHORT).show();

        } catch (final IOException e) {
            //Unable to write file.
            Log.e("WhosGotWhat", "Cannot write file! IOException: " + e);
            sightingQueue.add(sighting);
        }
    }

    private void setTapCountOnView(final View view, final int count) {
        if (view instanceof TextView) {
            TextView textButton = (TextView) view;
            final String[] currentText = textButton.getText().toString().split(" ");
            final String textWithCount = currentText[0] + " " + count;
            textButton.setText(textWithCount);
        }
    }

    private void recordDeviceSeen(final DeviceSighting deviceSighting) {
        buzzer.vibrate(VIBRATE_DUATION);

        if (lastKnownGPSLocation == null) {
            Toast.makeText(this, "Queueing sighting", Toast.LENGTH_SHORT).show();
            queueSighting(deviceSighting);
        } else {
            deviceSighting.setLocation(lastKnownGPSLocation);
        }
    }

    private void queueSighting(final DeviceSighting deviceSighting) {
        sightingQueue.add(deviceSighting);
    }

}
