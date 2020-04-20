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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.omar.task1.R;
import com.omar.task1.UpdateActivity;
import com.omar.task1.api.ApiClient;
import com.omar.task1.api.models.UserModel;
import com.omar.task1.api.services.UserService;
import com.omar.task1.app.Const;
import com.omar.task1.utils.MySharedPref;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private ImageView imgProfile;
    private TextView tvUsername;
    private TextView tvEmail;
    private TextView tvPhone;
    private TextView tvGender;
    private TextView tvAddress;
    private Button btnEdit;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View v){
        imgProfile = v.findViewById(R.id.imgProfile);
        tvUsername = v.findViewById(R.id.tvUsername);
        tvEmail = v.findViewById(R.id.tvEmail);
        tvGender = v.findViewById(R.id.tvGender);
        tvAddress = v.findViewById(R.id.tvAddress);
        tvPhone = v.findViewById(R.id.tvPhone);
        btnEdit = v.findViewById(R.id.btnEdit);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToEditActivity();
            }
        });

        getUser();
    }

    private void goToEditActivity(){
        Intent intent = new Intent(getActivity(), UpdateActivity.class);
        startActivity(intent);
    }

    private void getUser(){
        String jwt = MySharedPref.getInstance(getActivity()).isLoggedIn();
        UserService userService = ApiClient.getClient(getContext()).create(UserService.class);
        userService.get(jwt).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(
                new DisposableSingleObserver<Response<UserModel>>() {
                    @Override
                    public void onSuccess(Response<UserModel> userModelResponse) {
                        if(userModelResponse.code() == 200) {

                            UserModel user = userModelResponse.body();
                            Glide.with(getContext()).load(Const.BASE_URL + "file/" + user.getProfileImage()).placeholder(R.drawable.ic_profile).into(imgProfile);
                            tvUsername.setText(user.getUsername());
                            tvEmail.setText(user.getEmail());
                            tvPhone.setText(user.getPhone());
                            tvAddress.setText(user.getAddress());
                            tvGender.setText(user.getGender());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
}
