package android.example.moviesapp;

import android.content.res.Configuration;
import android.example.moviesapp.adapters.MovieCollectionAdapter;
import android.example.moviesapp.models.MovieCollection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView collectionRecyclerView;
    Toolbar toolbar;
    private MovieCollectionAdapter collectionAdapter;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    ProgressBar progressBar;
    private ArrayList<MovieCollection> movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initializeViews();
        setSupportActionBar(toolbar);
        drawerToggle = setupDrawerToggle();
        drawerLayout.addDrawerListener(drawerToggle);
        setupDrawerContent(navigationView);

        // Use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
        collectionRecyclerView.setHasFixedSize(true);
        collectionRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // RecyclerView adapter
        movies = new ArrayList<>();
        collectionAdapter = new MovieCollectionAdapter(this, movies);
        collectionRecyclerView.setAdapter(collectionAdapter);

        // Fetch movie collections
        String[] endpoints = {
                getResources().getString(R.string.now_playing_endpoint),
                getResources().getString(R.string.popular_endpoint),
                getResources().getString(R.string.upcoming_endpoint),
                getResources().getString(R.string.top_rated_endpoint)
        };
        new RetrieveCollection().execute(endpoints);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    selectDrawerItem(menuItem);
                    return true;
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        Toast.makeText(this, menuItem.getTitle(), Toast.LENGTH_SHORT).show();
        menuItem.setChecked(true);
        drawerLayout.closeDrawers();
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    public Request buildRequest(String baseUrl, String... paths) {
        Uri buildUri = Uri.parse(baseUrl);
        for (String path : paths) {
            buildUri = buildUri.buildUpon().appendPath(path).build();
        }
        buildUri = buildUri.buildUpon()
                .appendQueryParameter("language", "en-US")
                .appendQueryParameter("api_key", getResources().getString(R.string.api_key))
                .build();
        return new Request.Builder().url(buildUri.toString()).build();
    }

    public MovieCollection fetchCollection(String endpoint) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
        Request request = buildRequest(endpoint);
        try {
            Response response = client.newCall(request).execute();
            MovieCollection collection = new Gson().fromJson(response.body().string(), MovieCollection.class);
            if (collection != null && !collection.getResults().isEmpty()) {
                String collectionTitle = "";
                if (endpoint.endsWith("/now_playing")) {
                    collectionTitle = "Now Playing";
                } else if (endpoint.endsWith("/popular")) {
                    collectionTitle = "Popular";
                } else if (endpoint.endsWith("/top_rated")) {
                    collectionTitle = "Top Rated";
                } else if (endpoint.endsWith("/upcoming")) {
                    collectionTitle = "Upcoming";
                }
                collection.setTitle(collectionTitle);
                return collection;
            } else return null;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    public void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.home_drawer_layout);
        navigationView = findViewById(R.id.nv_view);
        progressBar = findViewById(R.id.pb_progress);
        collectionRecyclerView = findViewById(R.id.rv_movie_collection);
    }

    // Todo: non-static AsyncTask might cause memory leaks. Investigate about WeakReference to the activity
    private class RetrieveCollection extends AsyncTask<String, Void, ArrayList<MovieCollection>> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<MovieCollection> doInBackground(String... endpoints) {
            ArrayList<MovieCollection> collections = new ArrayList<>();
            MovieCollection collection;
            for (String endpoint : endpoints) {
                if ((collection = fetchCollection(endpoint)) != null) {
                    collections.add(collection);
                }
            }
            return collections;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieCollection> collections) {
            progressBar.setVisibility(View.GONE);
            movies.addAll(collections);
            collectionAdapter.notifyDataSetChanged();
        }
    }
}