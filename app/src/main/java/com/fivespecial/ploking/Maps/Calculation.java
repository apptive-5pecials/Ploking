package com.fivespecial.ploking.maps;
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

            if(binLocation.getLatitude() != null && binLocation.getLongitude() != null) {
                dLat = binLocation.getLatitude();
                dLong = binLocation.getLongitude();
            }

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
