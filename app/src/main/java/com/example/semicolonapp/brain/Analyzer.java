package com.example.semicolonapp.brain;//package com.example.mindwake; /**


import android.util.Log;

import java.util.Arrays;
import java.util.Vector;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

public class Analyzer {

    /* Constants */
    public static final int RECTANGULAR = 0;
    public static final int BARTLETT = 1;
    public static final int HANNING = 2;
    public static final int HAMMING = 3;
    public static final int BLACKMAN = 4;

    // auto-correlation 데이터에서 저주파 대역 일정부분은 사용하지 않음 (feature spectrum 추출시 오차를 줄이기 위해)
    public static final int CUT_HEAD_OFFSET = 70;
    public static final int CUT_TAIL_OFFSET = 70;
    public static final double PEAK_ENERGY_SIZE = 70; 		// 진폭 최대값을 추출 후 최대값의 x% 이내의 값들만 추출해서 사용



    /***************************************************
     *
     * Analyzing methods
     *
     ***************************************************/

    /**
     * Real forward FFT
     */
    public void realForwardFFT(double[] x) {
        int n = x.length;
        DoubleFFT_1D fft = new DoubleFFT_1D(n);

        fft.realForward(x);

        fftMagnitude(x);			// (int i = 2; i < n; i += 2) only (i)th member has magnitude value
    }

