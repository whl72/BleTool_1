package com.example.bletool_1;

public class MyBleInfo {
    private String ble_name;
    private String ble_uuid;
    private String ble_rssi;
    private String ble_state;

    public MyBleInfo(String name, String uuid, String rssi, String state){
        this.ble_name = name;
        this.ble_uuid = uuid;
        this.ble_rssi = rssi;
        this.ble_state = state;
    }

    public String getName(){
        return ble_name;
    }

    public String getUuid(){
        return ble_uuid;
    }

    public String getRssi(){
        return ble_rssi;
    }

    public String getState(){
        return ble_state;
    }

    public void setName(String name){
        this.ble_name = name;
    }

    public void setUuid(String uuid){
        this.ble_uuid = uuid;
    }

    public void setRssi(String rssi){
        this.ble_rssi = rssi;
    }

    public void setState(String state){
        this.ble_state = state;
    }
}
