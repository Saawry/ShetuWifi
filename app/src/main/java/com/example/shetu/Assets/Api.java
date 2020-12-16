package com.example.shetu.Assets;


import com.example.shetu.models.ActivePackagesResponse;
import com.example.shetu.models.AllPkgInfoResponse;
import com.example.shetu.models.DfltResponse;
import com.example.shetu.models.GetnetResponse;
import com.example.shetu.models.OtpRqResponse;
import com.example.shetu.models.PurchaseResponse;
import com.example.shetu.models.RegisterResponse;
import com.example.shetu.models.AuthUsersResponse;
import com.example.shetu.models.UpdateResponse;
import com.example.shetu.models.VerifyResponse;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {

    @FormUrlEncoded
    @POST("authenticate")
    Call<OtpRqResponse> requestOTP(
            @Field("mobile") String mobile
    );

    @FormUrlEncoded
    @POST("verify")
    Call<VerifyResponse> Verify(
            @Field("mobile") String mobile,
            @Field("otp") String otp,
            @Field("mac_address") String mac_address
    );

    @FormUrlEncoded
    @PUT("user")
    Call<DfltResponse> UpdateInfo(
            @Header("Authorization") String token,
            @Field("name") String UserName,
            @Field("birthdate") String UserDob
    );


    @GET("auth-user")
    Call<AuthUsersResponse> getUserDetails(@Header("Authorization") String token);


    @GET("packages")
    Call<AllPkgInfoResponse> getAllPkgDetails(@Header("Authorization") String token);



    @FormUrlEncoded
    @POST("purchase/{id}")
    Call<PurchaseResponse> PurchasePkg(
            @Path("id") String id,
            @Header("Authorization") String token,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("auto_renew") String renewOn
    );


    @GET("user/packages")
    Call<ActivePackagesResponse> GetMyActivepPkgs(
            @Header("Authorization") String token
    );



    @PUT("auto_renew/{id}")
    Call<DfltResponse> SetRenuePkg(
            @Path("id") String id,
            @Header("Authorization") String token
    );



//    @FormUrlEncoded
//    @POST("register")
//    Call<RegisterResponse> Register(
//            @Field("name") String name,
//            @Field("mobile") String mobile,
//            @Field("mac_address") String mac_address
//    );



//    @FormUrlEncoded
//    @POST("purchase/2")
//    Call<PurchaseResponse> PurchasePkg(
//            @Header("Authorization") String token,
//            @Field("latitude") String latitude,
//            @Field("longitude") String longitude
//    );

//
//    @FormUrlEncoded
//    @POST("purchase/{id}")
//    Call<PurchaseResponse> PurchasePkg(
//            @Path("id") String id,
//            @Header("Authorization") String token,
//            @Field("latitude") String latitude,
//            @Field("longitude") String longitude
//    );




    @FormUrlEncoded
    @POST("mac")
    Call<DfltResponse> StoreMac(@Header("Authorization") String token,@Field("mac_address") String mac_address);


    @FormUrlEncoded
    @POST("session")
    Call<DfltResponse> StoreSession(
            @Field("user_id") String user_id,
            @Field("mac_address") String mac_address,
            @Field("connected_at") String connected_at,
            @Field("disconnected_at") String disconnected_at,
            @Field("bandwidth_used") String bandwidth_used

    );

    @FormUrlEncoded
    @POST("network")
    Call<DfltResponse> StoreNetwork(
            @Field("ssid") String ssid,
            @Field("password") String password
    );

    @GET("network")
    Call<GetnetResponse> GetNetwork();


    @DELETE("logout")
    Call<DfltResponse> logout(@Header("Authorization") String token);


    @GET("force-update?version=0.0.1")
    Call<UpdateResponse> UpdateCheck() ;








//
//    @DELETE("network")
//    Call<DfltResponse> DeleteNetwork();


//    @GET("get/otp?mobile=01930997511")
//    Call<DfltResponse> getOTP();


    //    @FormUrlEncoded
//    @POST("request-otp")
//    Call<RequestOtpResponse> requestOTP(
//            @Field("name") String name,
//            @Field("mobile") String mobile,
//            @Field("mac_address") String mac_address
//
//    );
//    @FormUrlEncoded
//    @PUT("updateuser/{id}")
//    Call<LoginResponse> updateUser(
//            @Path("id") int id,
//            @Field("email") String email,
//            @Field("name") String name,
//            @Field("school") String school
//    );
//    @FormUrlEncoded
//    @POST("userlogin")
//    Call<LoginResponse> userLogin(
//            @Field("email") String email,
//            @Field("password") String password
//    );
//    @FormUrlEncoded
//    @PUT("updatepassword")
//    Call<DefaultResponse> updatePassword(
//            @Field("currentpassword") String currentpassword,
//            @Field("newpassword") String newpassword,
//            @Field("email") String email
//    );


//Call<LoginResponse> deleteUser(@Path("id") int id);

}
