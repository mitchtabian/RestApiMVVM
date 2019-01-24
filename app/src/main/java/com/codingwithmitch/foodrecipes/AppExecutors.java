package com.codingwithmitch.foodrecipes;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class AppExecutors {

    static AppExecutors instance = new AppExecutors();

    private final Executor mDiskIO = Executors.newSingleThreadExecutor();

//    private final Executor mNetworkIO = Executors.newFixedThreadPool(3);

    private final ScheduledExecutorService mNetworkIO = Executors.newScheduledThreadPool(3);

    public Executor diskIO() {
        return mDiskIO;
    }

//    public Executor networkIO() {
//        return mNetworkIO;
//    }
    public ScheduledExecutorService networkIO() {
        return mNetworkIO;
    }


    public static AppExecutors get(){
        return instance;
    }
}