package com.wanghao.myarchitecture.ui.viewmodel;

import com.dlut.wanghao.myarchitecture.ui.viewmodel.ViewModel;

/**
 * Created by wanghao on 2016/3/27.
 */
public class DetailMsgViewModel implements ViewModel {

    private final String  title;
    private final String  imgUrl;

    public DetailMsgViewModel(String title, String imgUrl) {
        this.title = title;
        this.imgUrl = imgUrl;
    }

    public String getTitle(){
        return this.title;
    }

    public String getImgUrl(){
        return this.imgUrl;
    }

    @Override
    public void destroy() {

    }
}
