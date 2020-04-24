package com.omar.task1.api.services;

import com.omar.task1.api.models.UserLogin;
import com.omar.task1.api.models.UserModel;


import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserService {

    @POST("users/sign-up/{sellerId}")
    Single<Response<UserModel>> signup(@Body UserModel user,@Path("sellerId") String sellerId);


    @GET("users")
    Single<Response<UserModel>> get(@Header("Authorization") String authorization);

    @POST("users/sign-in")
    Single<Response<Void>> signin(@Body UserLogin user);


    @PUT("users")
    Single<Response<UserModel>> update(@Header("Authorization") String authorization,@Body UserModel user);

}
