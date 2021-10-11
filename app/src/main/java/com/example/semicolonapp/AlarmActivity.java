package com.example.semicolonapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.media.MediaPlayer;
import android.os.Handler;
import android.widget.ProgressBar;

public class AlarmActivity extends Activity {
    private TextView txtProgress;
    private ProgressBar progressBar;
    private int pStatus = 60;
    private Handler handler = new Handler();
    Thread th;
    MediaPlayer mediaplayer;

    //음성 추가내용

    Context cThis ; // Context 설정
    String LogTT = "[STT]"; //Log 타이틀

    //음성 인식용
    Intent SttIntent;
    SpeechRecognizer mRecognizer;

    //음성 출력용
    TextToSpeech tts;

    //화면 처리용
    Button btnSttStart; //버튼설정
    TextView txtInMsg; //입력 박스 설정

    final int PERMISSION = 1;
    //String array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        cThis = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarmactivity);

        txtProgress = (TextView) findViewById(R.id.txtProgress);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        //초기화 추가
        txtProgress.setText(pStatus + "");

        mediaplayer = MediaPlayer.create(getApplicationContext(), R.raw.alarm);
        mediaplayer.setLooping(true);
        mediaplayer.start();

        th = new AlarmActivity.stopwatchThread();
        th.start();

        // 안드로이드 6.0버전 이상인지 체크해서 퍼미션 체크
        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO}, PERMISSION);
        }

        // 음성인식
        SttIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        SttIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getApplicationContext().getPackageName());
        SttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(cThis);
        mRecognizer.setRecognitionListener(listener);

        //음성출력 생성, 리스너 초기화
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != android.speech.tts.TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });

        //버튼 클릭 시 객체에 Context와 listener를 할당
        btnSttStart = (Button) findViewById(R.id.btnSttStart);
        btnSttStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(cThis, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AlarmActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
                    //권한을 허용하지 않는 경우
                } else {
                    try {
                        mRecognizer.startListening(SttIntent);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        //입력박스 설정
        txtInMsg = (TextView) findViewById(R.id.txtInMsg);


//         어플이 실행되면 자동으로 1초 뒤에 음성 인식 시작..
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 실행할 동작 코딩
                btnSttStart.performClick();
            }
        }, 1000);

        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                btnSttStart.performClick();
            }
        } ;

        Timer timer = new Timer(true);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = handler.obtainMessage();
                handler.sendMessage(msg);
            }

            @Override
            public boolean cancel() {
                return super.cancel();
            }
        };
        timer.schedule(timerTask, 0, 3000); // 5초에 한번씩 버튼 클릭
    }

    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            // 말하기 시작할 준비가되면 호출
            Toast.makeText(getApplicationContext(), "음성인식 시작", Toast.LENGTH_SHORT).show();
            Log.d("tst5", "시작");
        }

        @Override
        public void onBeginningOfSpeech() {
            // 말하기 시작했을 때 호출
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            // 입력받는 소리의 크기를 알려줌
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            // 말을 시작하고 인식이 된 단어를 buffer에 담음
        }

        @Override
        public void onEndOfSpeech() {
            // 말하기를 중지하면 호출
        }

        @Override
        public void onError(int error) {
            // 네트워크 또는 인식 오류가 발생했을 때 호출
            String message;

            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "클라이언트 에러";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "찾을 수 없음";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER 가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;
                default:
                    message = "알 수 없는 오류임";
                    break;
            }

            //Toast.makeText(getApplicationContext(), "에러 발생 : " + message, Toast.LENGTH_SHORT).show();
            Log.d("tst5", "onError: " + message);
        }

        @Override
        public void onResults(Bundle results) {
            String key = "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = results.getStringArrayList(key);
            String[] rs = new String[mResult.size()];
            mResult.toArray(rs);

            Log.i(LogTT, "입력값: " + rs[0]);
            //Toast.makeText(getContext(), rs[0], Toast.LENGTH_SHORT).show();
            //txtInMsg.setText(rs[0] + "\r\n" + txtInMsg.getText());
            FuncVoiceOrderCheck(rs[0]);
            mRecognizer.startListening(SttIntent); //음성인식이 계속 되는 구문이니 필요에 맞게 쓰시길 바람
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            // 부분 인식 결과를 사용할 수 있을 때 호출
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            // 향후 이벤트를 추가하기 위해 예약
        }
    };

    private void FuncVoiceOrderCheck(String VoiceMsg){
        if(VoiceMsg.length()<1) return;
        VoiceMsg = VoiceMsg.replace(" ", "");//공백제거

        if(VoiceMsg.indexOf("알람꺼줘") > -1 || VoiceMsg.indexOf("꺼") > -1){
            Log.i(LogTT, "메시지 화면 : 알람끄기");
            //FuncVoiceOut("메인화면으로 돌아갑니다.");
            setResult(RESULT_OK);
            th.interrupt();
            mediaplayer.stop();
            mediaplayer.release();
            finish();
        }
    }

    private void FuncVoiceOut(String OutMsg){
        if (OutMsg.length() < 1) {return;}
        //tts.setPitch(2.0f);
        //tts.setSpeechRate(1.0f);
        tts.speak(OutMsg, TextToSpeech.QUEUE_FLUSH, null);
    }

//    //어플 종료
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (tts != null) {
//            tts.stop();
//            tts.shutdown();
//            tts = null;
//        }
//        if (mRecognizer != null) {
//            mRecognizer.destroy();
//            mRecognizer.cancel();
//            mRecognizer = null;
//        }
//    }

    public class stopwatchThread extends Thread {

        public void run() {
            while (pStatus >= 0) {

                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        progressBar.setProgress(pStatus);
                        txtProgress.setText(pStatus + "");
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
                pStatus--;

            }
        }
    }
}
