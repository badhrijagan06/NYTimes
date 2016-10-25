package com.example.badhri.nytimes.utilityClasses;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by badhri on 10/22/16.
 */
public class NYTimesClient {
    String API_BASE_URL = "https://api.nytimes.com/svc/search/v2/";
    String API_KEY = "c2f050bc669a4c0ab9e418252e099dcc";
    private AsyncHttpClient client;

    public NYTimesClient() {
        client = new AsyncHttpClient();
    }

    private String getApiUrl(String relativeUrl) {
        return API_BASE_URL + relativeUrl;
    }

    public void getArticles(JsonHttpResponseHandler handler, String query, int page, String beginDate, String sortOrder, String newsDesk) {
        String url = getApiUrl("articlesearch.json");
        RequestParams params = new RequestParams();
        params.put("api-key",API_KEY);
        params.put("page", page);
        params.put("q",query);

        if (beginDate != null) {
            params.put("begin_date",beginDate);
        }

        if (sortOrder != null) {
            params.put("sort",sortOrder);
        }

        if (newsDesk != null) {
            params.put("fq",newsDesk);
        }

        Log.d("NYTimes","query" + query + "begindate" + beginDate + "sortorder" + sortOrder + "newsdesk" + newsDesk);
        client.get(url, params, handler);
    }
}
