package com.training.ojekonlineuser.network;

import com.training.ojekonlineuser.model.ResponseInsertBooking;
import com.training.ojekonlineuser.model.ResponseLogin;
import com.training.ojekonlineuser.model.ResponseRegisterrobo;
import com.training.ojekonlineuser.model.ResponseWaypoints;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

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

@FormUrlEncoded
    @POST("insert_booking")
    Call<ResponseInsertBooking> insertbooking(
            @Field("f_device") String device,
            @Field("f_token") String token,
            @Field("f_jarak") float jarak,
            @Field("f_catatan") String catatan,
            @Field("f_akhir") String akhir,
            @Field("f_lngAkhir") String lonakhir,
            @Field("f_latAkhir") String latakhir,
            @Field("f_awal") String awal,
            @Field("f_lngAwal") String lonawal,
            @Field("f_latAwal") String latawal,
            @Field("f_idUser") int iduser);


    @GET("json")
    Call<ResponseWaypoints> getrute (
            @Query("origin") String lokasiawal,
            @Query("destination") String lokasitujuan
    );

}
