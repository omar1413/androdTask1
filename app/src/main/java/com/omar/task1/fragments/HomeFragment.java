package com.omar.task1.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.omar.task1.R;
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

        getProfilePic();
    }


    private void getProfilePic(){
        prefs = MySharedPref.getInstance(getActivity());
        String jwt = prefs.isLoggedIn();
        UserService userService = ApiClient.getClient(getContext()).create(UserService.class);
        userService.get(jwt).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(
                new DisposableSingleObserver<Response<UserModel>>() {
                    @Override
                    public void onSuccess(Response<UserModel> userModelResponse) {
                        if (userModelResponse.code() == 200){
                            UserModel userModel = userModelResponse.body();

                            Glide.with(getContext()).load(Const.BASE_URL+"file/"+userModel.getProfileImage()).into(profileImg);
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


    }
}
