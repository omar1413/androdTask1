package com.omar.task1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.material.navigation.NavigationView;
import com.omar.task1.db.AppDatabase;
import com.omar.task1.db.entity.User;
import com.omar.task1.fragments.HomeFragment;
import com.omar.task1.fragments.ProfileFragment;
import com.omar.task1.utils.MySharedPref;

public class HomeActivity extends AppCompatActivity {

    private TextView tvUsername;
    private TextView tvPassword;

    private TextView tvUsernameText;
    private TextView tvPasswordText;
    private MySharedPref prefs;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_menu_24px);
//        toolbar.setTitle("Title");

//        toolbar.setLogo(R.drawable.ic_launcher);

        drawerLayout = findViewById(R.id.drawer_layout);
        final NavigationView nav_view = findViewById(R.id.nav_view);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        //ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        //drawerLayout.addDrawerListener(toggle);
        //toggle.syncState();


        //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new SearchFragment()).commit();

        nav_view.setCheckedItem(R.id.home);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();


        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch(menuItem.getItemId()) {
                    case R.id.home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
                        break;
//
                    case R.id.profile:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();

                        break;
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }


    private void getFacebookUser() {
        tvUsernameText.setText(R.string.email_label);
        tvPasswordText.setText("");

        tvUsername.setText(prefs.getEmail());
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
