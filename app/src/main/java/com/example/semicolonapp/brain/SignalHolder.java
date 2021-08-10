package com.example.semicolonapp.brain;

import android.util.Log;

import com.neurosky.thinkgear.TGDevice;
import com.neurosky.thinkgear.TGEegPower;

import java.util.Arrays;
import java.util.LinkedList;

public class SignalHolder {
    private static final String tag = "SignalHolder";

    private static final int EMOTION_ARRAY_SIZE = 60*10;		// Records 10 minutes
    private static final int EEG_BAND_ARRAY_SIZE = 5;
    private static final int COMMAND_ARRAY_SIZE = 5;

    private static final int GAMMA_THRESHOLD = 100000;
    private static final int BLINK_ACCEPT_TIME = 1500;

    //----- EEG Signal Holder
    public LinkedList<double[]> mEEGRaw = new LinkedList<double[]>();
    public int mEEGRawCount = 0;
    public long mEEGRawStartTime = 0L;

    public LinkedList<TGEegPower> mEEGBand = new LinkedList<TGEegPower>();
    public int mEEGBandIndex = 0;
    public TGEegPower[] mEEGBandArray = new TGEegPower[EEG_BAND_ARRAY_SIZE];
    public int mGammaHistoryIndex = 0;
    private int[] mGammaHistory = new int[COMMAND_ARRAY_SIZE];
    public int mBlinkHistoryIndex = 0;
    private int[] mBlinkHistory = new int[COMMAND_ARRAY_SIZE];

    public int[] mAttention = new int[EMOTION_ARRAY_SIZE];
    public long mAttStartTime = 0L;
    public int mAttPrevOffset = -1;

    public int[] mMeditation = new int[EMOTION_ARRAY_SIZE];
    public long mMedStartTime = 0L;
    public int mMedPrevOffset = -1;

    public int[] mBlink = new int[EMOTION_ARRAY_SIZE];
    public long mBlkStartTime = 0L;
    public int mBlkPrevOffset = -1;

    public int[] mHeartRate = new int[EMOTION_ARRAY_SIZE];
    public long mHrtStartTime = 0L;
    public int mHrtPrevOffset = -1;

    public int[] mPoorSignal = new int[EMOTION_ARRAY_SIZE];
    public long mPoorStartTime = 0L;
    public int mPoorPrevOffset = -1;

    //----- Global
    private boolean mIsDumpEnabled = false;



    /*****************************************************
     *		Initialization methods
     ******************************************************/
    public SignalHolder() {
        // Initialize linked list
        mEEGBand.clear();
        mEEGBandIndex = 0;
        Arrays.fill(mEEGBandArray, null);
        mGammaHistoryIndex = 0;
        Arrays.fill(mGammaHistory, 0);
        mBlinkHistoryIndex = 0;
        Arrays.fill(mBlinkHistory, 0);

        // Initialize array
        for(int i=0; i<EMOTION_ARRAY_SIZE; i++)
            mAttention[i] = 0;
        for(int i=0; i<EMOTION_ARRAY_SIZE; i++)
            mMeditation[i] = 0;
        for(int i=0; i<EMOTION_ARRAY_SIZE; i++)
            mBlink[i] = 0;
        for(int i=0; i<EMOTION_ARRAY_SIZE; i++)
            mHeartRate[i] = 0;
        for(int i=0; i<EMOTION_ARRAY_SIZE; i++)
            mPoorSignal[i] = 0;
    }



    /*****************************************************
     *		Private methods
     ******************************************************/

    private void dumpData(int type, int[] array) {
        // TODO: save array data to DB or ...
        if(mIsDumpEnabled) {

        }

        if(array != null)
            Arrays.fill(array, 0);

        mEEGRaw.clear();
        mEEGBand.clear();
        mEEGBandIndex = 0;
        mEEGRawCount = 0;
    }


    /*****************************************************
     *		Public methods
     ******************************************************/
    public void enableDump(boolean isTrue) {
        mIsDumpEnabled = isTrue;
    }

    public double[] setEEGRawData(int[] raw) {
        if(raw==null) return null;

        double[] copy = new double[raw.length];

        for(int i=0; i<raw.length; i++)
            copy[i] = (double) raw[i];

        mEEGRaw.add(copy);
        mEEGRawCount++;
        if(mEEGRawCount == 1)
            mEEGRawStartTime = System.currentTimeMillis();

        return copy;
    }

