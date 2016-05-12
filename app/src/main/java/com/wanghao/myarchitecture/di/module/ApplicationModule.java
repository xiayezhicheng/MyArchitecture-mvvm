package com.wanghao.myarchitecture.di.module;

import android.content.Context;

import com.wanghao.myarchitecture.domain.OkHttpClientUtil;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

/**
 * Created by wanghao on 2016/5/11.
 */
@Module
public class ApplicationModule {

    private final Context context;

    public ApplicationModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    public Context provideApplicationContext() {
        return context.getApplicationContext();
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        return OkHttpClientUtil.getClient();
    }
}
