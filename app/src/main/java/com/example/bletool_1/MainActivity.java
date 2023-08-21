package com.example.bletool_1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
    private boolean isScanning = false ;
    private boolean isClearList = false;

    TextView mRemoteRssiVal;
    RadioGroup mRg;
    private int mState = UART_PROFILE_DISCONNECTED;
    //    private UartService mServic                      e = null;
    private BluetoothDevice mDevice = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private ListView messageListView;

    private ArrayAdapter<String> listAdapter;
    private Button btnConnectDisconnect, btnSend, btnTestClearView;
    private EditText edtMessage;
    private BluetoothAdapter.LeScanCallback callback;
    private BluetoothLeScanner bluetoothLeScanner;

    private List<MyBleInfo> infoList=new ArrayList<>();
    ListAdapter my_list_adapter;
    ListView listView;
    TextView testView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.list_view);
        testView = findViewById(R.id.test_view);

        btnConnectDisconnect = (Button) findViewById(R.id.btn_select);
        btnTestClearView = (Button) findViewById(R.id.btn_test_clear_view);
        Handler mHandler = new Handler();

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "this device does not support ble", Toast.LENGTH_LONG).show();
            finish();
            return;
        } else {
            Toast.makeText(this, "this device support ble", Toast.LENGTH_LONG).show();
        }

        if (true) {
            int sdk_ver = Build.VERSION.SDK_INT;
            Toast.makeText(this, "device android sdk version is : " +
                    String.valueOf(sdk_ver), Toast.LENGTH_LONG).show();
        }

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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

        my_list_adapter = new ListAdapter(MainActivity.this, R.layout.list_view, infoList);
        listView.setAdapter(my_list_adapter);

        // Handler Disconnect & Connect button
        btnConnectDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ScanCallback mScanCallback = new ScanCallback() {
                    @Override
                    public void onScanResult(int callbackType, ScanResult result) {
                        BluetoothDevice dev = result.getDevice();
                        result.getScanRecord();

//                        String name = dev.getName();
//                        Toast.makeText(MainActivity.this, dev.getName(),
//                                Toast.LENGTH_SHORT).show();

//                        if(isClearList == true){
//                            my_list_adapter.clear();
//                        }else {
//                            isClearList = false;
//                        }
                        MyBleInfo info = new MyBleInfo(dev.getName(), dev.getAddress(),
                                dev.getAlias(), "NA");
                        my_list_adapter.add(info);
                    }
                };

                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
                bluetoothLeScanner.startScan(mScanCallback);
                isClearList = true;

                final ScanCallback mStopScanCallback = new ScanCallback() {
                    @Override
                    public void onScanResult(int callbackType, ScanResult result) {
                        isScanning = false;
                    }
                };
                 mHandler.postDelayed(new Runnable() {
                    @Override
                     public void run() {
                        isScanning = false;
                        bluetoothLeScanner.stopScan(mStopScanCallback);
                    }
                }, 3000);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyBleInfo bleInfo = (MyBleInfo) parent.getItemAtPosition(position);

                Toast.makeText((Context)MainActivity.this,
                        bleInfo.getUuid()+bleInfo.getRssi(),
                        Toast.LENGTH_LONG).show();
//                testView.clearComposingText();
//                testView.append(bleInfo.getUuid());
            }
        });


        btnTestClearView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(my_list_adapter != null){
                    my_list_adapter.clear();
                }else {
                    Toast.makeText(MainActivity.this, "list view is not init",
                            Toast.LENGTH_SHORT).show();
                }
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
                Toast.makeText(this, "ble is already opened!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "there isn't permission of ble.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}