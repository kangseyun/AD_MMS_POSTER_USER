package com.kmong.cyber.ad_mms_poster_user.Model;

import android.content.Context;

import com.orm.SugarRecord;

/**
 * Created by tpdbs on 2016-03-25.
 */
public class AdModel extends SugarRecord{
    String img_url;
    String content;

    public AdModel(){

    }

    public AdModel(String img_url, String content){
        this.img_url = img_url;
        this.content = content;
    }
}
