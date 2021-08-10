package com.example.semicolonapp.brain;//package com.example.mindwake;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.semicolonapp.MainActivity;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

//import coresignal.CoreSignalMain.SerialListener;

public class SerialConnector {

    public static final String tag = "SerialConnector";

    private Context mContext;
    private MainActivity.SerialListener mListener;
    private Handler mHandler;

    private SerialMonitorThread mSerialThread;

    private UsbDevice mDevice;
    private UsbSerialDriver mDriver;
    private UsbDeviceConnection mDevConn;

    private static final int READ_WAIT_MILLIS = 500;
    private static final int BUFSIZ = 4096;
    private final ByteBuffer mReadBuffer = ByteBuffer.allocate(BUFSIZ);
    //	private final ByteBuffer mWriteBuffer = ByteBuffer.allocate(BUFSIZ);
    private byte[] mWriteBuffer = null;

    public static final int TARGET_VENDOR_ID = 9025;
    public static final int BAUD_RATE = 9600;


    /*****************************************************
     *	Constructor, Initialize
     ******************************************************/
    public SerialConnector(Context c, MainActivity.SerialListener l, Handler h) {
        mContext = c;
        mListener = l;
        mHandler = h;
    }


    public void initialize() {
        UsbManager manager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();

        Log.d(tag, "Device Count : " + deviceList.size());
        mListener.onReceive(Constants.MSG_DEVICE_COUNT, deviceList.size(), 0, null, null);

        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while(deviceIterator.hasNext()){
            StringBuilder sb = new StringBuilder();
            UsbDevice device = deviceIterator.next();
            sb.append(" DName : ").append(device.getDeviceName()).append("\n")
                    .append(" DID : ").append(device.getDeviceId()).append("\n")
                    .append(" VID : ").append(device.getVendorId()).append("\n")
                    .append(" PID : ").append(device.getProductId()).append("\n")
                    .append(" IF Count : ").append(device.getInterfaceCount()).append("\n");
            mListener.onReceive(Constants.MSG_DEVICD_INFO, 0, 0, sb.toString(), null);

            if( device.getVendorId() == TARGET_VENDOR_ID) {
                mDevice = device;		// This is target device
            }
        }

        if(mDevice != null ) {
            try {
                // WARNING!!
                // If you've got the exception about you have no permission to this device.
                // Check /xml/device_filter.xml
                mDriver =  UsbSerialProber.acquire(manager, mDevice);
            }
            catch(Exception allEx) {
                // User has not given permission to current device
                mListener.onReceive(Constants.MSG_SERIAL_ERROR, 0, 0, "Fatal Error : User has not given permission to current device. \n", null);
                return;
            }


            if(mDriver != null) {
                try {
                    mDriver.open();
                    mDriver.setBaudRate(BAUD_RATE);
                }
                catch (IOException e) {
                    mListener.onReceive(Constants.MSG_SERIAL_ERROR, 0, 0, "Error 1: " + e.toString() + "\n", null);
                    return;
                }
                catch (Exception allEx) {
                    mListener.onReceive(Constants.MSG_SERIAL_ERROR, 0, 0, "Error 2: " + allEx.toString() + "\n", null);
                    return;
                }
                finally {
                }

                // Everything is fine. Start serial monitoring thread.
                startThread();

            } else {
                mListener.onReceive(Constants.MSG_SERIAL_ERROR, 0, 0, "Error 3: Driver is Null \n", null);
            }
        }
    }	// End of initialize()

    public void finalize() {
        try {
            if(mDriver != null)
                mDriver.close();
            mDevice = null;
            mDriver = null;

            stopThread();
        } catch(Exception ex) {
            mListener.onReceive(Constants.MSG_SERIAL_ERROR, 0, 0, "Error 4: " + ex.toString() + "\n", null);
        }
    }



    /*****************************************************
     *	public methods
     ******************************************************/
    public void sendCommand(SerialCommand cmd) {

        if(mDriver == null)
            return;

        if(cmd != null) {
            byte[] buffer = new byte[cmd.mWriteByte];
            Arrays.fill(buffer, (byte) 0x01);

            for(int i=0; i<cmd.mWriteByte && i<SerialCommand.SIZE_IN_BYTE; i++) {
                buffer[i] = (byte)cmd.mDataArray[i];
            }

            writeAsync(buffer);

//			try {
//				mDriver.write(buffer, 1000);		// (buffer, timeoutMillis)
//				mDriver.notify();
//			}
//			catch(IOException e) {
//				mListener.onReceive(Constants.MSG_SERIAL_ERROR, 0, 0, "Failed in sending command. : IO Exception \n", null);
//			}
        }


        // WARNING !!
        // mDriver must be locked by thread that calls Driver.notify()
        // or FATAL error will occur
//		synchronized(mDriver) {
//
//		}
    }

