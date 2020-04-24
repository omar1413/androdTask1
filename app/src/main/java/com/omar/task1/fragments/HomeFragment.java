package com.omar.task1.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.omar.task1.HomeActivity;
import com.omar.task1.R;
import com.omar.task1.api.ApiClient;
import com.omar.task1.api.models.SellerModel;
import com.omar.task1.api.models.UserModel;
import com.omar.task1.api.models.UserType;
import com.omar.task1.api.services.SellerService;
import com.omar.task1.api.services.UserService;
import com.omar.task1.api.services.UserTypeService;
import com.omar.task1.app.Const;
import com.omar.task1.utils.MySharedPref;
import com.omar.task1.utils.Utils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private MySharedPref prefs;
    private ImageView profileImg;

    private UserModel user;
    private SellerModel sellerModel;

    private Button shareBtn;
    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);

        getUserType();



    }


    public void share(String text){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }


//    private void getUserProfilePic(){
//        prefs = MySharedPref.getInstance(getActivity());
//        String jwt = prefs.isLoggedIn();
//        UserService userService = ApiClient.getClient(getContext()).create(UserService.class);
//        userService.get(jwt).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(
//                new DisposableSingleObserver<Response<UserModel>>() {
//                    @Override
//                    public void onSuccess(Response<UserModel> userModelResponse) {
//                        if (userModelResponse.code() == 200){
//                            UserModel userModel = userModelResponse.body();
//
//                            Glide.with(getContext()).load(Const.BASE_URL+"file/"+userModel.getProfileImage()).placeholder(R.drawable.ic_profile).into(profileImg);
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//                }
//        );
//
//    }



    private void getUserType(){
        String token = MySharedPref.getInstance(getActivity()).isLoggedIn();

        ApiClient.getClient(getContext()).create(UserTypeService.class).get(token).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(
                new DisposableSingleObserver<Response<Void>>() {
                    @Override
                    public void onSuccess(Response<Void> voidResponse) {
                        try {
                            int userType = Integer.parseInt(voidResponse.headers().get(Const.USER_TYPE));
                            MySharedPref.getInstance(getActivity()).setUserType(userType);
                            if (userType == UserType.CUSTOMER) {
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
        String token = MySharedPref.getInstance(getActivity()).isLoggedIn();



        ApiClient.getClient(getContext()).create(UserService.class).get(token).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(
                new DisposableSingleObserver<Response<UserModel>>() {
                    @Override
                    public void onSuccess(Response<UserModel> userModelResponse) {
                        if (userModelResponse.code() == 200){
                            UserModel userModel = userModelResponse.body();

                            Glide.with(getContext()).load(Const.BASE_URL+"file/"+userModel.getProfileImage()).placeholder(R.drawable.ic_profile).into(profileImg);

                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                }
        );

    }



    private void getSeller() {
        String token = MySharedPref.getInstance(getActivity()).isLoggedIn();

        shareBtn.setVisibility(View.VISIBLE);


        ApiClient.getClient(getContext()).create(SellerService.class).get(token).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(
                new DisposableSingleObserver<Response<SellerModel>>() {
                    @Override
                    public void onSuccess(Response<SellerModel> userModelResponse) {
                        if (userModelResponse.code() == 200){
                            sellerModel = userModelResponse.body();

                            Glide.with(getContext()).load(Const.BASE_URL+"file/"+sellerModel.getProfileImage()).placeholder(R.drawable.ic_profile).into(profileImg);

                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                }
        );

    }

    private void initViews(View view){



        profileImg = view.findViewById(R.id.profilePic);
        shareBtn = view.findViewById(R.id.shareBtn);

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sellerModel !=null){
                    share("http://www.omar-task1.com/"+sellerModel.getId());
                }else{
                    Utils.errorAlert(getContext(),"check you connectio");
                }
            }
        });

    }
}
