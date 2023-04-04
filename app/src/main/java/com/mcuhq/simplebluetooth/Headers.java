package com.mcuhq.simplebluetooth;

public class Headers {
    public static  String GET_GENERIC_DATA = "";
    public static String SET_APPKEY = "";
    public static String GET_APPKEY = "";
    public static String SET_DEVEUI = "";
    public static String GET_DEVEUI = "";
    public static String SET_TIME = "";
    public static String GET_TIME = "";
    public static String GET_GENERIC_INFO="";
    public static String GET_DEVICE_INFO="";

    public Headers() {
        this.GET_GENERIC_DATA = "00";
        this.GET_GENERIC_INFO = "A0";
        this.SET_APPKEY = "A1";
        this.SET_DEVEUI = "A2";
        this.SET_TIME = "A3";
        this.GET_DEVICE_INFO = "B0";






    }
}
