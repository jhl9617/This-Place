package com.example.testrightnow;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class TalkAdapter extends BaseAdapter {
    LayoutInflater inflater;
    ArrayList<TalkItem> talkItems;

    public TalkAdapter(LayoutInflater inflater, ArrayList<TalkItem> talkItems){
        this.inflater=inflater;
        this.talkItems = talkItems;
    }
    @Override
    public int getCount(){
        return talkItems.size();
    }
    @Override
    public Object getItem(int position){
        return talkItems.get(position);
    }
    @Override
    public long getItemId(int position){
        return position;
    }
    @Override
    public View getView(int position, View view, ViewGroup viewGroup){
        if(view == null){
            view = inflater.inflate(R.layout.list_item,viewGroup,false);
        }
        TextView tvName=view.findViewById(R.id.tv_name);
        TextView tvDate=view.findViewById(R.id.tv_date);
        ImageView iv = view.findViewById(R.id.iv);

        TalkItem talkItem = talkItems.get(position);
        tvName.setText(talkItem.getDate());

        Glide.with(view).load(talkItem.getImgPath()).into(iv);
        return view;
    }
}
