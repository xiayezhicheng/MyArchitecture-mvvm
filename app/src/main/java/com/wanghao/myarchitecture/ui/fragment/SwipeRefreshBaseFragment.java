package com.wanghao.myarchitecture.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dlut.wanghao.myarchitecture.adapter.HeaderBottomItemAdapter;
import com.dlut.wanghao.myarchitecture.ui.fragment.BaseLazyLoadFragment;
import com.dlut.wanghao.myarchitecture.ui.widget.LoadingFooter;
import com.wanghao.myarchitecture.R;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by wanghao on 2015/12/14.
 */
public abstract class SwipeRefreshBaseFragment extends BaseLazyLoadFragment {

    protected PtrClassicFrameLayout mPtrFrame;

    /**
     * refresh开始时回调
     */
    public abstract void onBeginRefresh();

    public abstract void loadNextPage();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPtrFrame = (PtrClassicFrameLayout)view.findViewById(R.id.rotate_header_list_view_frame);
        setupSwipeRefresh();
    }

    private void setupSwipeRefresh() {

        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                onBeginRefresh();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });
        // the following are default settings
        mPtrFrame.setResistance(1.7f);
        mPtrFrame.setRatioOfHeaderHeightToRefresh(1.2f);
        mPtrFrame.setDurationToClose(200);
        mPtrFrame.setDurationToCloseHeader(1000);
        // default is false(release to refresh )true is mean pulling to refresh
        mPtrFrame.setPullToRefresh(false);
        // default is true
        mPtrFrame.setKeepHeaderWhenRefresh(true);
    }

    protected void setRecyclerView(final RecyclerView mRecyclerView, final LoadingFooter mLoadingFooter
                                    , final HeaderBottomItemAdapter adapter){

        mRecyclerView.setHasFixedSize(true);

        mLoadingFooter.setOnClickLoadListener(new LoadingFooter.onClickLoadListener() {
            @Override
            public void onClick() {
                loadNextPage();
            }
        });

        RecyclerView.OnScrollListener recyclerScrollListener = new RecyclerView.OnScrollListener(){

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount    = mRecyclerView.getLayoutManager().getChildCount();
                int totalItemCount      = mRecyclerView.getLayoutManager().getItemCount();
                int firstVisibleItem = 0;

                RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
                if (layoutManager instanceof GridLayoutManager){
                    firstVisibleItem = ((GridLayoutManager)layoutManager).findFirstVisibleItemPosition();
                }else if(layoutManager instanceof LinearLayoutManager){
                    firstVisibleItem = ((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition();
                }

                if (mLoadingFooter.getState() == LoadingFooter.State.Idle) {
                    if (firstVisibleItem + visibleItemCount >= totalItemCount
                            && totalItemCount != 0
                            && totalItemCount != adapter.getHeaderViewCount() + adapter.getBottomViewCount()
                            && adapter.getContentItemCount() > 0) {
                        loadNextPage();
                    }
                } else if (mLoadingFooter.getState() == LoadingFooter.State.InvalidateNet) {
                    if (!mLoadingFooter.getView().isShown()) {
                        mLoadingFooter.setState(LoadingFooter.State.Idle);
                    }
                }
            }
        };

        mRecyclerView.addOnScrollListener(recyclerScrollListener);
    }
}
