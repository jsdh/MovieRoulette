package com.jdhdev.mm8.data.source.remote.themoviedbapi.model;

import com.google.gson.annotations.SerializedName;


public class ReleaseDate {
    @SerializedName("certification")
    public String certification;
    @SerializedName("iso_639_1")
    public String iso6391;
    @SerializedName("release_date")
    public String releaseDate;
    @SerializedName("type")
    public int type;
    @SerializedName("note")
    public String note;
}
