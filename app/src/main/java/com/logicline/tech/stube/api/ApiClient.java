package com.logicline.tech.stube.api;

import androidx.annotation.NonNull;

import com.logicline.tech.stube.constants.ApiConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.BuildConfig;

import java.io.IOException;
import java.net.InetAddress;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofit;

    public static Retrofit getInstance(String baseUrl) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .callTimeout(Duration.ZERO)
                    .readTimeout(360, TimeUnit.SECONDS)
                    .connectTimeout(360, TimeUnit.SECONDS)
                    .writeTimeout(360, TimeUnit.SECONDS)
                    .dns(hostname -> Single.fromCallable(() -> Arrays.asList(InetAddress.getAllByName(hostname)))
                            .timeout(300, TimeUnit.SECONDS)
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.computation())
                            .onErrorReturnItem(new ArrayList<>())
                            .blockingGet());

            if (BuildConfig.DEBUG) {
                builder.addInterceptor(interceptor);
            }
            builder.addInterceptor(new Interceptor() {
                @NonNull
                @Override
                public Response intercept(@NonNull Chain chain) throws IOException {
                    Request originalRequest = chain.request();
                    HttpUrl originalUrl = originalRequest.url();
                    HttpUrl url = originalUrl.newBuilder()
                            .addQueryParameter("maxResults", ApiConstants.HOME_VIDEO_API_MAX_RESULT + "")
                            .build();

                    Request request = originalRequest.newBuilder()
                            .url(url)
                            .build();

                    return chain.proceed(request);
                }
            });

            client = builder.build();

        }

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create());

        if (client != null) {
            builder.client(client);
        }


        retrofit = builder.build();

        return retrofit;

    }
}
