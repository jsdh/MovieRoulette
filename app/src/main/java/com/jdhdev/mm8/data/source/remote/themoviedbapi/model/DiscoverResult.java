package com.jdhdev.mm8.data.source.remote.themoviedbapi.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class DiscoverResult {
    public int page;
    public List<DiscoverMovie> results = null;
    @SerializedName("total_results")
    public int totalResults;
    @SerializedName("total_pages")
    public int totalPages;

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DR{");
        sb.append("page: ").append(page).append(", ");
        sb.append("totalResults: ").append(totalResults).append(", ");
        sb.append("totalPages: ").append(totalPages).append(", ");
        sb.append("[");
        for (DiscoverMovie dm : results) {
            sb.append(dm);
        }
        sb.append("]}");

        return sb.toString();
    }
}
