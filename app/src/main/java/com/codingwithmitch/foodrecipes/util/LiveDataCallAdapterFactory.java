package com.codingwithmitch.foodrecipes.util;

import android.arch.lifecycle.LiveData;

import com.codingwithmitch.foodrecipes.requests.responses.ApiResponse;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.CallAdapter.Factory;
import retrofit2.Retrofit;

public class LiveDataCallAdapterFactory extends Factory {


    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {

        if(Factory.getRawType(returnType) != LiveData.class){
            return null;
        }

        Type observableType = Factory.getParameterUpperBound(0, (ParameterizedType) returnType);
        Type rawObservableType = Factory.getRawType(observableType);
        if(rawObservableType != ApiResponse.class){
            throw new IllegalArgumentException("type must be a resource");
        }
        if(!(observableType instanceof ParameterizedType)){
            throw new IllegalArgumentException("resource must be parameterizeed");
        }

        Type bodyType = Factory.getParameterUpperBound(0, (ParameterizedType) observableType);
        return new LiveDataCallAdapter<Type>(bodyType);
    }

}












