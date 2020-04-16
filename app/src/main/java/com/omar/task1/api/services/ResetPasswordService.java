package com.omar.task1.api.services;

import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ResetPasswordService {


    @POST("password-reset/send-token")
    Single<Response<Void>> sendToken(@Query("email") String email);


    @POST("password-reset")
    Single<Response<Void>> checkToken(@Query("token") String token);


    @POST("password-reset/{token}")
    Single<Response<Void>> resetPassword(@Path("token") String token, @Query("password") String password);
}
