package com.dlut.wanghao.myarchitecture.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by wanghao on 2016/5/12.
 */
public abstract class BaseLazyLoadFragment extends BaseFragment {

    /**
     * 第一次载入时加载数据
     */
    public abstract void loadFirstPage();

    private boolean isPrepare = false;//是否初始化

    public boolean isFirstLoad = true;//是否第一次加载

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        isPrepare = true;
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            onVisible();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            onVisible();
        }
    }

    private void onVisible() {
        if (isFirstLoad && isPrepare) {
            loadFirstPage();
            isFirstLoad = false;
        }
    }

}
