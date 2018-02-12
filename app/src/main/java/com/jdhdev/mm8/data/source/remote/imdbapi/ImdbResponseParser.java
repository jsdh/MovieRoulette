package com.jdhdev.mm8.data.source.remote.imdbapi;


import com.jdhdev.mm8.data.source.remote.imdbapi.Model.CastMemeber;
import com.jdhdev.mm8.data.source.remote.imdbapi.Model.ImdbMovieInfo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ImdbResponseParser {

    @Inject
    public ImdbResponseParser() {}

    public ImdbMovieInfo parseResponse(String html) {
        String imdbScore = "";
        String metaScore = "";
        String id = "";
        List<CastMemeber> casting = null;
        try {
            Document doc = Jsoup.parse(html);
            id = parseId(doc);
            imdbScore = parseImdbScore(doc);
            metaScore = parseMetacriticScore(doc);
            casting = parseCast(doc);

        } catch (Exception e) {
            System.out.println("FAILED to parse: " + e);
        }
        return new ImdbMovieInfo(id, imdbScore, metaScore, casting);
    }

    private String parseImdbScore(Document doc) {
        String score = "";
        try {
            TextNode tn = doc.select("#ratings-bar div .inline-block:eq(1)")
                    .first()
                    .textNodes()
                    .get(0);
            score = tn.text();

        } catch (Exception e) {
            System.out.println("FAILED to parse: IMDB Score: " + e);
        }

        return score;
    }

    private String parseMetacriticScore(Document doc) {
        String score = "";
        try {
            TextNode tn = doc.select("#ratings-bar div.inline-block:eq(2) a span")
                    .first()
                    .textNodes()
                    .get(0);

            score = tn.text().trim();
        } catch (Exception e) {
            System.out.println("FAILED to parse METACRITIC Score: " + e);
        }

        return score;
    }

    private String parseId(Document doc) {
        String id = "";
        try {
            id = doc.select("meta[property=pageId]")
                    .attr("content");

        } catch (Exception e) {
            System.out.println("FAILED to parse: IMDB ID: " + e);
        }

        return id;
    }

    private List<CastMemeber> parseCast(Document doc) {
        List<CastMemeber> casting = new ArrayList<>();

        try {
            Element elm = doc.select("#cast-and-crew .smooth-scroller ul[class=list-inline]")
                    .get(0);
            for (Element child : elm.children()) {
                CastMemeber member = parseCastMember(child);
                if (member != null) casting.add(member);
            }
        } catch (Exception e) {
            System.out.println("FAILED to parse: IMDB Cast: " + e);
        }

        return casting;
    }

    private CastMemeber parseCastMember(Element container) {
        CastMemeber person = null;
        try {
            String picLink = container.select("img[data-src-x2]")
                    .attr("data-src-x2");
            Elements nameElms = container.select("div > [class=ellipse]");

            String actor = nameElms.get(0).select("small > a > strong").text();
            String character = nameElms.get(1).select("small").text();

            person = new CastMemeber(actor, character, picLink);
        } catch (Exception e) {
            System.out.println("FAILED to parse: IMDB Cast Memeber: " + e);
        }

        return person;
    }
}
