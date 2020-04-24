package com.omar.task1.api.services;

import com.omar.task1.api.models.UserModel;

import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface UserTypeService {

    @GET("usertype")
    Single<Response<Void>> get(@Header("Authorization") String authorization);
}
