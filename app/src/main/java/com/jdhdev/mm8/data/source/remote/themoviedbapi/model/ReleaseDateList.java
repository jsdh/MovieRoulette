package com.jdhdev.mm8.data.source.remote.themoviedbapi.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReleaseDateList {
    @SerializedName("results")
    public List<ReleaseDateMeta> results = null;
}
