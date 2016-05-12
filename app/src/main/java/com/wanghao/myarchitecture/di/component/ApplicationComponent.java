package com.wanghao.myarchitecture.di.component;

import android.content.Context;

import com.wanghao.myarchitecture.CustomApplication;
import com.wanghao.myarchitecture.di.module.ApplicationModule;

import javax.inject.Singleton;

import dagger.Component;
import okhttp3.OkHttpClient;

/**
 * Created by wanghao on 2016/5/11.
 */
@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {

    Context getContext();

    OkHttpClient getOkHttpClient();

    void inject(CustomApplication mApplication);
}
