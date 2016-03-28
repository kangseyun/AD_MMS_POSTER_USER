package com.kmong.cyber.ad_mms_poster_user.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kmong.cyber.ad_mms_poster_user.Model.ImageModel;
import com.kmong.cyber.ad_mms_poster_user.R;

import java.util.ArrayList;

/**
 * Created by 12kd1004 on 2016. 3. 23..
 */
public class ImageAdapter extends BaseAdapter {
    private ArrayList<ImageModel> m_list;
    public Context mContext;


    public ImageAdapter(Context mContext){
        this.mContext = mContext;
        m_list = new ArrayList<ImageModel>();
    }

    public int Additem(ImageModel item){
        m_list.add(item);
        return 1;
    }

    @Override
    public int getCount() {
        return m_list.size();
    }

    @Override
    public Object getItem(int position) {
        return m_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void dataclear(){
        m_list.clear();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.image_listview, parent, false);

        TextView number = (TextView)convertView.findViewById(R.id.listview_number);
        TextView name = (TextView)convertView.findViewById(R.id.listview_user_name);


        name.setText(m_list.get(position).name);
        number.setText(m_list.get(position).number);

        return convertView;
    }
}
