package com.omar.task1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.omar.task1.db.AppDatabase;
import com.omar.task1.db.entity.User;
import com.omar.task1.utils.MySharedPref;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;

public class LoginActivity extends AppCompatActivity {


    private static final String TAG = LoginActivity.class.getName();

    private TextView tvRegister;
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d(TAG, "i am here in firist");

        checkLogin();

        initViews();

    }

    private void initViews(){
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegisterActivity();
            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

    }

    private void goToRegisterActivity(){

        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }

    private void login(){
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        LiveData<User> user = AppDatabase.getInstance(this).getUserDao().getUser(username,password);
        Log.d(TAG, "i am here in login");
        Log.d(TAG, "i am here in login -" + username + "-");
        Log.d(TAG, "i am here in login  -" + password + "-");

        user.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if(user != null){
                    Log.d(TAG, "i am here in success");
                    goToHomeActivity(user.getId());
                }else{
                    Log.d(TAG, "i am here in error");
                    etUsername.setError("wrong");
                    etPassword.setError("wrong");
                    //Toast.makeText(LoginActivity.this, "error", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void goToHomeActivity(long id){
        MySharedPref.getInstance(this).saveLogIn(id);

        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        finish();
    }

    private boolean checkLogin(){
        long logId =  MySharedPref.getInstance(this).isLoggedIn();

        if(logId != -1){
            goToHomeActivity(logId);
            return true;
        }

        return false;
    }



}
