package com.omar.task1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.omar.task1.api.ApiClient;
import com.omar.task1.api.models.UserModel;
import com.omar.task1.api.services.ImageService;
import com.omar.task1.api.services.UserService;
import com.omar.task1.app.Const;
import com.omar.task1.db.AppDatabase;
import com.omar.task1.db.entity.User;
import com.omar.task1.utils.MySharedPref;
import com.omar.task1.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

public class UpdateActivity extends AppCompatActivity {

    private Button btnSave;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etEmail;
    private EditText etPhone;
    private EditText etGender;
    private EditText etAddress;
    private CircleImageView profileImage;
    private View progressLayout;

    //private UserModel user;


    private static final int CAMERA_REQUEST = 1;
    private static final int GALLERY_REQUEST = 2;
    private static final int MY_PERMISSION_CODE = 102;

    private CompositeDisposable disposable = new CompositeDisposable();

    private File file = null;


    /*
    ^                 # start-of-string
    (?=.*[0-9])       # a digit must occur at least once
    (?=.*[a-z])       # a lower case letter must occur at least once
    (?=.*[A-Z])       # an upper case letter must occur at least once
    (?=.*[@#$%^&+=])  # a special character must occur at least once you can replace with your special characters
    (?=\\S+$)         # no whitespace allowed in the entire string
    .{4,}             # anything, at least six places though
    $                 # end-of-string

     */
    private Pattern passwordPattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])\\S{8,12}$");
    //private Pattern passwordPattern = Pattern.compile("^(?=.*[a-z])$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        initViews(null);
        getUser();

    }

    private void showProgress(){
        progressLayout.setVisibility(View.VISIBLE);
    }

    private void hideProgress(){
        progressLayout.setVisibility(View.GONE);
    }

    private void getUser(){
        showProgress();
        String token = MySharedPref.getInstance(this).isLoggedIn();
        ApiClient.getClient(this).create(UserService.class).get(token).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(
                new DisposableSingleObserver<Response<UserModel>>() {
                    @Override
                    public void onSuccess(Response<UserModel> userModelResponse) {
                        if(userModelResponse.code() == 200){
                            initViews(userModelResponse.body());
                        }else{
                            finish();
                        }

                        hideProgress();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.errorAlert(UpdateActivity.this, "check your connection");
                        hideProgress();
                    }
                }
        );
    }

    private void initViews(final UserModel user) {


        if (user == null){
            progressLayout = findViewById(R.id.progressLayout);
            return;
        }


        btnSave = findViewById(R.id.btnSave);

        etUsername = findViewById(R.id.etUsername);
        etUsername.setKeyListener(null);
        etUsername.setText(user.getUsername());

        etPassword = findViewById(R.id.etPassword);

        etEmail = findViewById(R.id.etEmail);
        etEmail.setText(user.getEmail());

        etGender = findViewById(R.id.etGender);
        etGender.setText(user.getGender());

        etPhone = findViewById(R.id.etPhone);
        etPhone.setText(user.getPhone());

        etAddress = findViewById(R.id.etAddress);
        etAddress.setText(user.getAddress());

        profileImage = findViewById(R.id.profileImage);
        Glide.with(this).load(Const.BASE_URL + "file/" + user.getProfileImage()).into(profileImage);



        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser(user);
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
//                File f = new File(Environment.getExternalStorageDirectory().toString());
//                for (File temp : f.listFiles()) {
//                    if (temp.getName().equals("temp.jpg")) {
//                        f = temp;
//                        break;
//                    }
//                }
                try {
//                    Bitmap bitmap;
//                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
//                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), bitmapOptions);
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    //bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
//                    bitmap=getResizedBitmap(bitmap, 400);
                    //IDProf.setImageBitmap(bitmap);
                    // BitMapToString(bitmap);
                    profileImage.setImageBitmap(bitmap);


                    OutputStream outFile = null;
                    File picFile = new File(getCacheDir(), String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(picFile);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                        file = picFile;

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == GALLERY_REQUEST) {
                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                file = new File(picturePath);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                profileImage.setImageURI(selectedImage);
                thumbnail=getResizedBitmap(thumbnail, 400);

                OutputStream outFile = null;
                String path = android.os.Environment
                        .getExternalStorageDirectory() + "";
                File picFile = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                try {
                    outFile = new FileOutputStream(picFile);
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                    outFile.flush();
                    outFile.close();
                    file = picFile;

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.w("path of image", picturePath+"");
                //IDProf.setImageBitmap(thumbnail);
                //BitMapToString(thumbnail);
            }
        }
    }

//    public String BitMapToString(Bitmap userImage1) {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        userImage1.compress(Bitmap.CompressFormat.PNG, 60, baos);
//        byte[] b = baos.toByteArray();
//        Document_img1 = Base64.encodeToString(b, Base64.DEFAULT);
//        return Document_img1;
//    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private void selectImage(){
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    cameraImage();
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
//                    startActivityForResult(intent, 1);
                }
                else if (options[item].equals("Choose from Gallery"))
                {
                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, GALLERY_REQUEST);
                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    private void cameraImage(){
        if (!hasPermissions(this,Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE))
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_PERMISSION_CODE);
            //requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
        }
        else
        {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, " permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean validate(String email,String phone,String address,String gender){

        boolean valid = true;


        if (email.isEmpty()){
            etEmail.setError(getString(R.string.email_empty_error));
            valid = false;
        }

        if(phone.isEmpty()){
            etPhone.setError(getString(R.string.phone_empty_error));
            valid = false;
        }

        if (address.isEmpty()){
            etAddress.setError(getString(R.string.address_empty_error));
            valid = false;
        }

        if(gender.isEmpty()){
            etGender.setError(getString(R.string.gender_empty_error));
            valid = false;
        }




        return valid;
    }

    private void updateUser(UserModel user) {


        //final String username = etUsername.getText().toString().trim();
        String username = user.getUsername();
        final String password = etPassword.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        final String phone = etPhone.getText().toString().trim();
        final String address = etAddress.getText().toString().trim();
        final String gender = etGender.getText().toString().trim();
        user.setPassword(password);
        user.setEmail(email);
        user.setPhone(phone);
        user.setGender(gender);
        user.setAddress(address);

        if(!validate(email,phone,address,gender)) return;



        update(user);
//        AppDatabase.getInstance(this).getUserDao().getUser(username).observe(this, new Observer<User>() {
//            @Override
//            public void onChanged(User user) {
//                if (user == null) {
//                    new Thread() {
//                        @Override
//                        public void run() {
//                            AppDatabase.getInstance(RegisterActivity.this).getUserDao().insert(new User(username, password));
//
//
//                            MySharedPref.getInstance(RegisterActivity.this).saveLogIn(username);
//
//
//
//                        }
//                    }.start();
//                    goToHomeActivity();
//
//                } else {
//                    etUsername.setError(getString(R.string.user_exist_error));
//                }
//            }
//        });


    }

    private void update(final UserModel user){
        showProgress();
        if(file!=null) {
            ImageService imageService = ApiClient.getClient(this).create(ImageService.class);

            // Create a request body with file and image media type
            RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
            // Create MultipartBody.Part using file request-body,file name and part name
            MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), fileReqBody);

            imageService.uploadImage(part).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(
                    new DisposableSingleObserver<Response<String>>() {
                        @Override
                        public void onSuccess(Response<String> stringResponse) {
                            if(stringResponse.code() == 200) {
                                user.setProfileImage(stringResponse.body());
                                updateUserData(user);
                            }else{
                                hideProgress();
                                Utils.errorAlert(UpdateActivity.this,"Server Error in upload file");
                                Log.d("",stringResponse.errorBody().toString());
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideProgress();
                            Utils.errorAlert(UpdateActivity.this,"check your connection");
                            Log.d("","");
                        }
                    }
            );

        }else{
            updateUserData(user);
        }
    }

    private void updateUserData(UserModel user){
        UserService userService = ApiClient.getClient(this).create(UserService.class);
        disposable.add(
                userService.update(MySharedPref.getInstance(this).isLoggedIn(),user).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(
                        new DisposableSingleObserver<Response<UserModel>>(){

                            @Override
                            public void onSuccess(Response<UserModel> response) {
                                if(response.code() == 200){
                                    //String jwt = response.headers().get("Authorization");
                                    //MySharedPref.getInstance(RegisterActivity.this).saveLogIn(jwt);
                                    //goToHomeActivity();
                                    Utils.errorAlert(UpdateActivity.this,"user have been updated ");
                                }else{
                                    if(response.body() != null && response.body().getError() != null)
                                        Toast.makeText(UpdateActivity.this, response.body().getError() , Toast.LENGTH_SHORT).show();
                                    else{
                                        Utils.errorAlert(UpdateActivity.this,"Server Error ");                                    }
                                }
                                hideProgress();
                            }

                            @Override
                            public void onError(Throwable e)
                            {
                                hideProgress();
                                Utils.errorAlert(UpdateActivity.this,"check your connection");
                                Log.d("0","" + e.getMessage());
                            }
                        }
                )
        );
    }

    private void goToHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);


        finish();

    }

    @Override
    public void onBackPressed() {
        goToLoginActivity();

    }

    private void  goToLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        finish();
    }
}
