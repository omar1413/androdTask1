package com.omar.task1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.material.navigation.NavigationView;
import com.omar.task1.api.ApiClient;
import com.omar.task1.api.models.SellerModel;
import com.omar.task1.api.models.UserModel;
import com.omar.task1.api.models.UserType;
import com.omar.task1.api.services.SellerService;
import com.omar.task1.api.services.UserService;
import com.omar.task1.api.services.UserTypeService;
import com.omar.task1.app.Const;
import com.omar.task1.fragments.HomeFragment;
import com.omar.task1.fragments.ProfileFragment;
import com.omar.task1.ui.auth.LoginActivity;
import com.omar.task1.utils.MySharedPref;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private TextView tvUsername;
    private TextView tvPassword;

    private TextView tvUsernameText;
    private TextView tvPasswordText;
    private MySharedPref prefs;
    DrawerLayout drawerLayout;

    private View navHeader;

    private ImageView headerImgProfile;
    private TextView headerTvUsername;
    private TextView headerTvEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        prefs = MySharedPref.getInstance(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_menu_24px);
//        toolbar.setTitle("Title");

//        toolbar.setLogo(R.drawable.ic_launcher);

        drawerLayout = findViewById(R.id.drawer_layout);
        final NavigationView nav_view = findViewById(R.id.nav_view);

       navHeader = nav_view.getHeaderView(0);

        intiViews();
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

    private void intiViews() {
        headerImgProfile = navHeader.findViewById(R.id.imgProfile);
        headerTvEmail = navHeader.findViewById(R.id.tvEmail);
        headerTvUsername = navHeader.findViewById(R.id.tvUsername);

        getUserType();
    }


    private void getFacebookUser() {
        tvUsernameText.setText(R.string.email_label);
        tvPasswordText.setText("");

        tvUsername.setText(prefs.getEmail());
    }





    private void getUserType(){
        String token = MySharedPref.getInstance(this).isLoggedIn();

        ApiClient.getClient(this).create(UserTypeService.class).get(token).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(
                new DisposableSingleObserver<Response<Void>>() {
                    @Override
                    public void onSuccess(Response<Void> voidResponse) {
                        try {
                            int userType = Integer.parseInt(voidResponse.headers().get(Const.USER_TYPE));
                            MySharedPref.getInstance(HomeActivity.this).setUserType(userType);
                            if (voidResponse.headers().get(Const.USER_TYPE).equals(UserType.CUSTOMER)) {
                                getUser();
                            } else {
                                getSeller();
                            }
                        }catch (Exception e){

                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                }
        );
    }

    private void getUser() {
        String token = MySharedPref.getInstance(this).isLoggedIn();



        ApiClient.getClient(this).create(UserService.class).get(token).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(
                new DisposableSingleObserver<Response<UserModel>>() {
                    @Override
                    public void onSuccess(Response<UserModel> userModelResponse) {
                        if (userModelResponse.code() == 200){
                            UserModel user = userModelResponse.body();
                            Glide.with(HomeActivity.this).load(Const.BASE_URL+"file/"+user.getProfileImage()).placeholder(R.drawable.ic_profile).into(headerImgProfile);
                            headerTvEmail.setText(user.getEmail());
                            headerTvUsername.setText(user.getUsername());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                }
        );

    }



    private void getSeller() {
        String token = MySharedPref.getInstance(this).isLoggedIn();



        ApiClient.getClient(this).create(SellerService.class).get(token).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(
                new DisposableSingleObserver<Response<SellerModel>>() {
                    @Override
                    public void onSuccess(Response<SellerModel> userModelResponse) {
                        if (userModelResponse.code() == 200){
                            SellerModel user = userModelResponse.body();
                            Glide.with(HomeActivity.this).load(Const.BASE_URL+"file/"+user.getProfileImage()).placeholder(R.drawable.ic_profile).into(headerImgProfile);
                            headerTvEmail.setText(user.getEmail());
                            headerTvUsername.setText(user.getUsername());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                }
        );

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
