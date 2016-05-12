package com.wanghao.myarchitecture;

import android.app.Application;

import com.dlut.wanghao.myarchitecture.utils.SharedPreferencesUtils;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Supplier;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.wanghao.myarchitecture.di.component.DaggerApplicationComponent;
import com.wanghao.myarchitecture.di.module.ApplicationModule;

import javax.inject.Inject;

import me.drakeet.library.CrashWoodpecker;
import okhttp3.OkHttpClient;

/**
 * Created by wanghao on 2015/9/23.
 */
public class CustomApplication extends Application {

    private RefWatcher mRefWatcher;

    @Inject
    OkHttpClient mOkHttpClient;

    @Override
    public void onCreate() {
        super.onCreate();

        DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build()
                .inject(this);

        //LeakCanary
        mRefWatcher = LeakCanary.install(this);
        //CrashWoodpecker
        CrashWoodpecker.init(this);
        //初始化SharedPreferences
        SharedPreferencesUtils.getInstance().Builder(this);
        // Occasional EOFException when reading cached file
        // 1. Try to load lots of previously cached images in a ListView by
        // quickly scrolling back and forth
        // 2. Some images will fail to load
        System.setProperty("http.keepAlive", "false");

        initImageLoader();

    }

    private static final int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();

    public static final int MAX_DISK_CACHE_SIZE = 50 * ByteConstants.MB;
    public static final int MAX_DISK_CACHE_SIZE_LOW = 20 * ByteConstants.MB;
    public static final int MAX_DISK_CACHE_SIZE_VERY_LOW = 5 * ByteConstants.MB;
    public static final int MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 8;

    private void initImageLoader() {

        final MemoryCacheParams bitmapCacheParams = new MemoryCacheParams(
                MAX_MEMORY_CACHE_SIZE, // Max total size of elements in the cache
                Integer.MAX_VALUE,                     // Max entries in the cache
                MAX_MEMORY_CACHE_SIZE, // Max total size of elements in eviction queue
                Integer.MAX_VALUE,                     // Max length of eviction queue
                Integer.MAX_VALUE);
        ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
                .newBuilder(this, mOkHttpClient)
                .setProgressiveJpegConfig(new SimpleProgressiveJpegConfig())
                .setBitmapMemoryCacheParamsSupplier(
                        new Supplier<MemoryCacheParams>() {
                            public MemoryCacheParams get() {
                                return bitmapCacheParams;
                            }
                        })
                .setMainDiskCacheConfig(
                        DiskCacheConfig.newBuilder(this)
                                .setMaxCacheSize(MAX_DISK_CACHE_SIZE)
                                .setMaxCacheSizeOnLowDiskSpace(MAX_DISK_CACHE_SIZE_LOW)
                                .setMaxCacheSizeOnVeryLowDiskSpace(MAX_DISK_CACHE_SIZE_VERY_LOW)
                                .build()).build();
        Fresco.initialize(this, config);

    }

}
