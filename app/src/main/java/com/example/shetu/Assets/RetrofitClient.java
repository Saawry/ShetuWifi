package com.example.shetu.Assets;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class RetrofitClient {

    //private static final String AUTH = "Basic " + Base64.encodeToString(("babu:123456").getBytes(), Base64.NO_WRAP);

    private static final String BASE_URL = "http://100.25.223.254:8080/v1/";
    private static RetrofitClient retrofitClient;
    private Retrofit retrofit;


    private RetrofitClient() {
//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(
//                        new Interceptor() {
//                            @Override
//                            public Response intercept(Chain chain) throws IOException {
//                                Request original = chain.request();
//
//                                Request.Builder requestBuilder = original.newBuilder()
//                                        .addHeader("Authorization", AUTH)
//                                        .method(original.method(), original.body());
//
//                                Request request = requestBuilder.build();
//                                return chain.proceed(request);
//                            }
//                        }
//                ).build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                //.client(okHttpClient)
                .build();
    }

    public static synchronized RetrofitClient getInstance() {
        if (retrofitClient == null) {
            retrofitClient = new RetrofitClient();
        }
        return retrofitClient;
    }

    public Api getApi() {
        return retrofit.create(Api.class);
    }
}
