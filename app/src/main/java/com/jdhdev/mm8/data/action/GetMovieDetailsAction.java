package com.jdhdev.mm8.data.action;


public class GetMovieDetailsAction extends Action {
    public final int id;
    public GetMovieDetailsAction(int mId) {
        id = mId;
    }

    @Override
    public String toString() {
        return "{GetMovieDetailsAction: " + id + "}";
    }
}