    public void setEEGBandData(TGEegPower band) {
        mEEGBand.add(band);
        mEEGBandArray[mEEGBandIndex] = band;

        mEEGBandIndex++;
        if(mEEGBand.size() >= EMOTION_ARRAY_SIZE) {
            dumpData(TGDevice.MSG_EEG_POWER, null);
        }
        if(mEEGBandIndex >= EEG_BAND_ARRAY_SIZE) {
            mEEGBandIndex = 0;
        }
    }

    public void setAttention(int attention) {
        long cur_time = System.currentTimeMillis();

        // Init if it's first time.
        if(mAttPrevOffset == -1) {
            mAttStartTime = cur_time;
        }

        int offset = (int) ((cur_time - mAttStartTime)/1000);		// find offset

        // Fill 0 betweeen previous and current index
        for(int i=mAttPrevOffset+1; i<=offset; i++) {
            if(i >= EMOTION_ARRAY_SIZE)
                break;
            mAttention[i] = 0;
        }

        // Over-sized offset. Back to first index
        if(offset >= EMOTION_ARRAY_SIZE) {
            dumpData(TGDevice.MSG_ATTENTION, mAttention);
            mAttStartTime = cur_time;
            offset = 0;
        }

        // Write value
        mAttention[offset] = attention;
        mAttPrevOffset = offset;

    }

    public int getLatestAttention() {
        if(mAttPrevOffset < mAttention.length && mAttPrevOffset > -1)
            return mAttention[mAttPrevOffset];
        else
            return 0;
    }

    public void setMeditation(int meditation) {
        long cur_time = System.currentTimeMillis();

        // Init if it's first time.
        if(mMedPrevOffset == -1) {
            mMedStartTime = cur_time;
        }

        int offset = (int) ((cur_time - mMedStartTime)/1000);		// find offset

        // Fill 0 betweeen previous and current index
        for(int i=mMedPrevOffset+1; i<=offset; i++) {
            if(i >= EMOTION_ARRAY_SIZE)
                break;
            mMeditation[i] = 0;
        }

        // Over-sized offset. Back to first index
        if(offset >= EMOTION_ARRAY_SIZE) {
            dumpData(TGDevice.MSG_MEDITATION, mMeditation);
            mMedStartTime = cur_time;
            offset = 0;
        }

        // Write value
        mMeditation[offset] = meditation;
        mMedPrevOffset = offset;

    }

    public int getLatestMeditation() {
        if(mMedPrevOffset < mMeditation.length && mMedPrevOffset > -1)
            return mMeditation[mMedPrevOffset];
        else
            return 0;
    }

    public void setBlink(int blink) {
        long cur_time = System.currentTimeMillis();

        // Init if it's first time.
        if(mBlkPrevOffset == -1) {
            mBlkStartTime = cur_time;
        }

        int offset = (int) ((cur_time - mBlkStartTime)/1000);		// find offset

        // Fill 0 betweeen previous and current index
        for(int i=mBlkPrevOffset+1; i<=offset; i++) {
            if(i >= EMOTION_ARRAY_SIZE)
                break;
            mBlink[i] = 0;
        }

        // Over-sized offset. Back to first index
        if(offset >= EMOTION_ARRAY_SIZE) {
            dumpData(TGDevice.MSG_BLINK, mBlink);
            mBlkStartTime = cur_time;
            offset = 0;
        }

        // Write value
        mBlink[offset] = blink;
        mBlkPrevOffset = offset;

    }

    public int getLatestBlink() {
        if(mBlkPrevOffset < mBlink.length && mBlkPrevOffset > -1)
            return mBlink[mBlkPrevOffset];
        else
            return 0;
    }

    public void setHeartRate(int heart) {
        long cur_time = System.currentTimeMillis();

        // Init if it's first time.
        if(mHrtPrevOffset == -1) {
            mHrtStartTime = cur_time;
        }

        int offset = (int) ((cur_time - mHrtStartTime)/1000);		// find offset

        // Fill 0 betweeen previous and current index
        for(int i=mHrtPrevOffset+1; i<=offset; i++) {
            if(i >= EMOTION_ARRAY_SIZE)
                break;
            mHeartRate[i] = 0;
        }

        // Over-sized offset. Back to first index
        if(offset >= EMOTION_ARRAY_SIZE) {
            dumpData(TGDevice.MSG_HEART_RATE, mHeartRate);
            mHrtStartTime = cur_time;
            offset = 0;
        }

        // Write value
        mHeartRate[offset] = heart;
        mHrtPrevOffset = offset;

    }

