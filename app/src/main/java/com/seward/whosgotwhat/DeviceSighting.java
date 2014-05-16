package com.seward.whosgotwhat;

import android.location.Location;

import java.util.Date;

/**
 * Created by scottseward on 4/14/14.
 */
public class DeviceSighting {

    private final Date sightingDate = new Date();
    private Location location = null;
    private String deviceType = null;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public Date getSightingDate() {
        return sightingDate;
    }

}
