package com.example.badhri.nytimes;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.badhri.nytimes.adapters.ArticlesAdapter;
import com.example.badhri.nytimes.fragments.FilterDialogFragment;
import com.example.badhri.nytimes.models.Article;
import com.example.badhri.nytimes.utilityClasses.EndlessRecyclerViewScrollListener;
import com.example.badhri.nytimes.utilityClasses.NYTimesClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class SearchActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,
        FilterDialogFragment.FilterDialogListener {

    @BindView(R.id.rvNews)RecyclerView rvNews;
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.ivBackground) ImageView ivBack;

    ArrayList<Article> articles;
    ArticlesAdapter articlesAdapter;
    HashSet<String> titles;
    String lastQuery;
    int lastPageRequest;
    static final String STATE_QUERY = "query";
    String beginDate;
    String sortOrder;
    String newsDesk;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);




        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(this);
        if (!pref.getString("beginDate", "n/a").contentEquals("n/a")) {
            beginDate = pref.getString("beginDate", "n/a");
        }
        if (!pref.getString("sortOrder", "n/a").contentEquals("n/a")) {
            sortOrder = pref.getString("sortOrder", "n/a").toLowerCase();
        }
        if (!pref.getString("newsDesk", "n/a").contentEquals("n/a")) {
            newsDesk = pref.getString("newsDesk", "n/a");
        }

        titles = new HashSet<>();
        articles = new ArrayList<>();
        articlesAdapter = new ArticlesAdapter(this, articles, this);
        rvNews.setAdapter(articlesAdapter);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, 1);
        rvNews.setLayoutManager(staggeredGridLayoutManager);
        rvNews.setItemAnimator(new SlideInUpAnimator());
        rvNews.addOnScrollListener(new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadArticles(lastQuery, page);
                lastPageRequest = page;
            }
        });


        Log.d("NYTi", "activity created");
        if (savedInstanceState != null) {
            lastQuery = savedInstanceState.getString(STATE_QUERY);
            if (lastQuery != null) {
                loadArticles(lastQuery, 0);
                lastPageRequest = 0;
            }
        }

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_name);
        Toast.makeText(this, "Enter the topic of interest in search bar", Toast.LENGTH_LONG).show();
    }


    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Article item = articles.get(position);

        // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();

        // set toolbar color and/or setting custom actions before invoking build()
        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, item.getWebUrl());

        int requestCode = 100;
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setActionButton(bitmap, "Share Link", pendingIntent, true);

        builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorAccent));
        CustomTabsIntent customTabsIntent = builder.build();

        customTabsIntent.launchUrl(this, Uri.parse(item.getWebUrl()));


    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putString(STATE_QUERY, lastQuery);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query2) {
                articles.clear();
                titles.clear();
                articlesAdapter.notifyDataSetChanged();
                lastQuery = query2;
                loadArticles(query2, 0);
                lastPageRequest = 0;
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort) {
            FragmentManager fm = getSupportFragmentManager();
            FilterDialogFragment filterDialogFragment = FilterDialogFragment
                    .newInstance("Filter Settings", getApplicationContext());
            filterDialogFragment.show(fm, "Filter");
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }


    public void postDelayed(final String lastQuery, final int lastPageRequest) {
        Handler handler = new Handler();

        Runnable runnableCode = new Runnable() {
            @Override
            public void run() {
                loadArticles(lastQuery, lastPageRequest);
            }
        };
        handler.postDelayed(runnableCode, 200);
    }

    public void loadArticles(final String query, final int page) {
        if (!isOnline()) {
            Toast.makeText(this, "No Internet connection", Toast.LENGTH_LONG).show();
            return;
        }
        NYTimesClient client = new NYTimesClient();
        client.getArticles(new JsonHttpResponseHandler(){

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  JSONObject errorResponse) {
                if (query.equals(lastQuery)) {
                    if (statusCode == 429)
                        postDelayed(query, page);
                    else
                        Toast.makeText(getApplicationContext(),
                                "Network request failed ! Try again", Toast.LENGTH_LONG).show();
                }
            }


            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                Log.d("NYTimes", response.toString());
                JSONArray articleJsonResults = null;

                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    articles.addAll(Article.fromJsonArray(articleJsonResults, titles));
                    articlesAdapter.notifyItemRangeInserted(articlesAdapter.getItemCount(),
                            articleJsonResults.length());
                    Log.d("NYTimes", articles.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, query, page, beginDate, sortOrder, newsDesk);
    }


    public void onFinishFilterDialog(String date, String sortOrder, boolean sports, boolean fashion,
                                     boolean arts) {
        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = pref.edit();
        if (!date.isEmpty()) {
            String[] units = date.split("/");
            edit.putInt("year", Integer.parseInt(units[2]));
            edit.putInt("month", Integer.parseInt(units[0]));
            edit.putInt("date", Integer.parseInt(units[1]));

            String formatDate, formatMonth;
            if (Integer.parseInt(units[0]) < 10) {
                formatMonth = String.format("0%s", units[0]);
            } else {
                formatMonth = String.format("%s", units[0]);
            }

            if (Integer.parseInt(units[1]) < 10) {
                formatDate = String.format("0%s", units[1]);
            } else {
                formatDate = String.format("%s", units[1]);
            }

            beginDate = String.format("%s%s%s",units[2], formatMonth, formatDate);
            edit.putString("beginDate", beginDate);
        }

        edit.putString("sortOrder", sortOrder);
        this.sortOrder = sortOrder.toLowerCase();

        if (sports || fashion || arts ) {
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("news_desk:(");
            if (sports) {
                stringBuilder.append("\"Sports\" ");
            }

            if (arts) {
                stringBuilder.append("\"Arts\" ");
            }

            if (fashion) {
                stringBuilder.append("\"Fashion & Style\" ");
            }

            newsDesk = stringBuilder.toString().trim() + ")";
            edit.putString("newsDesk", newsDesk);

        } else {
            newsDesk = null;
            edit.remove("newsDesk");
        }
        edit.putBoolean("sports", sports);
        edit.putBoolean("fashion", fashion);
        edit.putBoolean("arts", arts);
        edit.commit();

        Log.d("NYTimes", date + sortOrder + sports + fashion + arts);
        Log.d("NYTimes", beginDate + this.sortOrder + newsDesk);

        if (lastQuery != null) {
            loadArticles(lastQuery, 0);
            articles.clear();
            titles.clear();
            articlesAdapter.notifyDataSetChanged();
            loadArticles(lastQuery, 0);
            lastPageRequest = 0;
        }
    }
}
