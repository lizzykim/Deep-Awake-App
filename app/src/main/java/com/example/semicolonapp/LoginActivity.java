package com.example.semicolonapp;

import android.content.Context;
import android.content.Intent;
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
import com.example.semicolonapp.data.DataHolder;
import com.example.semicolonapp.data.LoginData;
import com.example.semicolonapp.data.LoginResponse;
import com.example.semicolonapp.network.RetrofitClient;
import com.example.semicolonapp.network.ServiceApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    public AutoCompleteTextView mEmail;
    private EditText mPassword;
    private Button login,register;
    private ProgressBar mProgressView;
    private ServiceApi service;

    private  String email,password; //사용자가 입력한 이메일, 비번

    public static Context context_main; // context 변수 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginactivity);

        context_main = this;

        mEmail = (AutoCompleteTextView) findViewById(R.id.et_loginEmail);
        mPassword = (EditText) findViewById(R.id.et_loginPW);
        login = (Button)findViewById(R.id.bt_login);
        register = (Button)findViewById(R.id.bt_newReg);
        mProgressView = (ProgressBar) findViewById(R.id.login_progress);

        service = RetrofitClient.getClient().create(ServiceApi.class);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.bt_login:
                        attemptLogin();
                        break;
                    case R.id.bt_newReg:
                        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                        startActivity(intent);
                        break;
                }

            }
        };

        login.setOnClickListener(onClickListener);
        register.setOnClickListener(onClickListener);



    }

    private void attemptLogin() {

        mEmail.setError(null);
        mPassword.setError(null);


//        String email = mEmail.getText().toString();
//        String password = mPassword.getText().toString();

        //전역으로 뺴주기
        email = mEmail.getText().toString();
        password = mPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // 패스워드의 유효성 검사
        if (password.isEmpty()) {
            mEmail.setError("비밀번호를 입력해주세요.");
            focusView = mEmail;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPassword.setError("6자 이상의 비밀번호를 입력해주세요.");
            focusView = mPassword;
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

        if(cancel){
            focusView.requestFocus();
        }else{
            startLogin(new LoginData(email,password));
            showProgress(true);

        }

    }



    private void startLogin(LoginData data) {
        service.userLogin(data).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                LoginResponse result = response.body();
                Toast.makeText(LoginActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                showProgress(false);

                if(result.getCode()== 200){


                    DataHolder.setUseremail(email);
                    Intent intent2 = new Intent(LoginActivity.this, AsksmsActivity.class);
//                    Intent intent2 = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent2);
                }

            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "로그인 에러 발생", Toast.LENGTH_SHORT).show();
                Log.e("로그인 에러 발생", t.getMessage());
                t.printStackTrace();
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
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}