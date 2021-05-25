package com.example.rapiertech.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.rapiertech.R;
import com.example.rapiertech.api.ApiClient;
import com.example.rapiertech.api.ApiInterface;
import com.example.rapiertech.model.login.Login;
import com.example.rapiertech.model.login.LoginData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import www.sanju.motiontoast.MotionToast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText inputEmail, inputPassword;
    Button btnLogin;
    ProgressBar progressBar;

    String email, password;

    ApiInterface apiInterfaces;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        btnLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progress);

        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                email = inputEmail.getText().toString();
                password = inputPassword.getText().toString();
                login(email, password);
                break;
        }
    }

    private void login(String email, String password) {

        progressBar.setVisibility(View.VISIBLE);

        apiInterfaces = ApiClient.getClient().create(ApiInterface.class);
        Call<Login> loginCall = apiInterfaces.loginResponse(email,password);
        loginCall.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {

                progressBar.setVisibility(View.GONE);

                if (response.body() != null && response.isSuccessful() && response.body().isStatus()){
                    // simpan session
                    sessionManager = new SessionManager(LoginActivity.this);
                    LoginData loginData = response.body().getLoginData();
                    sessionManager.createLoginSession(loginData);

                    // pindah activity
                    MotionToast.Companion.createColorToast(LoginActivity.this, "Success",
                            response.body().getMessage(),
                            MotionToast.TOAST_SUCCESS,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(LoginActivity.this,R.font.helvetica_regular)
                    );
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    MotionToast.Companion.createColorToast(LoginActivity.this, "Error",
                            response.body().getMessage(),
                            MotionToast.TOAST_ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(LoginActivity.this,R.font.helvetica_regular)
                    );
                }

            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                MotionToast.Companion.createColorToast(LoginActivity.this, "Cannot connect server",
                        t.getMessage(),
                        MotionToast.TOAST_ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(LoginActivity.this,R.font.helvetica_regular)
                );
            }
        });

    }
}