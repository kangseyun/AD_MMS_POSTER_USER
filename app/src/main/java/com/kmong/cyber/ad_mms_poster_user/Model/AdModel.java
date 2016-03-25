package com.kmong.cyber.ad_mms_poster_user.Model;

import android.content.Context;

import com.orm.SugarRecord;

/**
 * Created by tpdbs on 2016-03-25.
 */
public class AdModel extends SugarRecord{
    public String img_url;
    public String content;

    public String getImg_url() {
        return img_url;
    }

    public String getContent() {
        return content;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }


    public void setContent(String content) {
        this.content = content;
    }

    public AdModel(){

    }

    public AdModel(String img_url, String content){
        this.img_url = img_url;
        this.content = content;
    }
}
