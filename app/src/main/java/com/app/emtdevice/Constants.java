package com.app.emtdevice;

public class Constants {

    public static final String SERVER_IP = "10.0.0.1"; //server IP address
    public static final int SERVER_PORT = 45123;
    public static final String IMAGE_FILEPATH = "/sdcard/emtDevice";
    public static final String IMAGE_NAME = "/image_from_android.bmp";
    public static final String IMAGE_ZIPNAME = "/image_from_android.zip";

    public static int INTERVALGETLINE = 200;
    public static int INTERVALRECONNECT = 5000;
    public static int INTERVAL_TIMEOUT = 3000;// when response not get from pi
    public final static int SOCKET_TIMEOUT = 5000;

    public static final byte GET_BMP_IMAGE = 01;
    public static final byte GET_APP_STATE = 02;
    public static final byte GET_CURRENT_LINE = 03;
    public static final byte SET_BMP_IMAGE = 10;
    public static final byte LOAD_BMP_IMAGE = 11;
    public static final byte PROCESS_BMP_IMAGE = 12;

    public static final byte SET_CURRENT_LINE = 13;
    public static final byte SET_TIME_OUT = 14;
    public static final byte GET_TIME_OUT = 15;
    public static final byte GET_MAX_LINE_NUMBER = 16;
    public static final byte REPEAT_DATA = 17;
    public static final byte GET_PI_LOG = 18;
    public static final byte GET_PI_KERNAL_LOG = 19;
    public static final byte GET_EQUPMENT_FIRST_STATUS = 20;
    public static final byte SET_EQUPMENT_FIRST_STATUS = 21;
    public static final byte GET_NO_OF_EQUPMENT_PER_LINE = 22;
    public static final byte SET_NO_OF_EQUPMENT_PER_LINE = 23;
    public static final byte RESET_DEVICE_APPLICATION = 24;
    public static final byte GET_MD5_SUM = 25;

    public static final byte NEXT_ACTION = 30;
    public static final byte SUCCESS = 55;
    public static final byte FAIL = 77;

    public static final int APP_DEFAULT_STATE = 20;
    public static final int APP_GOT_IMAGE_STATE = 21;
    public static final int APP_LOAD_IMAGE_STATE = 22;
    public static final int APP_PROCESS_IMAGE_STATE = 23;


    public static final byte ANDROID_APP_DEFAULT_STATE = 120;
    public static final byte ANDROID_APP_GOT_IMAGE_STATE = 121;
    public static final byte ANDROID_APP_LOAD_IMAGE_STATE = 122;
    public static final byte ANDROID_APP_PROCESS_IMAGE_STATE = 123;
    public static final byte ANDROID_APP_DISCONECT = 124;
    public static final byte ANDROID_CONTINUE = 125;


    public static final int ENABLED_EQUIPMENT_TIME_OUT = 1;
    public static final int DISABLE_EQUIPMENT_TIME_OUT = 2;


    public static String piLog = "";
}