    public void writeAsync(byte[] data) {
        try {
            if(mWriteBuffer == null) {
                mWriteBuffer = data;
            }
        } catch(Exception e) {
            mListener.onReceive(Constants.MSG_SERIAL_ERROR, 0, 0, "Error in writeAsync: " + e.toString() + "\n", null);
        }
    }


    /*****************************************************
     *	private methods
     ******************************************************/
    private void startThread() {
        Log.d(tag, "Start serial monitoring thread");
        mListener.onReceive(Constants.MSG_SERIAL_ERROR, 0, 0, "Start serial monitoring thread \n", null);
        if(mSerialThread == null) {
            mSerialThread = new SerialMonitorThread();
            mSerialThread.start();
        }
    }

    private void stopThread() {
        if(mSerialThread != null && mSerialThread.isAlive())
            mSerialThread.interrupt();
        if(mSerialThread != null) {
            mSerialThread.setKillSign(true);
            mSerialThread = null;
        }
    }





    /*****************************************************
     *	Sub classes, Handler, Listener
     ******************************************************/

    public class SerialMonitorThread extends Thread {
        // Thread status
        private boolean mKillSign = false;



        private void initializeThread() {
            // This code will be executed only once.
        }

        private void finalizeThread() {
        }

        public void setKillSign(boolean isTrue) {
            mKillSign = isTrue;
        }

        /**
         *	Main loop
         **/
        @Override
        public void run()
        {
            SerialCommand cmd = new SerialCommand();

            boolean writeInArray = false;
            int index_cur = 0;

            while(!Thread.interrupted())
            {
                if(mDriver != null) {

                    try {
                        // Handle incoming data.
                        int numBytesRead = mDriver.read(mReadBuffer.array(), READ_WAIT_MILLIS);
                        if (numBytesRead > 0) {
                            //Log.d(tag, "run : read bytes = " + numBytesRead);
                            //Message msg = mHandler.obtainMessage(Constants.MSG_READ_DATA_COUNT, numBytesRead, 0, null);
                            //mHandler.sendMessage(msg);

                            final byte[] data = new byte[numBytesRead];
                            mReadBuffer.get(data, 0, numBytesRead);

                            for(int i=0; i<numBytesRead; i++) {
                                // Check if buffer contains pre-defined message ID
                                if(writeInArray == false && data[i] == Constants.SERIAL_CMD_DISPLAY_VALUE) {
                                    writeInArray = true;
                                    index_cur = 0;
                                }

                                // copy data from buffer
                                if(writeInArray) {
                                    cmd.mDataArray[index_cur] = (int)data[i];
                                    index_cur++;
                                    if(index_cur >= cmd.mReadByte) {
                                        index_cur = 0;
                                        writeInArray = false;
                                        Message msg1 = mHandler.obtainMessage(Constants.MSG_READ_DATA, 0, 0, cmd);
                                        mHandler.sendMessage(msg1);
                                        cmd = new SerialCommand();
                                    }
                                }
                            }

                            mReadBuffer.clear();
                        }

                        // Handle outgoing data.
                        byte[] outBuff = null;

                        if (mWriteBuffer != null) {
                            synchronized (mWriteBuffer) {
                                mDriver.write(mWriteBuffer, READ_WAIT_MILLIS);
								/*
				                StringBuilder sb = new StringBuilder();
				                for(int i=0; i<mWriteBuffer.length; i++)
				                	sb.append(mWriteBuffer[i]);
								Message msg2 = mHandler.obtainMessage(Constants.MSG_SERIAL_ERROR, 0, 0, "# Serial connector : "+sb.toString()+" \n");
								mHandler.sendMessage(msg2);
								*/
                                mWriteBuffer = null;
                            }
                        }
                    }
                    catch (IOException e) {
                        Log.d(tag, "IOException - mDriver.read");
                        Message msg = mHandler.obtainMessage(Constants.MSG_SERIAL_ERROR, 0, 0, "Error # run: " + e.toString() + "\n");
                        mHandler.sendMessage(msg);
                        // mKillSign = true;
                    }

                }	// End of if(mDriver != null)


//				try {
//					Thread.sleep(100);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//					break;
//				}


                if(mKillSign)
                    break;

            }	// End of while() loop

            // Finalize
            finalizeThread();

        }	// End of run()


    }	// End of SerialMonitorThread




}
