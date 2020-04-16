package com.omar.task1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.omar.task1.api.ApiClient;
import com.omar.task1.api.services.ResetPasswordService;
import com.omar.task1.utils.Utils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class CheckCodeActivity extends AppCompatActivity {

    private EditText etCode;
    private Button btnRestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_code);

        initViews();
    }


    private void initViews(){
        etCode = findViewById(R.id.etCode);
        btnRestore = findViewById(R.id.btnRestore);

        btnRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkToken();
            }
        });
    }

    private void checkToken(){
        final String token = etCode.getText().toString();

        if(token.isEmpty()){

            etCode.setError(getString(R.string.token_empty_error));
            return;
        }

        ApiClient.getClient(this).create(ResetPasswordService.class).checkToken(token).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(
                new DisposableSingleObserver<Response<Void>>() {
                    @Override
                    public void onSuccess(Response<Void> voidResponse) {
                        if (voidResponse.code() == 200){
                            gotToNewPasswordActivity(token);
                        }else{
                            Utils.errorAlert(CheckCodeActivity.this,"Invalid Token");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                        Utils.errorAlert(CheckCodeActivity.this,"check your connection");
                    }
                }
        );


    }

    private void gotToNewPasswordActivity(String token){
        Intent intent = new Intent(this, NewPasswordActivity.class);
        intent.putExtra("token",token);
        startActivity(intent);
    }
}
