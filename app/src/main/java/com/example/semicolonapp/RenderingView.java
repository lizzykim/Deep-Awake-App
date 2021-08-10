package com.example.semicolonapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.neurosky.thinkgear.TGEegPower;

import java.util.ArrayList;

/**
 * Created by 지연 on 2016-06-18.
 */
public class RenderingView extends View {


    private static final String tag = "RenderingView";

    private Context mContext;

    private int mViewW = 0;
    private int mViewH = 0;

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mPaint;
    private Path mPath;
    private MaskFilter mEmboss;
    private MaskFilter  mBlur;



    /*****************************************************
     *		Initialization methods
     ******************************************************/
    // 속성이 없는 생성자는 소스상에서 직접 생성할때만 쓰인다.  //
    public RenderingView(Context context) {
        super(context);
    }
    /*
     *  리소스 xml 파일에서 정의하면 이 생성자가 사용된다.
     *
     *  대부분 this 를 이용해 3번째 생성자로 넘기고 모든 처리를 3번째 생성자에서 한다.
     */
    public RenderingView(Context context, AttributeSet attrs) {
        super(context,attrs);
    }

    /*
     * xml 에서 넘어온 속성을 멤버변수로 셋팅하는 역할을 한다.
     */
    public RenderingView(Context context, AttributeSet attrs, int defStyle) {
        super(context,attrs,defStyle);
    }



    public void setBitmap(Bitmap bitmap){
        mBitmap = bitmap;
    }


