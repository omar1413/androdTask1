package com.omar.task1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.omar.task1.api.ApiClient;
import com.omar.task1.api.services.ResetPasswordService;
import com.omar.task1.utils.Utils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class RestorePasswordActivity extends AppCompatActivity {

    private EditText etEmail;
    private Button btnRestore;

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
        setContentView(R.layout.activity_restore_password);

        initViews();
    }

    private void initViews(){
        etEmail = findViewById(R.id.etEmail);
        btnRestore = findViewById(R.id.btnRestore);

        btnRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToken();
            }
        });
    }

    private void sendToken(){
        String email = etEmail.getText().toString();

        if(email.isEmpty()){
            etEmail.setError(getString(R.string.email_empty_error));
            return;
        }


        showProgress();
        ResetPasswordService resetPasswordService = ApiClient.getClient(this).create(ResetPasswordService.class);

        resetPasswordService.sendToken(email).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(
                new DisposableSingleObserver<Response<Void>>() {
                    @Override
                    public void onSuccess(Response<Void> stringResponse) {
                        hideProgress();
                        if(stringResponse.code() == 200){
                            goToCheckCodeActivity();
                        }else{

                                Utils.errorAlert(RestorePasswordActivity.this,"Email not found");

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideProgress();
                        Utils.errorAlert(RestorePasswordActivity.this,"Check your connection");
                    }
                }
        );
    }

    private void goToCheckCodeActivity(){
        Intent intent = new Intent(this,CheckCodeActivity.class);
        startActivity(intent);
    }
}
