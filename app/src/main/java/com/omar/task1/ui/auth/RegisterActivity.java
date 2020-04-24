package com.omar.task1.ui.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.omar.task1.R;
import com.omar.task1.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {

    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authViewModel = new AuthViewModel();

        ActivityRegisterBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_register);
        binding.setViewmodel(authViewModel);
        binding.setLifecycleOwner(this);


        authViewModel.onSellerBtnClicked().observe(this, new Observer<View>() {
            @Override
            public void onChanged(View view) {
                goToSellerRegisterActivity();
            }
        });


        authViewModel.onCustomerBtnClicked().observe(this, new Observer<View>() {
            @Override
            public void onChanged(View view) {
                goToCustomerRegisterActivity();
            }
        });

    }


    private void goToSellerRegisterActivity(){
        Intent intent = new Intent(this,SellerRegisterActivity.class);
        startActivity(intent);
    }

    private void goToCustomerRegisterActivity(){

        Intent intent = new Intent(this,CustomerRegisterActivity.class);
        startActivity(intent);
    }
}
