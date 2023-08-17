package com.example.bletool_1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_SELECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int UART_PROFILE_READY = 10;
    public static final String TAG = "nRFUART";
    private static final int UART_PROFILE_CONNECTED = 20;
    private static final int UART_PROFILE_DISCONNECTED = 21;
    private static final int STATE_OFF = 10;

    TextView mRemoteRssiVal;
    RadioGroup mRg;
    private int mState = UART_PROFILE_DISCONNECTED;
    //    private UartService mService = null;
    private BluetoothDevice mDevice = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private ListView messageListView;
    private ListView listView;
    private ArrayAdapter<String> listAdapter;
    private Button btnConnectDisconnect, btnSend;
    private EditText edtMessage;
    private BluetoothAdapter.LeScanCallback callback;
    private BluetoothLeScanner bluetoothLeScanner;
    private ListAdapter my_list_adapter;

    private List<MyBleInfo> infoList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.list_view);

        btnConnectDisconnect = (Button) findViewById(R.id.btn_select);
//        messageListView = (ListView) findViewById(R.id.listMessage);
//        listAdapter = new ArrayAdapter<String>(this, R.layout.message_detail);
//        messageListView.setAdapter(listAdapter);
//        messageListView.setDivider(null);
//        btnSend = (Button) findViewById(R.id.sendButton);
//        edtMessage = (EditText) findViewById(R.id.sendText);
        Handler mHandler = new Handler();

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "this device does not support ble", Toast.LENGTH_LONG).show();
            finish();
            return;
        } else {
            Toast.makeText(this, "this device support ble", Toast.LENGTH_LONG).show();
        }

        //获取系统蓝牙适配器管理类
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // 询问打开蓝牙
        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
//                Toast.makeText(this, "request enable ble fail", Toast.LENGTH_LONG).show();
                return;
            } else {
//                Toast.makeText(this, "request enable ble success", Toast.LENGTH_LONG).show();
            }
            startActivityForResult(enableBtIntent, 1);
        }

        //test listview
        MyBleInfo ble_info = new MyBleInfo("testBle", "94:34:69:3d:33:01",
                "-65", "good");
        infoList.add(ble_info);
        my_list_adapter = new ListAdapter(MainActivity.this, R.layout.list_view, infoList);
        listView.setAdapter(my_list_adapter);

        // Handler Disconnect & Connect button
        btnConnectDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //test list view
                MyBleInfo info = new MyBleInfo("testBle", "94:34:69:3d:33:01",
                        "-65", "good");
                my_list_adapter.add(info);
                //list view set
//                EditText editText = (EditText) findViewById(R.id.sendText);
//                String message = editText.getText().toString();
//                byte[] value;
//                try {
//                    //send data to service
//                    value = message.getBytes("UTF-8");
//                    //Update the log with time stamp
//                    String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
//                    listAdapter.add("["+currentDateTimeString+"] TX: "+ message);
//                    messageListView.smoothScrollToPosition(listAdapter.getCount() - 1);
//                    edtMessage.setText("");
//                } catch (UnsupportedEncodingException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }

                Toast.makeText(MainActivity.super.getBaseContext(), "click scan button", Toast.LENGTH_LONG).show();
                // 扫描结果Callback
                final ScanCallback mScanCallback = new ScanCallback() {
                    @Override
                    public void onScanResult(int callbackType, ScanResult result) {
                        BluetoothDevice dev = result.getDevice();
                        result.getScanRecord();
                        String name = dev.getName();
                        Toast.makeText(MainActivity.this, name, Toast.LENGTH_LONG).show();
                    }
                };

                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
                bluetoothLeScanner.startScan(mScanCallback);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bluetoothLeScanner.stopScan(mScanCallback); //停止扫描
//                        isScanning = false;
                    }
                }, 3000);

//// 旧API是BluetoothAdapter.startLeScan(LeScanCallback callback)方式扫描BLE蓝牙设备，如下：
//                mBluetoothAdapter.startLeScan(callback);
//                callback = new BluetoothAdapter.LeScanCallback() {
//                    @Override
//                    public void onLeScan(BluetoothDevice device, int arg1, byte[] arg2) {
//                        //device为扫描到的BLE设备
//                        Log.e(TAG, device.getName());
//                    }
//                };

            }
        });
    }

    // 申请打开蓝牙请求的回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "蓝牙已经开启", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "没有蓝牙权限", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}