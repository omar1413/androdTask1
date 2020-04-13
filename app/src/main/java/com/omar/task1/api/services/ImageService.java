package com.omar.task1.api.services;

import com.omar.task1.api.models.UserModel;

import io.reactivex.Single;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ImageService {


    @GET("file/{image}")
    Single<Response<UserModel>> uploadImage(@Part("file\"; filename=\"anything.png\" ") RequestBody file);
}
