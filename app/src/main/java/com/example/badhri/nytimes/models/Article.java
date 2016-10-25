package com.example.badhri.nytimes.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by badhri on 10/22/16.
 */
public class Article {
    String webUrl;
    String headline;
    String thumbNail;
    String id;

    public String getThumbNail() {
        return thumbNail;
    }

    public String getHeadline() {
        return headline;
    }

    public String getWebUrl() {
        return webUrl;
    }




    public Article(JSONObject jsonObject) {
        try {
            this.webUrl = jsonObject.getString("web_url");
            this.id = jsonObject.getString("_id");
            this.headline = jsonObject.getJSONObject("headline").getString("main");

            JSONArray multimedia = jsonObject.getJSONArray("multimedia");

            if (multimedia.length() > 0) {
                JSONObject multimediaJson = multimedia.getJSONObject(0);
                this.thumbNail = "http://www.nytimes.com/" + multimediaJson.getString("url");
            } else {
                this.thumbNail = "";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Article> fromJsonArray(JSONArray array, HashSet<String> titles) {
        ArrayList<Article> out = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                Article current = new Article(array.getJSONObject(i));
                if (!titles.contains(current.headline)) {
                    out.add(current);
                    titles.add(current.headline);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  out;
    }
}
