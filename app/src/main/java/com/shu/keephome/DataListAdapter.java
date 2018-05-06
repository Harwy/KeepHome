package com.shu.keephome;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shu.keephome.db.Data;
import com.shu.keephome.db.Device;

import java.util.List;

/**
 * Created by 14623 on 2018/5/5.
 *
 */

public class DataListAdapter extends RecyclerView.Adapter<DataListAdapter.ViewHolder> {

    private Context mContext;

    private List<Data> mDataList;

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    //define interface
    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view , String data);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView main;
        TextView temp;
        TextView hum;
        TextView pm2_5;
        TextView hcho;



        public ViewHolder(View view){
            super(view);
            cardView = (CardView) view;
            main = (TextView) itemView.findViewById(R.id.device_main);
            temp = (TextView) itemView.findViewById(R.id.device_temp);
            hum = (TextView) itemView.findViewById(R.id.device_hum);
            pm2_5 = (TextView) itemView.findViewById(R.id.device_pm2_5);
            hcho = (TextView) itemView.findViewById(R.id.device_hcho);
        }
    }

    public DataListAdapter(List<Data> dataList){
        mDataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if (mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.device_item, parent ,false);
        return new ViewHolder(view);
    }

    /**
     * 数据绑定
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Data data = mDataList.get(position);
        holder.main.setText(data.getCreated());
        holder.temp.setText(String.valueOf(data.getTemp()));
        holder.hum.setText(String.valueOf(data.getHum()));
        holder.pm2_5.setText(String.valueOf(data.getPm2_5()));
        holder.hcho.setText(String.valueOf(data.getHcho()));
        //将数据保存在itemView的Tag中，以便点击时进行获取
        holder.itemView.setTag(mDataList.get(position));
    }



    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    //    /**
//     *设备控件
//     */
//    public class DeviceItemHolder extends RecyclerView.ViewHolder{
//        TextView main;
//        TextView temp;
//        TextView hum;
//        TextView pm2_5;
//        TextView hcho;
//
//        public DeviceItemHolder(View itemView){
//            super(itemView);
//            main = (TextView) itemView.findViewById(R.id.device_main);
//            temp = (TextView) itemView.findViewById(R.id.device_temp);
//            hum = (TextView) itemView.findViewById(R.id.device_hum);
//            pm2_5 = (TextView) itemView.findViewById(R.id.device_pm2_5);
//            hcho = (TextView) itemView.findViewById(R.id.device_hcho);
//            itemView.findViewById(R.id.base_device_container).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    showNewDevice(getOldPosition());
//                }
//            });
//        }
//    }
}
