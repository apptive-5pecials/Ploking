package com.fivespecial.ploking.MemberClass;

public class Album {
    private int id;
    private String path;
    private  String file_name;
    private double longitude;
    private double latitude;

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getPath(){return path;}

    public void setPath(String path) {
        this.path = path;
    }

    public String getFile_name(){return file_name;}

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public int getId(){return id;}

    public  void setId(int id){ this.id= id; }


}
