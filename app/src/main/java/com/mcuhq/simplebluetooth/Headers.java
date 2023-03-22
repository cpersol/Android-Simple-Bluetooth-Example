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
    public Headers() {
        this.SET_APPKEY = "A2";
        this.GET_APPKEY = "A1";
        this.SET_DEVEUI = "A3";
        this.GET_DEVEUI = "A5";
        this.SET_TIME = "A4";
        this.GET_TIME = "A6";
        this.GET_GENERIC_INFO = "B0";
        this.GET_GENERIC_DATA = "00";

    }
}
