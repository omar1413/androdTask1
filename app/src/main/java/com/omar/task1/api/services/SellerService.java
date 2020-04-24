package com.omar.task1.api.services;

import com.omar.task1.api.models.SellerModel;
import com.omar.task1.api.models.UserLogin;
import com.omar.task1.api.models.UserModel;

import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface SellerService {


    @POST("sellers/sign-up")
    Single<Response<SellerModel>> signup(@Body SellerModel user);


    @GET("sellers")
    Single<Response<SellerModel>> get(@Header("Authorization") String authorization);


    @PUT("sellers")
    Single<Response<SellerModel>> update(@Header("Authorization") String authorization,@Body SellerModel user);
}
