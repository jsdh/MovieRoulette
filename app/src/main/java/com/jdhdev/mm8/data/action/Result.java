package com.jdhdev.mm8.data.action;

import com.jdhdev.mm8.data.Model;

public abstract class Result {
    public final Throwable error;

    public Result() {
        error = null;
    }

    public Result(Throwable t) {
        error = t;
    }

    public abstract Model updateModel(Model old);

    public boolean hasError() {
        return error != null;
    }
}
