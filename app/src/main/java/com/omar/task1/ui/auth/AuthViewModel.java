package com.omar.task1.ui.auth;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


public class AuthViewModel extends ViewModel {

     private MutableLiveData<View> sellerBtn = new MutableLiveData<>();
     private MutableLiveData<View> customerBtn = new MutableLiveData<>();














     public LiveData<View> onSellerBtnClicked(){
         return sellerBtn;
     }

    public LiveData<View> onCustomerBtnClicked(){
        return customerBtn;
    }

    public void setSellerBtn(View view) {
       sellerBtn.setValue(view);
    }

    public void setCustomerBtn(View view) {
        customerBtn.setValue(view);
    }
}
