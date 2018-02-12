package com.jdhdev.mm8.util;

import android.util.Log;

import io.reactivex.ObservableTransformer;

public class RxUtils {

    private static final boolean ENABLE_LOGGING = false;

    public static <T> ObservableTransformer<T, T> logSource (final String source) {
        if (ENABLE_LOGGING) {
            return MPObservable -> MPObservable.doOnNext(data -> {
                Log.w(source, data.toString());
            });
        } else {
            return MPObservable -> MPObservable;
        }
    }
}