    /*****************************************************
     *		Override methods
     ******************************************************/
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(mBitmap != null)
            canvas.drawBitmap(mBitmap, 0, 0, null);
    }

    /*
     * xml 로 부터 모든 뷰를 inflate 를 끝내고 실행된다.
     *
     * 대부분 이 함수에서는 각종 변수 초기화가 이루어 진다.
     *
     * super 메소드에서는 아무것도 하지않기때문에 쓰지 않는다.
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setClickable(true);
    }


    /*****************************************************
     *		Private methods
     ******************************************************/
    private int mCurrentDrawingX = 1 + POINT_WIDTH_HALF; //3
    private int mPrevValueUpdateTime = 0;

    private static final int POINT_WIDTH = 5;		// must be odd number.
    private static final int POINT_THICKNESS = 5;	// must be odd number.
    private static final int POINT_WIDTH_HALF = 2;
    private static final int POINT_THICKNESS_HALF = 2;

    private static final int FREQ_POINT_WIDTH = 9;		// must be odd number.
    private static final int FREQ_POINT_THICKNESS = 9;	// must be odd number.
    private static final int FREQ_POINT_WIDTH_HALF = 4;
    private static final int FREQ_POINT_THICKNESS_HALF = 4;

    private static final int GRID_SIZE = 5; 	// how many point are include in one grid unit
    private static final int TIME_UNIT = 1000;	// Each point of graph represents 1 second
    //private static final int TIME_UNIT = 100;	// Each point of graph represents 0.1초로 바꿔보기


    private static final int VERTICAL_SCALE_VALUE = 2;

    private static final int TYPE_ATTENTION = 1;
    private static final int TYPE_MEDITATION = 2;
    private static final int TYPE_BLINK = 3;
    private static final int TYPE_HEART_RATE = 4;
    private static final int TYPE_POOR_SIGNAL = 5;

    private static final int TYPE_BAND_DELTA = 1;
    private static final int TYPE_BAND_THETA = 2;
    private static final int TYPE_BAND_LOW_ALPHA = 3;
    private static final int TYPE_BAND_HIGH_ALPHA = 4;
    private static final int TYPE_BAND_LOW_BETA = 5;
    private static final int TYPE_BAND_HIGH_BETA = 6;
    private static final int TYPE_BAND_LOW_GAMMA = 7;
    private static final int TYPE_BAND_MID_GAMMA = 8;

    private void resetGraphics() {
        mCanvas.drawColor(Color.WHITE);
        mCurrentDrawingX = 1 + POINT_WIDTH_HALF;
    }

    private void moveTimeLine() {
        int howManyPointInScreen = mViewW / POINT_WIDTH;
        int cutPoint = howManyPointInScreen / GRID_SIZE / 3;		// must be multiple of GRID_SIZE and cut 1/n

        // Cut image from original
        Bitmap bCut  = Bitmap.createBitmap(mBitmap,
                mCurrentDrawingX - POINT_WIDTH - cutPoint*5*POINT_WIDTH, 0,
                cutPoint*5*POINT_WIDTH, mViewH);

        mCanvas.drawColor(Color.WHITE);
        // mCanvas.drawGrid();
        mCanvas.drawBitmap(bCut, 0, 0, null);

        mCurrentDrawingX = 1 + POINT_WIDTH_HALF + cutPoint*5*POINT_WIDTH;
        bCut = null;
    }

    private void drawRawData(double[] rawData) {
        if(rawData == null)
            return;

        mCanvas.drawColor(Color.WHITE);
        // mCanvas.drawRawGrid();

        // Find max val
//		double max = 0f;
//		for(int i=0; i < rawData.length && i < mViewW; i++) {
//			if(rawData[i] > max) max = rawData[i];
//		}

        double scale = 1f / mViewH;

        mPaint.setColor(0xFF777777);
        for(int i=2; i < rawData.length && i < mViewW; i+=2) {
            mCanvas.drawLine(i, mViewH, i, mViewH - (int)(rawData[i]/scale), mPaint);
        }
    }

    private void drawFrequencyBand(int type, int row, int value) {

        switch(type) {
            case TYPE_BAND_DELTA:
                mPaint.setColor(0xFF00FF00);	// Green
                break;
            case TYPE_BAND_THETA:
                mPaint.setColor(0xFF00AA00);
                break;
            case TYPE_BAND_LOW_ALPHA:
                mPaint.setColor(0xFF0000FF);	// Blue
                break;
            case TYPE_BAND_HIGH_ALPHA:
                mPaint.setColor(0xFF0000AA);
                break;
            case TYPE_BAND_LOW_BETA:
                mPaint.setColor(0xFFFF0000);	// Red
                break;
            case TYPE_BAND_HIGH_BETA:
                mPaint.setColor(0xFFAA0000);
                break;
            case TYPE_BAND_LOW_GAMMA:
                mPaint.setColor(0xFFAAAAAA);	// Gray
                break;
            case TYPE_BAND_MID_GAMMA:
                mPaint.setColor(0xFF777777);
                break;
        }

        float fValue;
        if(type == TYPE_BAND_DELTA)
            fValue = value / 100000;
        else
            fValue = value / 500;

        if(fValue >= mViewH)
            fValue = fValue -1;

        // Log.d("coresignal", "# Value = "+fValue);

        if(POINT_THICKNESS == 1) {
            mCanvas.drawLine(row - POINT_WIDTH_HALF, fValue,
                    row + POINT_WIDTH_HALF, fValue,
                    mPaint);
        } else {
            mCanvas.drawRect(row - POINT_WIDTH_HALF, mViewH - fValue + POINT_THICKNESS_HALF,
                    row + POINT_WIDTH_HALF, mViewH - fValue - POINT_THICKNESS_HALF,
                    mPaint);
        }
    }

    private void drawRelativePower(TGEegPower power) {
        if(power == null){
            Log.d("power", "power는 null");
            return;
        }


//		int[] colorArray = new int[9];
//		colorArray[0] = 0xFF000000;
//		colorArray[1] = 0xFF00FF00;
//		colorArray[2] = 0xFF00AA00;
//		colorArray[3] = 0xFF0000FF;
//		colorArray[4] = 0xFF0000AA;
//		colorArray[5] = 0xFFFF0000;
//		colorArray[6] = 0xFFAA0000;
//		colorArray[7] = 0xFFAAAAAA;
//		colorArray[8] = 0xFF777777;

        int[] colorArray = new int[5];
        colorArray[0] = 0xFF555555;
        colorArray[1] = 0xFF999999;
        colorArray[2] = 0xFFFF0000;
        colorArray[3] = 0xFF00FF00;
        colorArray[4] = 0xFF0000FF;


        // Make sum except delta
        float sum = /* power.theta + */ power.highAlpha + power.lowAlpha + power.highBeta + power.lowBeta + power.midGamma + power.lowGamma;

        // Calculate each relative power
        /**
         * Theta power is too big. Do not sum theta.
         float relativeTheta = 0f;
         relativeTheta = (power.theta / sum) * 100;
         */
        float relativeLowAlpha = (power.lowAlpha / sum) * 100;
        float relativeHighAlpha = (power.highAlpha / sum) * 100;

        float relativeLowBeta = (power.lowBeta / sum) * 100;
        float relativeHighBeta = (power.highBeta / sum) * 100;

        float relativeLowGamma = (power.lowGamma / sum) * 100;
        float relativeMidGamma = (power.midGamma / sum) * 100;

//		float[] relativeValue = new float[9];
//		relativeValue[0] = 0f;
//		relativeValue[1] = power.delta;
//		relativeValue[2] = power.theta;
//		relativeValue[3] = relativeLowAlpha;
//		relativeValue[4] = relativeHighAlpha;
//		relativeValue[5] = relativeLowBeta;
//		relativeValue[6] = relativeHighBeta;
//		relativeValue[7] = relativeLowGamma;
//		relativeValue[8] = relativeMidGamma;

        float[] relativeValue = new float[5];
        relativeValue[0] = power.delta;
        relativeValue[1] = power.theta;
        // relativeValue[2] = relativeLowAlpha+relativeHighAlpha;
        relativeValue[2] = power.lowAlpha+power.highAlpha;
        // relativeValue[3] = relativeLowBeta+relativeHighBeta;
        relativeValue[3] = power.lowBeta+power.highBeta;
        // relativeValue[4] = relativeLowGamma+relativeMidGamma;
        relativeValue[4] = power.lowGamma+power.midGamma;

        // Calculate scale, drawing block size
        int elementCount = 5;
        int offset = mViewH / elementCount;
        float scale = (float)offset / 1000000 ; // 100f;
        float scale2 = (float)offset / 30000000f;

        // Draw
        int drawingBottom = 0;
        //for(int i=relativeValue.length - 1; i>2; i--) {
        for(int i=relativeValue.length - 1; i>-1; i--) {
//            Log.d(tag, "colorArray[0] :" + colorArray[0]);
//            Log.d(tag, "colorArray[1] :" + colorArray[1]);
//            Log.d(tag, "colorArray[2] :" + colorArray[2]);
//            Log.d(tag, "colorArray[3] :" + colorArray[3]);
//            Log.d(tag, "colorArray[4] :" + colorArray[4]);
            mPaint.setColor(colorArray[i]);

            if(i<2) {
                if( relativeValue[i]*scale2 > offset ) {
                    mCanvas.drawRect(mCurrentDrawingX - FREQ_POINT_WIDTH_HALF, mViewH - drawingBottom - offset,
                            mCurrentDrawingX + FREQ_POINT_WIDTH_HALF, mViewH - drawingBottom,
                            mPaint);
                }
                else {
                    mCanvas.drawRect(mCurrentDrawingX - FREQ_POINT_WIDTH_HALF, mViewH - drawingBottom - relativeValue[i]*scale2,
                            mCurrentDrawingX + FREQ_POINT_WIDTH_HALF, mViewH - drawingBottom,
                            mPaint);
                }

            }
            else {
                if(relativeValue[i]*scale > offset) {
                    mCanvas.drawRect(mCurrentDrawingX - FREQ_POINT_WIDTH_HALF, mViewH - drawingBottom - offset,
                            mCurrentDrawingX + FREQ_POINT_WIDTH_HALF, mViewH - drawingBottom,
                            mPaint);
                } else {
                    mCanvas.drawRect(mCurrentDrawingX - FREQ_POINT_WIDTH_HALF, mViewH - drawingBottom - relativeValue[i]*scale,
                            mCurrentDrawingX + FREQ_POINT_WIDTH_HALF, mViewH - drawingBottom,
                            mPaint);
                }

            }

            drawingBottom += offset;
        }
    }

    private void drawValue(int type, int row, int value) {
        if(value + POINT_THICKNESS_HALF > mViewH)
            value = mViewH - POINT_THICKNESS_HALF;
        else if(value < 1)		// This means there is no valid data
            return;

        switch(type) {
            case TYPE_ATTENTION:
                mPaint.setColor(0xFFFF0000);	// Green
                break;
            case TYPE_MEDITATION:
                mPaint.setColor(0xFF0000FF);	// Blue
                break;
            case TYPE_BLINK:
                mPaint.setColor(0xFF00CC00);	// Red
                break;
            case TYPE_HEART_RATE:
                mPaint.setColor(0xFF444444);
                break;
            case TYPE_POOR_SIGNAL:
                mPaint.setColor(0xFFAAAAAA);
                break;
        }

        if(POINT_THICKNESS == 1) {
            mCanvas.drawLine(row - POINT_WIDTH_HALF, value,
                    row + POINT_WIDTH_HALF, value,
                    mPaint);
        } else {
            mCanvas.drawRect(row - POINT_WIDTH_HALF, mViewH - value*VERTICAL_SCALE_VALUE + POINT_THICKNESS_HALF,
                    row + POINT_WIDTH_HALF, mViewH - value*VERTICAL_SCALE_VALUE - POINT_THICKNESS_HALF,
                    mPaint);
        }
    }


    /*****************************************************
     *		Public methods
     ******************************************************/

    //여기를 못들어오는 것 같다...
    public void initializeGraphics() {
        mViewW = this.getWidth();
        mViewH = this.getHeight();
        Log.d(tag, "width : "+ mViewW);
        Log.d(tag, "height : "+ mViewH);

        // Initialize graphics
        mBitmap = Bitmap.createBitmap(mViewW, mViewH, Bitmap.Config.ARGB_8888);
        mPaint = new Paint();
        mPaint.setAntiAlias(false);
//        mPaint.setDither(true);
//        mPaint.setColor(0xFFFF0000);
//        mPaint.setStyle(Paint.Style.STROKE);
//        mPaint.setStrokeJoin(Paint.Join.ROUND);
//        mPaint.setStrokeCap(Paint.Cap.ROUND);
//        mPaint.setStrokeWidth(12);
//         mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 }, 0.4f, 6, 3.5f);
//         mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);

        mCanvas = new Canvas(mBitmap);
    }

    /**
     * Draw with EEG raw data. Get raw data from SignalHolder instance.
     */
    public void drawRawGraph(double[] rawData) {
        if(rawData==null)
            return;

        drawRawData(rawData);
    }

    public void drawFreqBandGraph(TGEegPower power) {
        if(power==null)
            return;

        drawFrequencyBand(TYPE_BAND_DELTA, mCurrentDrawingX, power.delta );
        drawFrequencyBand(TYPE_BAND_THETA, mCurrentDrawingX, power.theta );
        drawFrequencyBand(TYPE_BAND_LOW_ALPHA, mCurrentDrawingX, power.lowAlpha );
        drawFrequencyBand(TYPE_BAND_HIGH_ALPHA, mCurrentDrawingX, power.highAlpha );
        drawFrequencyBand(TYPE_BAND_LOW_BETA, mCurrentDrawingX, power.lowBeta );
        drawFrequencyBand(TYPE_BAND_HIGH_BETA, mCurrentDrawingX, power.highBeta );
        drawFrequencyBand(TYPE_BAND_LOW_GAMMA, mCurrentDrawingX, power.lowGamma );
        drawFrequencyBand(TYPE_BAND_MID_GAMMA, mCurrentDrawingX, power.midGamma );

        mCurrentDrawingX += POINT_WIDTH;

        if( mCurrentDrawingX + POINT_WIDTH_HALF >= mViewW )
            moveTimeLine();		// push leftward to make space
    }

    //알파~간마 그림 그리기 그래프 1)데이터 추가될떄 왼쪽으로 자리 만들어주는 메서드
    public void drawRelativePowerGraph(TGEegPower power) { //
        if(power==null)
            return;

//        Log.d(tag, power.delta +"");
//        Log.d(tag, power.theta +"");
//        Log.d(tag, power.lowAlpha +"");
//        Log.d(tag, power.highAlpha +"");
//        Log.d(tag, power.lowBeta +"");
//        Log.d(tag, power.highBeta +"");
//        Log.d(tag, power.lowGamma +"");
//        Log.d(tag, power.midGamma +"");

        drawRelativePower(power); //8개 데이터



        mCurrentDrawingX += POINT_WIDTH;

        if( mCurrentDrawingX + POINT_WIDTH_HALF >= mViewW )
            moveTimeLine();		// push leftward to make space
    }

    public void drawValueGraph(int att, int med, int blk, int poor, int hrt) {
        long curTime = System.currentTimeMillis();
        if(curTime - mPrevValueUpdateTime > TIME_UNIT)
            mCurrentDrawingX += POINT_WIDTH;

        if( mCurrentDrawingX + POINT_WIDTH_HALF >= mViewW )
            moveTimeLine();		// push leftward to make space

        if(att > 0)		// Attention is Red dot
            drawValue(1, mCurrentDrawingX, att);
        if(med > 0)		// Meditation is Blue dot
            drawValue(2, mCurrentDrawingX, med);
        if(blk > 0)		// Blink is Green dot
            drawValue(3, mCurrentDrawingX, blk);
        if(poor > 0)
            drawValue(4, mCurrentDrawingX, poor);
        if(hrt > 0)
            drawValue(5, mCurrentDrawingX, hrt);
    }

    /*****************************************************
     *		Sub classes, Handler, Listener
     ******************************************************/


}


