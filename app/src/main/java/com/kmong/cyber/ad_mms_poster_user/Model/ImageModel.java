package com.kmong.cyber.ad_mms_poster_user.Model;

/**
 * Created by 12kd1004 on 2016. 3. 23..
 */
public class ImageModel {
    public String name;
    public String number;


    public ImageModel(String name){
        this.name = name;
    }

    public ImageModel(String number, String name){
        this.name = name;
        this.number = number;
    }
}
