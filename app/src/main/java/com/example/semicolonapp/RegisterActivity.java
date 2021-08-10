package com.example.semicolonapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.semicolonapp.R;
import com.example.semicolonapp.data.RegisterData;
import com.example.semicolonapp.data.RegisterResponse;
import com.example.semicolonapp.network.RetrofitClient;
import com.example.semicolonapp.network.ServiceApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private AutoCompleteTextView mEmail;
    private EditText mPw,mName,mAge;
    private Button register;
    private ProgressBar mProgressView;
    private ServiceApi service;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registeractivity);

        mEmail = (AutoCompleteTextView) findViewById(R.id.et_email);
        mPw = (EditText)findViewById(R.id.et_pass);
        mName = (EditText)findViewById(R.id.et_name);
        mAge = (EditText)findViewById(R.id.et_age);
        register = (Button)findViewById(R.id.btn_register);
        mProgressView = (ProgressBar) findViewById(R.id.register_progress);

        service = RetrofitClient.getClient().create(ServiceApi.class);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });


    }

    private void register() {
        mEmail.setError(null); //null들어가면 에러라고 사용자에게 알려줌
        mPw.setError(null); //null들어가면 에러라고 사용자에게 알려줌
        mName.setError(null); //null들어가면 에러라고 사용자에게 알려줌
        mAge.setError(null); //null들어가면 에러라고 사용자에게 알려줌
        register.setError(null); //null들어가면 에러라고 사용자에게 알려줌

        String email = mEmail.getText().toString();
        String password = mPw.getText().toString();
        String name = mName.getText().toString();
        String age = mAge.getText().toString();

        boolean cancel = false;
        View focusView = null; //현재 초점맞춰야하는 뷰

        //패스워드의 유효성 검사
        if (password.isEmpty()){
            mPw.setError("비밀번호를 입력해주세요.");
            focusView=mPw;
            cancel = true;

        }else if (!isPasswordValid(password)){
            mPw.setError("6자 이상의 비밀번호를 입력해주세요.");
            focusView=mPw;
            cancel = true;
        }

        // 이메일의 유효성 검사
        if (email.isEmpty()) {
            mEmail.setError("이메일을 입력해주세요.");
            focusView = mEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmail.setError("@를 포함한 유효한 이메일을 입력해주세요.");
            focusView = mEmail;
            cancel = true;
        }

        // 이름의 유효성 검사
        if (name.isEmpty()) {
            mName.setError("이름을 입력해주세요.");
            focusView = mName;
            cancel = true;
        }

        // 나이 유효성 검사
        if (age.isEmpty()) {
            mAge.setError("이름을 입력해주세요.");
            focusView = mAge;
            cancel = true;
        }

        if(cancel){ //cancel=true일때 포커스 실시
            focusView.requestFocus();
        }else{
            //핵심 기능
            startRegister( new RegisterData(email,password,name,age));
            showProgress(true);
        }



    }

    private void startRegister(RegisterData data) {
        service.userRegister(data).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                RegisterResponse result = response.body(); //response.body는 server(nodejs)에서 보낸 결과값들
                Toast.makeText(RegisterActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                showProgress(false);

                if(result.getCode()==200){
                    finish();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "회원가입 에러 발생", Toast.LENGTH_SHORT).show();
                Log.e("회원가입 에러 발생", t.getMessage());
                t.printStackTrace(); // 에러 발생시 에러 발생 원인 단계별로 출력해줌
                showProgress(false);
            }
        });
    }


    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    private void showProgress(boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE:View.GONE);
    }

}