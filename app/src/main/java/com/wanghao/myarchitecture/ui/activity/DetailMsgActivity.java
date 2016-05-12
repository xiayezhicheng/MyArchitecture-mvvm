package com.wanghao.myarchitecture.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.webkit.WebView;

import com.wanghao.myarchitecture.R;
import com.wanghao.myarchitecture.databinding.ActivityDetailMsgBinding;
import com.wanghao.myarchitecture.ui.viewmodel.DetailMsgViewModel;
import com.wanghao.myarchitecture.vendor.Config;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by wanghao on 2015/12/13.
 */
public class DetailMsgActivity extends ToolbarActivity {

    @Bind(R.id.collapsingToolbarLayout) CollapsingToolbarLayout
            mCollapsingToolbarLayout;
    @Bind(R.id.nested_view) NestedScrollView mNestedScrollView;
    @Bind(R.id.fragment_webview) WebView fragment_webview;

    private ActivityDetailMsgBinding binding;
    private DetailMsgViewModel viewModel;

    public static Intent newIntent(Context context, String title, String thumb) {
        Intent intent = new Intent(context, DetailMsgActivity.class);
        intent.putExtra(Config.Key_House_Title, title);
        intent.putExtra(Config.Key_House_Img, thumb);
        return intent;
    }

    @Override
    public void onCreateBinding() {
        binding = DataBindingUtil.setContentView(this,R.layout.activity_detail_msg);
    }

    @Override
    public boolean canBack() {
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadData();
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        // 如果想使用 CollapsingToolbarLayout和Toolbar，那么设置标题就使用CollapsingToolbarLayout的setTitle()
        // 为可折叠toolbar设置标题
        mCollapsingToolbarLayout.setTitle("房屋详情");

        mNestedScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        if (Build.VERSION.SDK_INT>=21){
            mNestedScrollView.setElevation(0);
        }

    }

    private void loadData() {
        Intent intent = getIntent();
        String title = intent.getStringExtra(Config.Key_House_Title);
        String imgUrl = intent.getStringExtra(Config.Key_House_Img);

        viewModel = new DetailMsgViewModel(title,imgUrl);
        binding.setViewModel(viewModel);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.destroy();
        fragment_webview.removeAllViews();
        fragment_webview.destroy();
    }
}
