package com.training.ojekonlineuser.network;

import com.training.ojekonlineuser.model.ResponseLogin;
import com.training.ojekonlineuser.model.ResponseRegisterrobo;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RestApi {

    //untuk endpoint daftar cek di api.php

        @FormUrlEncoded
        @POST("daftar")
    Call<ResponseRegisterrobo> registeruser
                (
                        @Field( "nama") String nama,
                        @Field( "email") String email,
                        @Field( "password") String password,
                        @Field( "phone") String phone
                 );

    //endpoint untuk login
    @FormUrlEncoded
    @POST("login")
    Call<ResponseLogin> loginuser(
            @Field("device") String device,
            @Field("f_email") String email,
            @Field("f_password") String pass);



}
