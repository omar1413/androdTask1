package com.omar.task1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.omar.task1.db.AppDatabase;
import com.omar.task1.db.entity.User;
import com.omar.task1.utils.MySharedPref;

public class RegisterActivity extends AppCompatActivity {

    private Button btnRegister;
    private EditText etUsername;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
    }

    private void initViews() {

        btnRegister = findViewById(R.id.btnRegister);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }


    private void register() {

        final String username = etUsername.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();
        if (username.isEmpty() || password.isEmpty()) {
            if (username.isEmpty()) {
                etUsername.setError(getString(R.string.username_empty_error));
            }


            if (password.isEmpty()) {
                etPassword.setError(getString(R.string.password_empty_error));
            }

            return;

        }


        AppDatabase.getInstance(this).getUserDao().getUser(username).observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user == null) {
                    new Thread() {
                        @Override
                        public void run() {
                            AppDatabase.getInstance(RegisterActivity.this).getUserDao().insert(new User(username, password));


                            MySharedPref.getInstance(RegisterActivity.this).saveLogIn(username);



                        }
                    }.start();
                    goToHomeActivity();

                } else {
                    etUsername.setError(getString(R.string.user_exist_error));
                }
            }
        });


    }

    private void goToHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);


        finish();

    }

    @Override
    public void onBackPressed() {
        goToLoginActivity();

    }

    private void  goToLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        finish();
    }
}
