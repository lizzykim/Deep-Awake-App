package com.example.semicolonapp.brain;

public class Constants {

    //----- Brain wave analysis
    public static final int EEG_RAW_DATA_LEN = 512;

    //----- Draw graph
    public static final int FREQ_DRAW_MODE_ALPHA_TO_GAMMA = 1;
    public static final int FREQ_DRAW_MODE_ALL_BAND = 2;

    public static final int FREQ_DRAW_MODE_MAX = 2;

    //----- Current View Mode
    public static final int VIEW_MODE_MONITORING = 1;
    public static final int VIEW_MODE_CONTROLLER = 2;

    //----- Arduino serial message
    public static final int MSG_DEVICE_COUNT = 7001;
    public static final int MSG_DEVICD_INFO = 7011;
    public static final int MSG_READ_DATA_COUNT = 7021;
    public static final int MSG_READ_DATA = 7022;
    public static final int MSG_SERIAL_ERROR = -7000;
    public static final int MSG_FATAL_ERROR_FINISH_APP = -7002;

    //----- Serial commands (Sending)
    public static final int SERIAL_CMD_MOVE = 60;
    public static final int SERIAL_CMD_BUTTON_PRESSED = 61;
    public static final int SERIAL_CMD_MIND_SIGNAL = 62;

//	public static final byte SERIAL_SUB_CMD_MOVE_STOP = 's';
//	public static final byte SERIAL_SUB_CMD_MOVE_FORWARD = 'f';
//	public static final byte SERIAL_SUB_CMD_MOVE_BACKWARD = 'b';
//	public static final byte SERIAL_SUB_CMD_MOVE_LEFT = 'l';
//	public static final byte SERIAL_SUB_CMD_MOVE_RIGHT = 'r';

    public static final int SERIAL_SUB_CMD_MOVE_NONE = 1;
    public static final int SERIAL_SUB_CMD_MOVE_STOP = 10;
    public static final int SERIAL_SUB_CMD_MOVE_FORWARD = 20;
    public static final int SERIAL_SUB_CMD_MOVE_BACKWARD = 30;
    public static final int SERIAL_SUB_CMD_MOVE_LEFT = 40;
    public static final int SERIAL_SUB_CMD_MOVE_RIGHT = 50;

    public static final byte SERIAL_SUB_CMD_BTN_1 = '1';
    public static final byte SERIAL_SUB_CMD_BTN_2 = '2';

    //----- Serial commands (Receiving)
    public static final byte SERIAL_CMD_DISPLAY_VALUE = 'a';
}
