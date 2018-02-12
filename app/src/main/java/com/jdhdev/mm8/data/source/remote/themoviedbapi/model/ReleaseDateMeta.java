package com.jdhdev.mm8.data.source.remote.themoviedbapi.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReleaseDateMeta {
    @SerializedName("iso_3166_1")
    public String iso31661;
    @SerializedName("release_dates")
    public List<ReleaseDate> releaseDates = null;
}
