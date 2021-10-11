package com.example.semicolonapp.data;

public class DataHolder {

    private static String useremail;
    private static String phonenumber;
    private static String isSendSMS;

    public static String getPhonenumber() {
        return phonenumber;
    }

    public static void setPhonenumber(String phonenumber) {
        DataHolder.phonenumber = phonenumber;
    }

    public static String getUseremail() {
        return useremail;
    }

    public static void setUseremail(String useremail) {
        DataHolder.useremail = useremail;
    }

    public static String getIsSendSMS() {
        return isSendSMS;
    }

    public static void setIsSendSMS(String isSendSMS) {
        DataHolder.isSendSMS = isSendSMS;
    }
}
