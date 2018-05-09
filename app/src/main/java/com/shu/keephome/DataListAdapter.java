package com.shu.keephome;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shu.keephome.db.Data;
import com.shu.keephome.db.Device;
import com.shu.keephome.db.DeviceList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by 14623 on 2018/5/5.
 *
 */

public class DataListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;

    private List<DeviceList> mDataList;


    private OnItemClickListener clickListener;

    public void setClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public static interface OnItemClickListener {
        void onClick(View view, int position);
    }




    static class ViewHolder extends RecyclerView.ViewHolder{




        public ViewHolder(View view){
            super(view);

        }
    }

    public DataListAdapter(List<DeviceList> dataList){
        mDataList = dataList;
    }

//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
//        if (mContext == null){
//            mContext = parent.getContext();
//        }
//        View view = LayoutInflater.from(mContext).inflate(R.layout.device_item, parent ,false);
//        return new ViewHolder(view);
//    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new DHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.device_item, parent, false));

    }


    /**
     * 数据绑定
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final DeviceList data = mDataList.get(position);
        Date date = new Date();
        long d1 = date.getTime();
        Log.d(TAG, "onBindViewHolder: d1 = " + date);
        long d2 = data.nowtime.date.getTime();
        Log.d(TAG, "onBindViewHolder: d2 = " + data.nowtime.date);
        Log.d(TAG, "onBindViewHolder: 时间差："+ (int)(d1 - d2));
        if((int)(d1 - d2) < 5*60*1000){
            ((DHolder)holder).device_set_text.setText("在线");
            ((DHolder)holder).device_set_img.setImageResource(R.drawable.ic_online);
        }else {
            ((DHolder)holder).device_set_text.setText("离线");
            ((DHolder)holder).device_set_img.setImageResource(R.drawable.ic_outoffline);
        }
        String uptime_temp = String.valueOf(data.nowtime.date);
        String hum_temp = String.valueOf(data.nowtime.hum) + " %";
        String tem_temp = String.valueOf(data.nowtime.temp) + "℃";
        String tag_temp = data.devName;
        Log.d(TAG, "onBindViewHolder: main = " + data.nowtime);
        ((DHolder)holder).upTime.setText(uptime_temp);
        ((DHolder)holder).main.setText(tag_temp);
        ((DHolder)holder).temp.setText(tem_temp);
        ((DHolder)holder).hum.setText(hum_temp);
        ((DHolder)holder).pm2_5.setText(String.valueOf(data.nowtime.pm2_5));
        ((DHolder)holder).hcho.setText(String.valueOf(data.nowtime.hcho));
        //将数据保存在itemView的Tag中，以便点击时进行获取
//        holder.itemView.setTag(mDataList.get(position));
    }



    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    class DHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CardView cardView;
        TextView main;
        TextView temp;
        TextView hum;
        TextView pm2_5;
        TextView hcho;
        TextView upTime;

        ImageView device_set_img;
        TextView device_set_text;

        public DHolder(View itemView){
            super(itemView);
//            cardView = (CardView) view;
            main = (TextView) itemView.findViewById(R.id.device_main);
            temp = (TextView) itemView.findViewById(R.id.device_temp);
            hum = (TextView) itemView.findViewById(R.id.device_hum);
            pm2_5 = (TextView) itemView.findViewById(R.id.device_pm2_5);
            hcho = (TextView) itemView.findViewById(R.id.device_hcho);
            device_set_img = (ImageView) itemView.findViewById(R.id.device_set_img);
            device_set_text = (TextView) itemView.findViewById(R.id.device_set_text);
            upTime = (TextView) itemView.findViewById(R.id.device_time);

            cardView = (CardView) itemView.findViewById(R.id.card_container);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null){
                clickListener.onClick(itemView, getAdapterPosition());
            }
        }
    }

}