    /**
     * This is a "wrapped" signal processing-style autocorrelation.
     * For "true" autocorrelation, the data must be zero padded.
     */
    public void bruteForceAutoCorrelation(double [] x, double [] ac) {
        Arrays.fill(ac, 0);
        int n = x.length;
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < n; i++) {
                ac[j] += x[i] * x[(n + i - j) % n];
            }
        }
    }

    /**
     * Main analyzing method : Auto correlation(자기상관함수)
     * FFT(real forward) -> calc mag -> convert to square value -> IFFT(real inverse)
     *
     * Parameter	x		Audio input (double), FFT 결과가 여기에 저장됨
     * 					ac		Auto-correlation 결과가 저장될 배열
     * Result will be updated in input parameter x[], ac[]  (FFT result, Auto-correlation result)
     */
    public void fftAutoCorrelation(double [] x, double [] ac, boolean normalize) {
        int n = x.length;

        // 1. Forward FFT
        //     Assumes n is even.
        DoubleFFT_1D fft = new DoubleFFT_1D(n);
        fft.realForward(x);

        // 2. 각 element의 Magnitude 값을 구함.
        //     |X[k]^2|
        ac[0] = sqr(x[0]);
        // ac[0] = 0;  // For statistical convention, zero out the mean
        ac[1] = sqr(x[1]);

        for (int i = 2; i < n; i += 2) {
            ac[i] = sqr(x[i]) + sqr(x[i+1]);		// Auto correlation needs |X(k)|^2

            ac[i+1] = 0;
        }

        // 3. Inverse FFT
        //     최종 결과로 나오는 배열이 자기상관함수(auto correlation)의 스펙트럼이 됨.
        DoubleFFT_1D ifft = new DoubleFFT_1D(n);
        ifft.realInverse(ac, true);

        // 4. For statistical convention, normalize by dividing through with variance
        if(normalize)
            for (int i = 1; i < n; i++)
                ac[i] /= ac[0];
        ac[0] = 1;
    }

    /**
     * Main analyzing method : Cepstrum
     * FFT(real forward) -> calc mag -> convert to log scale -> IFFT(real inverse)
     * Result will be updated in parameter x[], ac[]  (FFT result, Cepstrum result)
     */
    public void fftCepstrum(double [] x, double [] ac) {
        int n = x.length;

        // 1. Forward FFT
        //     Assumes n is even.
        DoubleFFT_1D fft = new DoubleFFT_1D(n);
        fft.realForward(x);

        // 2. 각 element의 Magnitude 값을 구함.
        //     |X[k]^2|
        ac[0] = x[0]*x[0];
        // ac[0] = 0;  // For statistical convention, zero out the mean
        ac[1] = x[1]*x[1];

        for (int i = 2; i < n; i += 2) {
            ac[i] = Math.log(x[i] * x[i] + x[i + 1] * x[i + 1]);		// Auto correlation needs Log|X(k)|

            ac[i+1] = 0;
        }

        // 3. Inverse FFT
        //     최종 결과로 나오는 배열이 Cepstrum 이 됨.
        DoubleFFT_1D ifft = new DoubleFFT_1D(n);
        ifft.realInverse(ac, true);

        // 4. For statistical convention, normalize by dividing through with variance
        //for (int i = 1; i < n; i++)
        //    ac[i] /= ac[0];
        //ac[0] = 1;
    }



    /***************************************************
     *
     * Pre-process of FFT
     *
     ***************************************************/

    /*
     * Overlapping function.
     * + fftSampleSize : size of each frame.
     * + overlapFactor : overlap size. (1/overlapFactor)*100 %
     * - return value : size of array after overlapping, (e.g. 1/4=25% overlapping, 0 for no overlapping)
     */
    public int overlapping(double[] source, int fftFrameSize, int overlapFactor) {

        double overlapSize = 1/overlapFactor;
        int frameCount = source.length / fftFrameSize;
        int numOverlappedSamples = fftFrameSize + (fftFrameSize - (int)(fftFrameSize*overlapSize)) * (frameCount - 1);

        frameCount = numOverlappedSamples / fftFrameSize;
        if( numOverlappedSamples % fftFrameSize > 0 )
            frameCount++;

        if(overlapFactor > 1 && frameCount > 1) {

            double[] overlapAmp= new double[frameCount*fftFrameSize];
            Arrays.fill(overlapAmp, 0f);

            int pointer=0;
            int backStepSize = fftFrameSize / overlapFactor;

            for (int i=0; i<source.length; i++){
                overlapAmp[pointer++] += source[i++];
                if ( (i+1) % fftFrameSize == 0){
                    // overlap. When pointer touched frame's end, go back to new start point.
                    pointer -= backStepSize;
                }
            }
            source = overlapAmp;
        }
        // end overlapping

        return source.length;
    }


    public void overlapping2(double[] source, int fftFrameSize) {
        int frameCount = source.length / fftFrameSize;

        for(int i = 0; i < frameCount-2; i++)
        {
            int numOverlappedSamples = fftFrameSize / 2;  //1024
            int index = i * fftFrameSize;                 // 0,2048,4096....
            for(int j = 0; j < numOverlappedSamples-1; j++)
            {
                source[index + numOverlappedSamples + j] += source[index + fftFrameSize + j];
            }
        }
    }

    /*
     * Windowing starts from [offset] and ends in [offset+size]
     */
    public void windowing(double[] targetFrame, int offset, int size, int windowType) {

        // find window function
        String sWindow = null;
        if (windowType == RECTANGULAR)
            sWindow= "RECTANGULAR";
        else if (windowType == BARTLETT)
            sWindow= "BARTLETT";
        else if (windowType == HANNING)
            sWindow= "HANNING";
        else if (windowType == HAMMING)
            sWindow= "HAMMING";
        else if (windowType == BLACKMAN)
            sWindow= "BLACKMAN";
        else
            sWindow= "HANNING";

        // set signals for fft
        WindowFunction window = new WindowFunction();
        window.setWindowType(sWindow);
        double[] win = window.generate(size);

        for (int n=0; n<size; n++){
            targetFrame[offset+n] = targetFrame[offset+n]*win[n];
        }
        // end set signals for fft
    }

    public void windowing(byte[] targetFrame, int offset, int size, int windowType) {

        // find window function
        String sWindow = null;
        if (windowType == RECTANGULAR)
            sWindow= "RECTANGULAR";
        else if (windowType == BARTLETT)
            sWindow= "BARTLETT";
        else if (windowType == HANNING)
            sWindow= "HANNING";
        else if (windowType == HAMMING)
            sWindow= "HAMMING";
        else if (windowType == BLACKMAN)
            sWindow= "BLACKMAN";
        else
            sWindow= "HANNING";

        // set signals for fft
        WindowFunction window = new WindowFunction();
        window.setWindowType(sWindow);
        double[] win = window.generate(size);

        for (int n=0; n<size; n++){
            targetFrame[offset+n] = (byte) (targetFrame[offset+n]*win[n]);
        }
        // end set signals for fft
    }


    /***************************************************
     *
     * Post-processing FFT result
     * Only even element of array has magnitude value
     *
     ***************************************************/

    //  get magnitude of each frequency
    public void fftMagnitude(double [] x) {
        int n = x.length;

        for (int i = 2; i < n; i += 2) {
            x[i] = Math.sqrt(sqr(x[i]) + sqr(x[i + 1]));
        }
    }

    // TODO:
    public void normalization(double[] x) {
        double spectralEnergy = x[0];

        // normalization of absoultSpectrogram
        // set max and min amplitudes
        double maxAmp= Double.MIN_VALUE;
        double minAmp= Double.MAX_VALUE;
        for (int i=2; i<x.length; i+=2){
            if (x[i]>maxAmp){
                maxAmp=x[i];
            }
            else if(x[i]<minAmp){
                minAmp=x[i];
            }
        }
        // end set max and min amplitudes

        // normalization
        // avoiding divided by zero
        double minValidAmp=0.00000000001F;
        if (minAmp==0){
            minAmp=minValidAmp;
        }

        double diff= Math.log10(maxAmp / minAmp);	// perceptual difference
        for (int i=2; i<x.length; i+=2){
            if (x[i]<minValidAmp){
                x[i]=0;
            }
            else{
                x[i]=(Math.log10(x[i] / minAmp))/diff;
            }
        }
        // end normalization
    }

    // TODO:
    public void lowPassFilter(double[] x, double filteringFactor) {
        filteringFactor = 0.8f;

        for( int i=2; i< x.length; i++ ) {
            double u = i / (i + filteringFactor);

            x[i] = u * (x[i] - x[i-1]) + x[i-1];
        }
    }



    /***************************************************
     *
     * Find features
     *
     ***************************************************/
    // Extract feature spectrum from auto-correlation data.
    // Feature spectrum is bit sequence of half spectrum size.
    //
    // parameter		spectrograms		사용자가 설정한 음의 데이터(auto-correlation). 각 벡터 요소가 하나의 프레임 정보를 가지고 있음.
    //							startIndex			feature spectrum 으로 변환할 프레임의 시작 index
    //							offset					변환할 feature spectrum 갯수
    //	return				변환한 feature spectrum 들을 하나의 byte array 로 합쳐서 반환
    public byte[] extractFeatureSpectrum(Vector<double[]> spectrograms, int startIndex, int offset) {

        double maxvalue = 0;
        int spectrumSize = spectrograms.get(0).length;		// 입력된 spectrum의 배열 갯수
        int featureSpectrumSize = spectrumSize / 2;			// Auto-correlation 데이터는 중앙을 기준으로 대칭형태이므로 절반만 저장

        // feature spectrum 을 x 개의 bit sequence 로 저장
        // byte array 를 사용
        byte[] featureSpectrum = new byte[ featureSpectrumSize*offset/ Byte.SIZE ];		// (feature spectrum 사이즈 * 프레임 갯수) / Byte
        Arrays.fill(featureSpectrum, (byte) 0x00);

        for(int i=0; i<offset; i++) {
            double[] temp = spectrograms.get(startIndex+i);

            // 최대값을 구함
            for(int j=CUT_HEAD_OFFSET; j<featureSpectrumSize; j++) {
                double value = Math.abs(temp[j]);

                if(value > maxvalue)
                    maxvalue = value;
            }

            double peakEnergy = (PEAK_ENERGY_SIZE/100f)*maxvalue;		// 최대값의 x% 이내의 값만 사용

            for(int j=CUT_HEAD_OFFSET; j<featureSpectrumSize; j++) {
                if( temp[j] < peakEnergy )
                    continue;

                // turn on n-th bit
                int targetByte = ( i*featureSpectrumSize + j) / Byte.SIZE;
                int targetBit = ( i*featureSpectrumSize + j) % Byte.SIZE;
                featureSpectrum[targetByte] = (byte)( featureSpectrum[targetByte] | (byte)(0x01 << (7 - targetBit)) );  		// Turn on j(th) bit
                //Log.d("_____test", "# Byte="+targetByte+", Bit="+targetBit+", value = on");
            }

            maxvalue = 0;
        }

        return featureSpectrum;
    }

    // Extract feature spectrum from auto-correlation data.
    // Feature spectrum is bit sequence of half spectrum size.
    // 하나의 프레임을 feature spectrum으로 변환
    //
    // parameter	peakEnergyFiltering					High pass filtering. Zeroing out under this value.
    // return			feature spectrum sequence 	Half size of spectogram (because symmetric about last half)
    public byte[] extractFeatureSpectrum(double[] spectrograms, double peakEnergyFiltering) {

        double maxvalue = 0;
        int spectrumSize = spectrograms.length;
        int featureSpectrumSize = spectrumSize / 2;

        byte[] featureSpectrum = new byte[ featureSpectrumSize/ Byte.SIZE ];
        Arrays.fill(featureSpectrum, (byte) 0x00);

        for(int j=CUT_HEAD_OFFSET; j<featureSpectrumSize; j++) {
            double value = Math.abs(spectrograms[j]);

            if(value > maxvalue)
                maxvalue = value;
        }

        double peakEnergy = (peakEnergyFiltering/100f)*maxvalue;

        for(int j=CUT_HEAD_OFFSET; j<featureSpectrumSize; j++) {
            if( spectrograms[j] < peakEnergy )
                continue;

            // turn on n-th bit
            int targetByte = j / Byte.SIZE;
            int targetBit = j % Byte.SIZE;
            featureSpectrum[targetByte] = (byte)( featureSpectrum[targetByte] | (byte)(0x01 << (7 - targetBit)) );  		// Turn on j(th) bit
            //Log.d("_____test", "# Byte="+targetByte+", Bit="+targetBit+", value = on");
        }

        return featureSpectrum;
    }

    // Compare two spectrum and calculate matching size.
    // parameter	origin		비교 기준. 사용자가 미리 셋팅해 둔 feature spectrum
    // 						source		입력 음원. 비교에 사용할 현재 입력된 feature spectrum
    // return			value		matching percentage(%)
    public int compareFeatureSpectrum(byte[] origin, byte[] source, int sizeOfByteArray) {

        int same = 0;
        int total = 0;

        for(int i=CUT_HEAD_OFFSET; i<sizeOfByteArray; i++) {
            if((byte)0x00 == origin[i])
                continue;

            if( (origin[i] & source[i]) == origin[i] )
            //if( origin[i] == source[i] )
            {
                same++;
            }

            total++;
        }

        return (int)( ((double)same / (double)total) * 100f );
    }


    // Find max frequency by Moving average
    // parameter		source		FFT result. Only even element has magnitude value.
    // return			index		Index at max value
    public int findMaxByMovingAverage(double[] x) {
        int filterWidth = 3;								// must be odd number
        int halfOfFilterWidth = filterWidth/2;
        int maxFrequencyIndex = 0;
        double maxMagnitude = 0.0;

        for (int i = 2; i < x.length; i += 2) {
            if( i/2 - halfOfFilterWidth <= 0 || i/2 + halfOfFilterWidth >= x.length/2)		// Check boundary
                continue;

            double magnitudeTotal = 0.0;
            for(int j=0; j<filterWidth; j++) {
                magnitudeTotal += x[i+(j-halfOfFilterWidth)*2];
            }

            if(maxMagnitude < magnitudeTotal) {
                maxFrequencyIndex = i;
                maxMagnitude = magnitudeTotal;
            }
        }

        return maxFrequencyIndex;
    }

    // Find max frequency by Moving average
    // parameter		source		FFT result. Only even element has magnitude value.
    // return			index		Index at max value
    public int findMaxByMovingAverage(double[] x, int start, int end) {
        int filterWidth = 3;								// must be odd number
        int halfOfFilterWidth = filterWidth/2;
        int maxFrequencyIndex = 0;
        double maxMagnitude = 0.0;

        for (int i = start; i <= end; i += 2) {
            if( i/2 - halfOfFilterWidth <= 0 || i/2 + halfOfFilterWidth >= x.length/2)		// Check boundary
                continue;

            double magnitudeTotal = 0.0;
            for(int j=0; j<filterWidth; j++) {
                magnitudeTotal += x[i+(j-halfOfFilterWidth)*2];
            }

            if(maxMagnitude < magnitudeTotal) {
                maxFrequencyIndex = i;
                maxMagnitude = magnitudeTotal;
            }
        }

        return maxFrequencyIndex;
    }




    /***************************************************
     *
     * Etc
     *
     ***************************************************/

    private double sqr(double x) {
        return x * x;
    }

    void print(String msg, double [] x) {
        Log.d("___Humming", msg);
        StringBuilder sb = new StringBuilder();
        sb.append("array = ");

        for (double d : x) {
            sb.append(d);
            sb.append(", ");
        }

        Log.d("___Humming", sb.toString());
    }


    /***************************************************
     *
     * Window function
     *
     ***************************************************/

    class WindowFunction {

        public static final int RECTANGULAR = 0;
        public static final int BARTLETT = 1;
        public static final int HANNING = 2;
        public static final int HAMMING = 3;
        public static final int BLACKMAN = 4;

        int windowType = 0; // defaults to rectangular window

        public WindowFunction() {
        }

        public void setWindowType(int wt) {
            windowType = wt;
        }

        public void setWindowType(String w) {
            if (w.toUpperCase().equals("RECTANGULAR"))
                windowType = RECTANGULAR;
            if (w.toUpperCase().equals("BARTLETT"))
                windowType = BARTLETT;
            if (w.toUpperCase().equals("HANNING"))
                windowType = HANNING;
            if (w.toUpperCase().equals("HAMMING"))
                windowType = HAMMING;
            if (w.toUpperCase().equals("BLACKMAN"))
                windowType = BLACKMAN;
        }

        public int getWindowType() {
            return windowType;
        }

        /**
         * Generate a window
         *
         * @param nSamples	size of the window
         * @return	window in array
         */
        public double[] generate(int nSamples) {
            // generate nSamples window function values
            // for index values 0 .. nSamples - 1
            int m = nSamples / 2;
            double r;
            double pi = Math.PI;
            double[] w = new double[nSamples];
            switch (windowType) {
                case BARTLETT: // Bartlett (triangular) window
                    for (int n = 0; n < nSamples; n++)
                        w[n] = 1.0f - Math.abs(n - m) / m;
                    break;
                case HANNING: // Hanning window
                    r = pi / (m + 1);
                    for (int n = -m; n < m; n++)
                        w[m + n] = 0.5f + 0.5f * Math.cos(n * r);
                    break;
                case HAMMING: // Hamming window
                    r = pi / m;
                    for (int n = -m; n < m; n++)
                        w[m + n] = 0.54f + 0.46f * Math.cos(n * r);
                    break;
                case BLACKMAN: // Blackman window
                    r = pi / m;
                    for (int n = -m; n < m; n++)
                        w[m + n] = 0.42f + 0.5f * Math.cos(n * r) + 0.08f
                                * Math.cos(2 * n * r);
                    break;
                default: // Rectangular window function
                    for (int n = 0; n < nSamples; n++)
                        w[n] = 1.0f;
            }
            return w;
        }
    }

}

