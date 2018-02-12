package com.jdhdev.mm8.util;

import android.content.Context;
import android.content.res.Resources;

import com.jdhdev.mm8.R;
import com.jdhdev.mm8.data.Movie;
import com.jdhdev.mm8.data.source.remote.themoviedbapi.model.ImageInfo;
import com.jdhdev.mm8.data.source.remote.themoviedbapi.model.ImageList;
import com.jdhdev.mm8.data.source.remote.themoviedbapi.model.ReleaseDate;
import com.jdhdev.mm8.data.source.remote.themoviedbapi.model.ReleaseDateList;
import com.jdhdev.mm8.data.source.remote.themoviedbapi.model.ReleaseDateMeta;
import com.jdhdev.mm8.data.source.remote.themoviedbapi.model.VideoList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class TheMovieDBUtils {
    private static final String RELEASE_DATE_API_FORMAT = "yyyy-MM-dd";
    private static final String RELEASE_DATE_UI_FORMAT = "MMM d, y";

    public static final String IMAGE_HOST = "https://image.tmdb.org/t/p/w780";

    public static String parseReleaseDate(String date) {
        String formattedDate = "";
        try {
            SimpleDateFormat parser = new SimpleDateFormat(RELEASE_DATE_API_FORMAT, Locale.US);
            SimpleDateFormat formatter = new SimpleDateFormat(RELEASE_DATE_UI_FORMAT, Locale.US);
            Date d = parser.parse(date);
            formattedDate = formatter.format(d);
        } catch (ParseException pe) {
            System.out.println(pe);
        }

        return formattedDate;
    }

    public static String getFullImagePath(String resId) {
        if (resId == null || resId.length() == 0) return null;
        return IMAGE_HOST + resId;
    }

    public static String getEsrbRating(ReleaseDateList dateList) {
        if (dateList != null && dateList.results != null) {
            for (ReleaseDateMeta rdm : dateList.results) {
                if ("US".equalsIgnoreCase(rdm.iso31661)) {
                    if (rdm.releaseDates != null) {
                        for (ReleaseDate date : rdm.releaseDates) {
                            if (date.certification != null && date.certification.length() > 0) {
                                return date.certification;
                            }
                        }
                    }
                }
            }
        }

        return "NR";
    }

    public static List<String> getImageUrisList(ImageList images) {
        List<String> imageList = new ArrayList<>();
        if (images != null) {
            fillImageUris(images.backdrops, imageList);
//            fillImageUris(images.posters, imageList);
        }
        return imageList;
    }

    private static void fillImageUris(List<ImageInfo> images, List<String> result) {
        if (images != null) {
            for (ImageInfo info : images) {
                if (info.filePath != null && info.filePath.length() > 0) {
                    result.add(getFullImagePath(info.filePath));
                }
            }
        }
    }

    public static List<String> getVideoUrisList(VideoList videos) {
        List<String> videoList = new ArrayList<>();
        // TODO
        return videoList;
    }

    public static boolean equalImdb(String id, Movie m) {
        return id != null && m != null && id.equals(m.imdbId);
    }

    public static int colorForMetaScore(Context context, String scoreString) {
        Resources res = context.getResources();
        int score;
        try {
            score = Integer.parseInt(scoreString.trim());
        } catch (Exception e){
            return res.getColor(R.color.metascore_bg_unknown);
        }

        int color;
        if (score > 60)      color = res.getColor(R.color.metascore_bg_green);
        else if (score > 39) color = res.getColor(R.color.metascore_bg_yellow);
        else                 color = res.getColor(R.color.metascore_bg_red);

        return color;
    }
}
