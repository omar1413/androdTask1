package com.omar.task1.api.services;

import com.omar.task1.api.models.UserModel;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ImageService {


    @Multipart
    @POST("file")
    Single<Response<String>> uploadImage(@Part MultipartBody.Part file);
}