    /**
     * Save poor signal value
     * @param	//int poor	Signal value
     */
    public void setPoorSignal(int poor) {
        long cur_time = System.currentTimeMillis();

        // Init if it's first time.
        if(mPoorPrevOffset == -1) {
            mPoorStartTime = cur_time;
        }

        int offset = (int) ((cur_time - mPoorStartTime)/1000);		// find offset

        // Fill 0 betweeen previous and current index
        for(int i=mPoorPrevOffset+1; i<=offset; i++) {
            if(i >= EMOTION_ARRAY_SIZE)
                break;
            mPoorSignal[i] = 0;
        }

        // Over-sized offset. Back to first index
        if(offset >= EMOTION_ARRAY_SIZE) {
            dumpData(TGDevice.MSG_HEART_RATE, mPoorSignal);
            mPoorStartTime = cur_time;
            offset = 0;
        }

        // Write value
        mPoorSignal[offset] = poor;
        mPoorPrevOffset = offset;

    }


    private static final int COMMAND_INTERVAL = 1500;
    private static final int CONTINUOUS_GAMMA_THRESHOLD = 4;
    private static final int CONTINUOUS_BLINK_THRESHOLD = 3;
    private long mCommandFinishTime = 0L;
    private int mLastCommand = Constants.SERIAL_SUB_CMD_MOVE_STOP;

    /**
     * Analyze recent EEG Band data and return command to execute.
     * @return	command
     */
    public int makeMoveCommand() {
        int command = Constants.SERIAL_SUB_CMD_MOVE_NONE;
        long cur_time = System.currentTimeMillis();

        setGammaSignalHistory();
        setBlinkSignalHistory();


        // boolean isContinuousBlink = checkBlinkSignalHistory();
        int blink_count = checkContinuousBlinkSignalReceived();
        int gamma_pattern = checkGammaSignalHistory();

        //if( gamma_pattern >= CONTINUOUS_GAMMA_THRESHOLD ) {
        if( blink_count >= CONTINUOUS_BLINK_THRESHOLD ) {
            command = Constants.SERIAL_SUB_CMD_MOVE_FORWARD;
            mLastCommand = command;
            mCommandFinishTime = cur_time;
        }
        else {
            command = Constants.SERIAL_SUB_CMD_MOVE_STOP;
            mLastCommand = command;
            mCommandFinishTime = cur_time;
            resetBlinkSignalHistory();
        }

		/*
		// 1. Left turn (4 continuous high-gamma signal)
		int gamma_pattern = checkGammaSignalHistory();
		if(gamma_pattern > 1 && mLastCommand != Constants.SERIAL_SUB_CMD_MOVE_FORWARD ) {
			if( gamma_pattern >= CONTINUOUS_GAMMA_THRESHOLD
					 && mLastCommand == Constants.SERIAL_SUB_CMD_MOVE_STOP ) {
				command = Constants.SERIAL_SUB_CMD_MOVE_LEFT;
				mLastCommand = command;
				mCommandFinishTime = cur_time;
			}
			else if(gamma_pattern < CONTINUOUS_GAMMA_THRESHOLD
					 && mLastCommand == Constants.SERIAL_SUB_CMD_MOVE_LEFT) {
				command = Constants.SERIAL_SUB_CMD_MOVE_STOP;
				mLastCommand = command;
				mCommandFinishTime = cur_time;
			}
			resetBlinkSignalHistory();
		}
		else {
			if(mLastCommand == Constants.SERIAL_SUB_CMD_MOVE_LEFT) {
				command = Constants.SERIAL_SUB_CMD_MOVE_STOP;
				mLastCommand = command;
				mCommandFinishTime = cur_time;
			}
			else {
				if( checkBlinkSignalHistory() ) {
					if(mLastCommand == Constants.SERIAL_SUB_CMD_MOVE_STOP && mCommandFinishTime + COMMAND_INTERVAL < cur_time) {
						command = Constants.SERIAL_SUB_CMD_MOVE_FORWARD;
						mLastCommand = command;
						mCommandFinishTime = cur_time;
						resetBlinkSignalHistory();
					}
					else if(mLastCommand == Constants.SERIAL_SUB_CMD_MOVE_FORWARD && mCommandFinishTime + COMMAND_INTERVAL < cur_time) {
						command = Constants.SERIAL_SUB_CMD_MOVE_STOP;
						mLastCommand = command;
						mCommandFinishTime = cur_time;
						resetBlinkSignalHistory();
					}
				}
				//else if(mLastCommand == Constants.SERIAL_SUB_CMD_MOVE_FORWARD && mCommandFinishTime + COMMAND_INTERVAL < cur_time) {
				else if(mLastCommand == Constants.SERIAL_SUB_CMD_MOVE_FORWARD && gamma_pattern > 3) {
					command = Constants.SERIAL_SUB_CMD_MOVE_STOP;
					mLastCommand = command;
					mCommandFinishTime = cur_time;
					resetBlinkSignalHistory();
				}
			}
		}
		*/

        Log.d(tag, "# COMMAND=" + command);

        return command;
    }

