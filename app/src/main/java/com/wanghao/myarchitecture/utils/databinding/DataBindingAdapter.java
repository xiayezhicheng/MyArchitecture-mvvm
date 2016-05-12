package com.wanghao.myarchitecture.utils.databinding;

import android.content.Context;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.wanghao.myarchitecture.domain.entity.Group;
import com.wanghao.myarchitecture.domain.entity.Rental;
import com.wanghao.myarchitecture.ui.activity.DetailMsgActivity;

/**
 * Created by wanghao on 2016/3/25.
 */
public class DataBindingAdapter {

    @BindingAdapter({"bind:imgUrl"})
    public static void loadImage(SimpleDraweeView simpleDraweeView, String imageUrl) {
        if (!TextUtils.isEmpty(imageUrl)){
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(imageUrl))
                    .setProgressiveRenderingEnabled(true)
                    .build();

            DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(simpleDraweeView.getController())
                    .build();
            simpleDraweeView.setController(draweeController);
        }
    }

   @BindingAdapter({"bind:groupClick"})
    public static void setOnGroupClick(final View view, final Group group){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = view.getContext();
                Intent intent = DetailMsgActivity.newIntent(context, group.getTitle(), group.getThumb());
                context.startActivity(intent);
            }
        });
    }

    @BindingAdapter({"bind:rentalClick"})
    public static void setOnRentalClick(final View view, final Rental rental){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = view.getContext();
                Intent intent = DetailMsgActivity.newIntent(context, rental.getTitle(), rental.getThumb());
                context.startActivity(intent);
            }
        });
    }
}
