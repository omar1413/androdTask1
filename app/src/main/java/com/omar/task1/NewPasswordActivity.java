package com.omar.task1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.omar.task1.api.ApiClient;
import com.omar.task1.api.services.ResetPasswordService;
import com.omar.task1.ui.auth.LoginActivity;
import com.omar.task1.utils.Utils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class NewPasswordActivity extends AppCompatActivity {

    private EditText etNewPassword;
    private Button btnRestore;

    private String token = null;

    private LinearLayout progressLayout;

    private void showProgress(){
        if (progressLayout == null){
            progressLayout = findViewById(R.id.progressLayout);
        }

        progressLayout.setVisibility(View.VISIBLE);
    }

    private void hideProgress(){
        if (progressLayout == null){
            progressLayout = findViewById(R.id.progressLayout);
        }

        progressLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        token = getIntent().getStringExtra("token");
        initViews();
        if (token == null || token.isEmpty()) {

            Utils.errorAlert(this, "invalid token");
            goToLoginPage();
        }

    }

    private void initViews() {
        etNewPassword = findViewById(R.id.etPassword);
        btnRestore = findViewById(R.id.btnRestore);

        btnRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restorePassword();
            }
        });
    }

    private void restorePassword() {
        String newPassword = etNewPassword.getText().toString();

        if(newPassword.isEmpty()){
            etNewPassword.setError(getString(R.string.password_empty_error));
            return;
        }

        showProgress();

        ApiClient.getClient(this).create(ResetPasswordService.class).resetPassword(token, newPassword)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Response<Void>>() {
                    @Override
                    public void onSuccess(Response<Void> voidResponse) {
                        hideProgress();
                        if(voidResponse.code() == 200){
                            Toast.makeText(NewPasswordActivity.this, "password have been reset", Toast.LENGTH_SHORT).show();
                            goToLoginPage();
                        }else{
                            Utils.errorAlert(NewPasswordActivity.this, "Unknown error happened");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideProgress();
                        Utils.errorAlert(NewPasswordActivity.this, "check your connection");
                    }
                });
    }

    private void goToLoginPage() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }
}
