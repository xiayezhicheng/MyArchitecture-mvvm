package com.wanghao.myarchitecture.ui.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;

import com.dlut.wanghao.myarchitecture.ui.viewmodel.ViewModel;
import com.dlut.wanghao.myarchitecture.ui.widget.LoadingFooter;
import com.wanghao.myarchitecture.domain.RetrofitClient;
import com.wanghao.myarchitecture.domain.SchedulersUtils;
import com.wanghao.myarchitecture.domain.entity.Group;
import com.wanghao.myarchitecture.domain.service.HouseService;
import com.dlut.wanghao.myarchitecture.enums.TYPE_LAYOUT;
import com.wanghao.myarchitecture.ui.adapter.GroupItemAdapter;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;

/**
 * Created by wanghao on 2016/3/30.
 */
public class GroupViewModel extends BaseObservable implements ViewModel {

    private Context context;
    private HouseService mHouseService;
    private List<Group> data;
    private GroupItemAdapter adapter;
    private LoadingFooter loadingFooter;

    @Inject
    public GroupViewModel(Context context) {
        this.context = context;
        mHouseService = RetrofitClient.getInstance().create(HouseService.class);
        loadingFooter = new LoadingFooter(context);
    }

    public GroupItemAdapter getAdapter(){
        if (adapter==null){
            adapter = new GroupItemAdapter(context,data);
        }
        return adapter;
    }

    public TYPE_LAYOUT getLayoutType(){
        return TYPE_LAYOUT.TYPE_LINEAR_LAYOUT;
    }

    public LoadingFooter getLoadingFooter(){ return loadingFooter;}

    public void setData(List<Group> data){
        this.data = data;
        if (adapter==null){
            adapter = new GroupItemAdapter(context,data);
        }
    }


    /**
     * 用于获取团购房的数据
     * @param subscriber  由调用者传过来的观察者对象
     * @param page 页数
     * @param count 获取长度
     */
    public Subscription getGroupList(Subscriber<List<Group>> subscriber, int page, int count){

        return mHouseService.getGroupList(page, count)
//                .map(new HttpResultFunc<List<Subject>>())
                .retry(2)
                .compose(SchedulersUtils.<List<Group>>applyExecutorSchedulers())
                .subscribe(subscriber);

    }

    /**
     * 用来统一处理Http的resultCode,并将HttpResult的Data部分剥离出来返回给subscriber
     *
     * @param <T>   Subscriber真正需要的数据类型，也就是Data部分的数据类型
     */
//    private class HttpResultFunc<T> implements Func1<HttpResult<T>, T> {
//
//        @Override
//        public T call(HttpResult<T> httpResult) {
//            if (httpResult.getCount() == 0) {
//                throw new ApiException(100);
//            }
//            return httpResult.getSubjects();
//        }
//    }

    @Override
    public void destroy() {

    }
}
