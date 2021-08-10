package com.example.semicolonapp.brain;

import java.util.Arrays;

public class SerialCommand {

    /**
     //---------- buffer
     mDataArray[0] : msg type
     mDataArray[1] : Up button
     mDataArray[2] : Down button
     mDataArray[3] : Left button
     mDataArray[4] : Right button
     mDataArray[5] : A button
     mDataArray[6] : B button
     mDataArray[7] : Extra 1
     mDataArray[8] : Extra 2
     */


    public static final int SIZE_IN_BYTE = 9;

    public int[] mDataArray;
    public String mMsgString;

    public int mWriteByte = SIZE_IN_BYTE;		// How many byte will be written on sending buffer
    public int mReadByte = SIZE_IN_BYTE;		// How many byte will be read from received buffer



    public SerialCommand() {
        mDataArray = new int[SIZE_IN_BYTE];
        Arrays.fill(mDataArray, 1);
    }



    public void setCommand(int msgType, int index, int value) {
        if(msgType > 0) {
            mDataArray[0] = msgType;
        }
        // Index 0 is reserved for message type
        if(0 < index && index < SIZE_IN_BYTE) {
            mDataArray[index] = value;
        }
    }

    public void setCommand(int msgType, int[] value) {
        if(msgType > 0) {
            mDataArray[0] = msgType;
        }
        // Index 0 is reserved for message type
        for(int i=0; i<SIZE_IN_BYTE - 1 && i<value.length; i++) {
            mDataArray[i+1] = value[i];
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Received: ");
        for(int i=0; i<mDataArray.length; i++)
            sb.append(i).append("=").append(mDataArray[i]).append(", ");
        if(mMsgString != null)
            sb.append(mMsgString);

        sb.append("\n");
        return sb.length()>0 ? sb.toString() : "No data \n";
    }
}
