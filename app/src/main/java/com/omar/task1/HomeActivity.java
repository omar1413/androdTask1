package com.omar.task1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.omar.task1.db.AppDatabase;
import com.omar.task1.db.entity.User;
import com.omar.task1.utils.MySharedPref;

public class HomeActivity extends AppCompatActivity {

    private TextView tvUsername;
    private TextView tvPassword;

    private TextView tvUsernameText;
    private TextView tvPasswordText;
    private MySharedPref prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        prefs = MySharedPref.getInstance(this);

        initViews();


    }

    private void getFacebookUser() {
        tvUsernameText.setText(R.string.email_label);
        tvPasswordText.setText("");

        tvUsername.setText(prefs.getEmail());
    }



    private void initViews() {
        tvUsername = findViewById(R.id.tvUsernameLabel);
        tvPassword = findViewById(R.id.tvPasswordLabel);

        tvUsernameText = findViewById(R.id.tvUsernameText);
        tvPasswordText = findViewById(R.id.tvPasswordText);

        if(prefs.getToken() == null) {
            getUser();
        }else{
            getFacebookUser();
        }
    }


    private void getUser() {
        String username = MySharedPref.getInstance(this).isLoggedIn();

        AppDatabase.getInstance(this).getUserDao().getUser(username).observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {


                if (user == null) {

                    logout();
                    return;
                }


                tvUsername.setText(user.getUsername());

                tvPassword.setText(user.getPassword());
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.home_menu, menu);


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.logout_action) {
            logout();
        }
        return true;
    }

    private void logout() {
        prefs.clearToken();
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken !=null && !accessToken.isExpired()) {
            LoginManager.getInstance().logOut();
        }
        MySharedPref.getInstance(this).saveLogIn(null);
        goToLoginActivity();
    }


    private void goToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        finish();
    }
}
