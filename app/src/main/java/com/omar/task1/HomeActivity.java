package com.omar.task1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.omar.task1.utils.MySharedPref;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

         getMenuInflater().inflate(R.menu.home_menu,menu);


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.logout_action){
            logout();
        }
        return true;
    }

    private void logout(){
        MySharedPref.getInstance(this).saveLogIn(-1);
        goToLoginActivity();
    }


    private void goToLoginActivity(){
        Intent intent = new Intent(this,LoginActivity.class);

        startActivity(intent);

        finish();
    }
}
