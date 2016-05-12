package com.wanghao.myarchitecture.ui.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dlut.wanghao.myarchitecture.ui.widget.LoadingFooter;
import com.dlut.wanghao.myarchitecture.ui.widget.LoadingLayout;
import com.dlut.wanghao.myarchitecture.utils.NetUtils;
import com.wanghao.myarchitecture.R;
import com.wanghao.myarchitecture.databinding.ListGroupBinding;
import com.wanghao.myarchitecture.di.component.DaggerFragmentComponent;
import com.wanghao.myarchitecture.di.module.GroupModule;
import com.wanghao.myarchitecture.di.module.RentalModule;
import com.wanghao.myarchitecture.domain.entity.Group;
import com.wanghao.myarchitecture.ui.adapter.GroupItemAdapter;
import com.wanghao.myarchitecture.ui.viewmodel.GroupViewModel;
import com.wanghao.myarchitecture.vendor.Config;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import rx.Subscriber;

public class GroupFragment extends SwipeRefreshBaseFragment {

    private Context context;
    private LoadingFooter mLoadingFooter;
    protected ArrayList<Group> data;
    private GroupItemAdapter adapter;
    private boolean isInitLoad = true;//是否第一次加载
    private int mPage = 0;

    @Inject
    GroupViewModel groupViewModel;

    @Bind(R.id.rotate_header_list_view_frame)
    PtrClassicFrameLayout mPtrFrame;
    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @Bind(R.id.loading_layout)
    LoadingLayout loadingLayout;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerFragmentComponent
                .builder()
                .groupModule(new GroupModule(getActivity()))
                .rentalModule(new RentalModule(getActivity()))
                .build().inject(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        ListGroupBinding binding = DataBindingUtil.inflate(inflater,R.layout.list_group,container,false);
        binding.setViewModel(groupViewModel);
        View view  = binding.getRoot();
        ButterKnife.bind(this,view);

        //加载视图展示
        loadingLayout.setOnRetryClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isInitLoad = true;
                loadFirstPage();
            }
        });

        data = new ArrayList<Group>();
        groupViewModel.setData(data);
        adapter = groupViewModel.getAdapter();

        mLoadingFooter = groupViewModel.getLoadingFooter();

        //监听RecyclerView滑动动作
        setRecyclerView(mRecyclerView,mLoadingFooter,adapter);

        return view;
    }

    private void loadData(final int page) {

        final boolean isRefreshFromTop = page == 0;

        Subscriber subscriber = new Subscriber<List<Group>>() {

            @Override
            public void onNext(List<Group> lists) {
                if (isRefreshFromTop) {
                    data.clear();
                    if (isInitLoad) {
                        if (lists.isEmpty()){
                            loadingLayout.showEmpty();
                        }else {
                            loadingLayout.showContent();
                        }
                    }else {
                        mPtrFrame.refreshComplete();
                    }
                }
                if (lists.size() < Config.LIST_COUNT) {
                    mLoadingFooter.setState(LoadingFooter.State.TheEnd);
                } else {
                    mLoadingFooter.setState(LoadingFooter.State.Idle);
                }
                data.addAll(lists);
            }

            @Override
            public void onCompleted() {
                mPage = page;
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {
                if (isRefreshFromTop) {
                    if(isInitLoad){
                        loadingLayout.showError();
                    }else{
                        mPtrFrame.refreshComplete();
                    }
                } else {
                    mLoadingFooter.setState(LoadingFooter.State.Idle);
                }
                if (!NetUtils.isConnect(getActivity())) {
                    if(isRefreshFromTop){
                        if (data.isEmpty()) {
                            loadingLayout.showUnnet();
                        }else {
                            Toast.makeText(getActivity(), "网络异常，请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        mLoadingFooter.setState(LoadingFooter.State.InvalidateNet);
                    }
                }
            }
        };

        addSubscription(groupViewModel.getGroupList(subscriber, page, Config.LIST_COUNT));
    }

    @Override
    public void onBeginRefresh() {
        isInitLoad = false;
        loadFirstPage();
    }

    @Override
    public void loadNextPage() {
        mLoadingFooter.setState(LoadingFooter.State.Loading);
        loadData(mPage+1);
    }

    @Override
    public void loadFirstPage() {
        if (isInitLoad) loadingLayout.showLoading();
        mPage = 0;
        loadData(mPage);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        groupViewModel.destroy();
        ButterKnife.unbind(this);
    }
}
