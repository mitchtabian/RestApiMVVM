package com.codingwithmitch.foodrecipes.requests;

import com.codingwithmitch.foodrecipes.util.Constants;
import com.codingwithmitch.foodrecipes.util.LiveDataCallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.codingwithmitch.foodrecipes.util.Constants.NETWORK_TIMEOUT;

/**
 * Modeled after @brittbarak's example on Github
 * https://github.com/brittBarak/NetworkingDemo
 * https://twitter.com/brittbarak
 */
public class ServiceGenerator {

    private static OkHttpClient okHttpClient =
            new OkHttpClient.Builder()
                    .readTimeout(NETWORK_TIMEOUT, TimeUnit.MILLISECONDS)
                    .writeTimeout(NETWORK_TIMEOUT, TimeUnit.MILLISECONDS)
                    .callTimeout(NETWORK_TIMEOUT, TimeUnit.MILLISECONDS)
                    .connectTimeout(NETWORK_TIMEOUT, TimeUnit.MILLISECONDS).build();


    private static Retrofit.Builder retrofitBuilder =
            new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = retrofitBuilder.build();

    private static RecipeApi recipeApi = retrofit.create(RecipeApi.class);

    public static RecipeApi getRecipeApi(){
        return recipeApi;
    }
}
