package com.fivespecial.ploking.Maps;

import android.location.Location;

import java.util.List;

public class Calculation {

    BinLocation binLocation;
    double dLat, dLong;
//    List<BinLocation> nearBin;

    public Calculation() {
    }

    public int NearBins(List<BinLocation> binLocationList, double currentLat, double currentLong) {
        int count = 0;

        Location locationB = new Location("point B");
        locationB.setLatitude(currentLat);
        locationB.setLongitude(currentLong);

        for(int i = 0; i < binLocationList.size(); i++) {
            binLocation = binLocationList.get(i);
            dLat = Double.parseDouble(binLocation.latitude);
            dLong = Double.parseDouble(binLocation.longitude);

            Location locationA = new Location("point A");
            locationA.setLatitude(dLat);
            locationA.setLongitude(dLong);

            if(locationA.distanceTo(locationB) < 1000){
//                nearBin.add(binLocation);
                count++;
            }
        }
            return count;
    }



}
