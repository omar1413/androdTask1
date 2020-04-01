package com.omar.task1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.omar.task1.db.AppDatabase;
import com.omar.task1.db.entity.User;
import com.omar.task1.utils.MySharedPref;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {


    private static final String TAG = LoginActivity.class.getName();

    private TextView tvRegister;
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;

    private MySharedPref prefUtil;
    private CallbackManager callbackManager = CallbackManager.Factory.create();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefUtil = MySharedPref.getInstance(this);

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken !=null && !accessToken.isExpired() && prefUtil.getToken() == null) {
            LoginManager.getInstance().logOut();
        }

        if(prefUtil.getToken() != null){
            loginWithFacebook();
        }

        setContentView(R.layout.activity_login);



        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setPermissions(Arrays.asList(
                "public_profile", "email"));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Log.d("facebooooook", "success");
                        Log.d("facebooooook", "success");
                        Log.d("facebooooook", "success");
                        AccessToken token = loginResult.getAccessToken();
                        prefUtil.saveAccessToken(token.getToken());
                                GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject jsonObject,
                                                            GraphResponse response) {
                                        Log.d("facebooooook", "success");
                                        // Getting FB User Data
                                        Bundle facebookData = getFacebookData(jsonObject);
                                        loginWithFacebook();

                                    }
                                });

                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,first_name,last_name,email,gender");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Log.d("facebooooook", "cancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.d("facebooooook", "excep + " + exception.getLocalizedMessage());
                    }
                });



        //Log.d("facebooooook", AccessToken.getCurrentAccessToken().getApplicationId());
        Log.d(TAG, "i am here in firist");

        checkLogin();

        initViews();

    }

    private Bundle getFacebookData(JSONObject object) {
        Bundle bundle = new Bundle();

        try {
            String id = object.getString("id");
            Log.d("facebooooook", id);
            URL profile_pic;
            try {
                profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?type=large");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();

                return null;
            }
            Log.d("facebooooook", "" + (object.has("email")));
//            bundle.putString("idFacebook", id);
//            if (object.has("first_name"))
//                bundle.putString("first_name", object.getString("first_name"));
//            if (object.has("last_name"))
//                bundle.putString("last_name", object.getString("last_name"));
//            if (object.has("email"))
//                bundle.putString("email", object.getString("email"));
//            if (object.has("gender"))
//                bundle.putString("gender", object.getString("gender"));


            Log.d("facebooooook", "" + (object.has("email")));
            prefUtil.saveFacebookUserInfo(object.getString("first_name"),
                    object.getString("last_name"),object.getString("email"), profile_pic.toString());

        } catch (Exception e) {
            Log.d("facebooooook", "" + (object.has("email")));
            Log.d(TAG, "BUNDLE Exception : "+e.toString());
        }

        return bundle;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initViews(){
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegisterActivity();
            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

    }

    private void goToRegisterActivity(){

        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);

        finish();
    }

    private void login(){
        String username = etUsername.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();

        LiveData<User> user = AppDatabase.getInstance(this).getUserDao().getUser(username);
        Log.d(TAG, "i am here in login");
        Log.d(TAG, "i am here in login -" + username + "-");
        Log.d(TAG, "i am here in login  -" + password + "-");

        user.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if(user != null){
                    if (user.getPassword().equals(password)){
                        goToHomeActivity(user.getUsername());
                    }else{
                        etPassword.setError(getString(R.string.wrong_password_error));
                    }


                }else{
                    Log.d(TAG, "i am here in error");
                    etUsername.setError(getString(R.string.wrong_user_name_error));

                    //Toast.makeText(LoginActivity.this, "error", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void goToHomeActivity(String username){
        MySharedPref.getInstance(this).saveLogIn(username);

        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        finish();
    }

    private boolean checkLogin(){
        String username =  MySharedPref.getInstance(this).isLoggedIn();

        if(username != null){
            goToHomeActivity(username);
            return true;
        }

        return false;
    }


    private void loginWithFacebook(){

        Intent intent = new Intent(this,HomeActivity.class);


        startActivity(intent);

        finish();
    }

}
