package com.example.bletool_1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.ArrayList;

import com.example.bletool_1.MyBleInfo;

import java.util.Collections;

public class ListAdapter extends ArrayAdapter<MyBleInfo> {
    private List<MyBleInfo> listInfo;

    //用于将上下文、list view 子项布局 id 和数据都传递过来
    public ListAdapter(@NonNull Context context, int resource, @NonNull List<MyBleInfo> list) {
        super(context, resource, list);
        listInfo = list;
    }

    //增加一个方法添加动态数据
    public void add(MyBleInfo info) {
        listInfo.add(info);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        MyBleInfo info = getItem(position);
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_view, parent,
                    false);
        } else {
            view = convertView;
        }

        TextView b_name = view.findViewById(R.id.ble_name);
        TextView b_uuid = view.findViewById(R.id.uuid_num);
        TextView b_rssi = view.findViewById(R.id.rssi_val);
        TextView b_state = view.findViewById(R.id.state);

        b_name.setText(info.getName());
        b_rssi.setText(info.getRssi());
        b_uuid.setText(info.getUuid());
        b_state.setText(info.getState());

        return view;
    }
}
