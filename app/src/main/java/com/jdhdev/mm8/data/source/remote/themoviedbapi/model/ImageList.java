package com.jdhdev.mm8.data.source.remote.themoviedbapi.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ImageList {
    @SerializedName("backdrops")
    public List<ImageInfo> backdrops;
    @SerializedName("posters")
    public List<ImageInfo> posters;
}
