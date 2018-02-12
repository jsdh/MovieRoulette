package com.jdhdev.mm8.util;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


public class ImageUtils {

    public static void loadImageIntoView(Context context,
                                         ImageView view,
                                         String uri) {
        Picasso.with(context)
                .load(uri)
                .into(view);
    }

    public static void loadImageIntoView(Context context,
                                         ImageView view,
                                         String uri,
                                         @DrawableRes int placeholder) {
        Picasso.with(context)
                .load(uri)
                .placeholder(placeholder)
                .into(view);
    }

    public static void loadImageIntoView(Context context,
                                         ImageView view,
                                         String uri,
                                         Runnable onLoadCallback) {
        Picasso.with(context)
                .load(uri)
                .into(view, new Callback() {
                    @Override
                    public void onSuccess() {
                        if (onLoadCallback != null) {
                            onLoadCallback.run();
                        }
                    }

                    @Override
                    public void onError() {

                    }
                });
    }
}
