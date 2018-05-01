package com.shu.keephome;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.shu.keephome.db.Data;
import com.shu.keephome.db.Device;
import com.shu.keephome.db.User;
import com.shu.keephome.util.HttpUtil;
import com.shu.keephome.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static java.lang.String.valueOf;

/**
 * Created by 14623 on 2018/5/1.
 */

public class ChooseDeviceFragment extends Fragment{

    public static final int LEVEL_USER = 0;

    public static final int LEVEL_DEVICE = 1;

    public static final int LEVEL_DATA = 2;

    ProgressDialog progressDialog;

    private TextView titleText;

    private Button backButton;

    private ListView listView;

    private ArrayAdapter<String> adapter;

    private List<String> dataList = new ArrayList<>();

    /**
     * 用户
     */
    private List<User> userList;

    /**
     * 设备列表
     */
    private List<Device> deviceList;

    /**
     * 设备数据列表
     */
    private List<Data> devicedataList;

    /**
     * 选中的用户
     */
    private User selectedUser;

    /**
     * 选中的设备
     */
    private Device selectedDevice;

    /**
     * 选中的数据
     */
    private Data selectedData;

    /**
     * 选中的级别
     */
    private int currentLevel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.choose_device, container, false);
        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_DEVICE){
                    selectedDevice = deviceList.get(position);
                    queryData();
                }else if (currentLevel == LEVEL_USER){
                    selectedUser = userList.get(position);
                    queryDevices();
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_DATA){
                    queryDevices();
                }else if(currentLevel == LEVEL_DEVICE){
                    queryUsers();
                }
            }
        });
        queryUsers();
    }

    /**
     * 查询所有用户，从服务器中获取
     */
    private void queryUsers(){
        titleText.setText("用户列表");
        backButton.setVisibility(View.GONE);
        userList = DataSupport.findAll(User.class);
        if (userList.size() > 0){
            dataList.clear();
            for (User user : userList){
                dataList.add(user.getName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_DEVICE;
        }else {
            String address = "http://39.106.213.217:8080/api/prouser/";
            queryFromServer(address, "user");
        }
    }

    /**
     * 查询选中的用户的所有的设备，优先从数据库中查询，如果没有查询到再去服务器上查询
     */
    private void queryDevices(){
        titleText.setText(selectedUser.getName());
        backButton.setVisibility(View.VISIBLE);
        deviceList = DataSupport.where("userid = ?", String.valueOf(selectedUser.getId())).find(Device.class);
        if (deviceList.size() > 0){
            dataList.clear();
            for (Device device : deviceList){
                dataList.add(device.getName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_DEVICE;
        }else{
            String address = "http://39.106.213.217:8080/api/prouser/";
            queryFromServer(address, "device");
        }
    }

    /**
     * 查询选中的设备的所有数据，优先从数据库中查询，如果没有查询到再去服务器上查询
     */
    private void queryData(){
        titleText.setText(selectedDevice.getName());
        backButton.setVisibility(View.VISIBLE);
        devicedataList = DataSupport.where("deviceid = ?", String.valueOf(selectedDevice.getId())).find(Data.class);
        if (devicedataList.size() > 0 ){
            dataList.clear();
            for (Data data : devicedataList){
                dataList.add(data.getCreated());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_DATA;
        }else{
            String address = "http://39.106.213.217:8080/api/prouser/";
            queryFromServer(address, "data");
        }
    }

    /**
     * 根据传入的地址和类型从服务器上查询对应数据
     */
    private void queryFromServer(String address, final String type){
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 通过runOnUiThread()回主线程
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if("user".equals(type)){
                    result = Utility.handleUserResponse(responseText);
                }else if ("device".equals(type)){
                    result = Utility.handleDevResponse(responseText);
                }else if ("data".equals(type)){
                    result = Utility.handleDataResponse(responseText);
                }
                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("user".equals(type)){
                                queryUsers();
                            }else if ("device".equals(type)){
                                queryDevices();
                            }else if ("data".equals(type)){
                                queryData();
                            }
                        }
                    });
                }
            }


        });
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }

}