    private int checkGammaSignalHistory() {
        int count = 0;
        for(int i=0; i<mGammaHistory.length; i++) {
            if( mGammaHistory[i] > 0 ) {
                count++;
            }
        }

        Log.d(tag, "# Gamma count = " + count);

        return count;
    }

    private void setGammaSignalHistory() {
        //int[] max_signal = new int[6];
        //int[] min_signal = new int[6];
        int[] avg_signal = new int[6];
        //Arrays.fill(max_signal, 0);
        //Arrays.fill(min_signal, 0);
        Arrays.fill(avg_signal, 0);

        for(int i=0; i<mEEGBandArray.length; i++) {
            TGEegPower power = mEEGBandArray[i];
            if(power == null)
                continue;
            //avg_signal[0] += power.lowAlpha;
            //avg_signal[1] += power.highAlpha;
            //avg_signal[2] += power.lowBeta;
            //avg_signal[3] += power.highBeta;
            avg_signal[4] += power.lowGamma;
            avg_signal[5] += power.midGamma;
        }

        //avg_signal[0] /= mEEGBandArray.length;		// Low alpha
        //avg_signal[1] /= mEEGBandArray.length;		// High alpha
        //avg_signal[2] /= mEEGBandArray.length;		// Low beta
        //avg_signal[3] /= mEEGBandArray.length;		// High beta
        avg_signal[4] /= mEEGBandArray.length;		// Low gamma
        avg_signal[5] /= mEEGBandArray.length;		// Mid gamma

        //int avgAlpha = (avg_signal[0] + avg_signal[1]) / 2;
        //int avgBeta = (avg_signal[2] + avg_signal[3]) / 2;
        int avgGamma = (avg_signal[4] + avg_signal[5]) / 2;

        if( avgGamma > GAMMA_THRESHOLD ) {
            mGammaHistory[mGammaHistoryIndex] = avgGamma;
            mGammaHistoryIndex++;
        } else {
            mGammaHistory[mGammaHistoryIndex] = 0;
            mGammaHistoryIndex++;
        }
        if(mGammaHistoryIndex >= mGammaHistory.length)
            mGammaHistoryIndex = 0;
    }

    private boolean checkBlinkSignalHistory() {
        int count = 0;
        for(int i=0; i<mBlinkHistory.length; i++) {
            if( mBlinkHistory[i] > 0 ) {
                count++;
            }
        }

        Log.d(tag, "# Blink count = " + count);

        return (count >= CONTINUOUS_BLINK_THRESHOLD ? true : false);
    }

    private void setBlinkSignalHistory() {
        boolean isBlink = checkBlinkSignalReceived();

        if(isBlink) {
            mBlinkHistory[mBlinkHistoryIndex] = 1;
        } else {
            mBlinkHistory[mBlinkHistoryIndex] = 0;
        }

        mBlinkHistoryIndex++;
        if(mBlinkHistoryIndex >= mBlinkHistory.length)
            mBlinkHistoryIndex = 0;
    }

    private boolean checkBlinkSignalReceived() {
        boolean isTrue = false;
        long cur_time = System.currentTimeMillis();

        // There is blink signal within a second
        if( mBlkStartTime + mBlkPrevOffset*1250 + BLINK_ACCEPT_TIME > cur_time ) {
            isTrue = true;
        }

        return isTrue;
    }

    private int checkContinuousBlinkSignalReceived() {
        long cur_time = System.currentTimeMillis();

        int offset = (int) ((cur_time - 5000 - mBlkStartTime)/1000);		// find offset
        if(offset < 0) offset = 0;
        if(offset > mBlink.length - 1) offset = mBlink.length - 1;

        int count = 0;
        for(int i=offset; i<mBlink.length && i<=mBlkPrevOffset; i++) {
            if(mBlink[i] > 0)
                count++;
        }

        return count;
    }

    private void resetBlinkSignalHistory() {
        mBlinkHistoryIndex = 0;
        Arrays.fill(mBlinkHistory, 0);
    }

    private boolean checkAttentionSignal() {
        boolean isTrue = false;
        if(mAttPrevOffset > -1 && mAttention[mAttPrevOffset] > 70) {
            isTrue = true;
        }

        return isTrue;
    }










}
