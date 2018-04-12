package com.example.nguyenthanhxuan.appcaro.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.nguyenthanhxuan.appcaro.R;
import com.example.nguyenthanhxuan.appcaro.data.ItemCaro;

import java.util.List;

/**
 * Created by Nguyen Thanh Xuan on 4/11/2018.
 */

public class CaroAdapter extends BaseAdapter {
    private List<ItemCaro> mList;
    private LayoutInflater minflate;
    private Context mContext;
    public CaroAdapter(Context c,List<ItemCaro> ListPlayer)
    {
        this.mList = ListPlayer;
        this.minflate =LayoutInflater.from(c);
        this.mContext = c;
    }
    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return arg0;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHoler holer;
        if(convertView == null)
        {
            holer = new ViewHoler();
            convertView = minflate.inflate(R.layout.gird_item,null);
            holer.img =(ImageView)convertView.findViewById(R.id.imageGridItem);
            if(mList.get(position).getIsYou() =='o')
                holer.img.setImageResource(R.drawable.img_o);
            if(mList.get(position).getIsYou() =='x')
                holer.img.setImageResource(R.drawable.img_x);
            convertView.setTag(holer);
        }
        else
            holer =(ViewHoler) convertView.getTag();
        holer.img.setTag(position);
        return convertView;
    }
    class ViewHoler
    {
        ImageView img;
    }
}
