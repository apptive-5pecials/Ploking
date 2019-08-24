package com.fivespecial.ploking.MemberClass;

public class Album {
    private int id;
    private String path;
    private  String file_name;



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
