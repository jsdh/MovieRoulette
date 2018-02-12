package com.jdhdev.mm8.data.source.remote.themoviedbapi.model;

import com.google.gson.annotations.SerializedName;

public class ImageInfo {
    @SerializedName("aspect_ratio")
    public double aspectRatio;
    @SerializedName("file_path")
    public String filePath;
    @SerializedName("height")
    public int height;
    @SerializedName("iso_639_1")
    public String iso6391;
/*
    @SerializedName("vote_average")
    public int voteAverage;
    @SerializedName("vote_count")
    public int voteCount;
    */
    @SerializedName("width")
    public int width;

}
