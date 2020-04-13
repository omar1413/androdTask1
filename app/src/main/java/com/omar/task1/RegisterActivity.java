package com.omar.task1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.omar.task1.api.ApiClient;
import com.omar.task1.api.models.UserModel;
import com.omar.task1.api.services.UserService;
import com.omar.task1.db.AppDatabase;
import com.omar.task1.db.entity.User;
import com.omar.task1.utils.MySharedPref;

import java.util.regex.Pattern;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private Button btnRegister;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etEmail;
    private EditText etPhone;
    private EditText etGender;
    private EditText etAddress;

    private CompositeDisposable disposable = new CompositeDisposable();


    /*
    ^                 # start-of-string
    (?=.*[0-9])       # a digit must occur at least once
    (?=.*[a-z])       # a lower case letter must occur at least once
    (?=.*[A-Z])       # an upper case letter must occur at least once
    (?=.*[@#$%^&+=])  # a special character must occur at least once you can replace with your special characters
    (?=\\S+$)         # no whitespace allowed in the entire string
    .{4,}             # anything, at least six places though
    $                 # end-of-string

     */
    private Pattern passwordPattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])\\S{8,12}$");
    //private Pattern passwordPattern = Pattern.compile("^(?=.*[a-z])$");

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
        etEmail = findViewById(R.id.etEmail);
        etGender = findViewById(R.id.etGender);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }


    private boolean validate(String username, String password){

        boolean valid = true;
        if (username.isEmpty() || !passwordPattern.matcher(password).matches()) {
            if (username.isEmpty()) {
                etUsername.setError(getString(R.string.username_empty_error));
            }

            if (!passwordPattern.matcher(password).matches()) {
                etPassword.setError(getString(R.string.password_not_valid_error));
            }
            valid = false;
        }




        return valid;
    }

    private void register() {

        final String username = etUsername.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        final String phone = etPhone.getText().toString().trim();
        final String address = etAddress.getText().toString().trim();
        final String gender = etGender.getText().toString().trim();

        if(!validate(username, password)) return;


        UserService userService = ApiClient.getClient(this).create(UserService.class);
        disposable.add(
        userService.signup(new UserModel(username,password,email,phone,address)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(
                new DisposableSingleObserver<Response<UserModel>>(){

                    @Override
                    public void onSuccess(Response<UserModel> response) {
                        if(response.code() == 200){
                            String jwt = response.headers().get("authorization");
                            MySharedPref.getInstance(RegisterActivity.this).saveLogIn(jwt);
                            goToHomeActivity();
                        }else{
                            if(response.body() != null && response.body().getError() != null)
                                Toast.makeText(RegisterActivity.this, response.body().getError() , Toast.LENGTH_SHORT).show();
                            else{
                                Toast.makeText(RegisterActivity.this, "error in register " + ((response.errorBody() != null) ? response.errorBody().toString() : ""), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("0","" + e.getMessage());
                    }
                }
        )
        );
//        AppDatabase.getInstance(this).getUserDao().getUser(username).observe(this, new Observer<User>() {
//            @Override
//            public void onChanged(User user) {
//                if (user == null) {
//                    new Thread() {
//                        @Override
//                        public void run() {
//                            AppDatabase.getInstance(RegisterActivity.this).getUserDao().insert(new User(username, password));
//
//
//                            MySharedPref.getInstance(RegisterActivity.this).saveLogIn(username);
//
//
//
//                        }
//                    }.start();
//                    goToHomeActivity();
//
//                } else {
//                    etUsername.setError(getString(R.string.user_exist_error));
//                }
//            }
//        });


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
