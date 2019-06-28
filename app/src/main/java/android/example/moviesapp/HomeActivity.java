package android.example.moviesapp;

import android.content.res.Configuration;
import android.example.moviesapp.adapters.MovieAdapter;
import android.example.moviesapp.adapters.MovieCollectionAdapter;
import android.example.moviesapp.models.Movie;
import android.example.moviesapp.models.MovieCollection;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.example.moviesapp.utilities.NetworkUtils.buildRequest;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView collectionRecyclerView;
    private MovieCollectionAdapter collectionAdapter;
    private ArrayList<MovieCollection> collections;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initializeViews();
        setSupportActionBar(toolbar);
        drawerToggle = setupDrawerToggle();
        drawerLayout.addDrawerListener(drawerToggle);
        setupDrawerContent(navigationView);
        setupRecyclerView();
        initializeCollections();
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

    public void initializeCollections() {
        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("language", "en-US");
        queryParams.put("api_key", getString(R.string.api_key));
        Request[] requests = {
                buildRequest(getResources().getString(R.string.now_playing_endpoint), "", queryParams),
                buildRequest(getResources().getString(R.string.upcoming_endpoint), "", queryParams),
                buildRequest(getResources().getString(R.string.popular_endpoint), "", queryParams),
                buildRequest(getResources().getString(R.string.top_rated_endpoint), "", queryParams)
        };
        new RetrieveCollections().execute(requests);
    }

    public MovieCollection fetchCollection(Request request) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
        try {
            Response response = client.newCall(request).execute();
            MovieCollection collection = new Gson().fromJson(response.body().string(), MovieCollection.class);
            if (collection != null && !collection.getResults().isEmpty()) {
                String url = request.url().uri().getPath();
                // Todo: I don't like this logic here, find a better place to handle this.
                if (url.endsWith("/now_playing")) {
                    collection.setTitle("Now Playing");
                    collection.setUrl(getString(R.string.now_playing_endpoint));
                } else if (url.endsWith("/popular")) {
                    collection.setTitle("Popular");
                    collection.setUrl(getString(R.string.popular_endpoint));
                } else if (url.endsWith("/top_rated")) {
                    collection.setTitle("Top Rated");
                    collection.setUrl(getString(R.string.top_rated_endpoint));
                } else if (url.endsWith("/upcoming")) {
                    collection.setTitle("Upcoming");
                    collection.setUrl(getString(R.string.upcoming_endpoint));
                }
                // 'type' value would be set automatically if it was included in the response but it's not, so I have to manually set it. 'type' is used in MovieAdapter
                for (Movie movie : collection.getResults()) {
                    movie.setType("movie");
                }
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

    public void addToCollection(int position, Movie... movies) {
        this.collections.get(position).getResults().addAll(Arrays.asList(movies));
    }

    public void fetchIntoCollection(MovieAdapter adapter, Request request, int position) {
        new LoadIntoCollection(position, adapter).execute(request);
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

    private void setupRecyclerView() {
        // Use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
        collectionRecyclerView.setHasFixedSize(true);

        // Layout manager (vertical linear layout for home collections)
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        collectionRecyclerView.setLayoutManager(layoutManager);

        // RecyclerView adapter
        collections = new ArrayList<>();
        collectionAdapter = new MovieCollectionAdapter(this, collections);
        collectionRecyclerView.setAdapter(collectionAdapter);
    }

    // Todo: non-static AsyncTask might cause memory leaks. Investigate about WeakReference to the activity
    // This async tasks retrieves a collection for each request and adds them to the main collections ArrayList
    public class RetrieveCollections extends AsyncTask<Request, Void, ArrayList<MovieCollection>> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<MovieCollection> doInBackground(Request... requests) {
            ArrayList<MovieCollection> collections = new ArrayList<>();
            MovieCollection collection;
            for (int i = 0; i < requests.length; i++) {
                Request request = requests[i];
                collection = fetchCollection(request);
                if (collection != null && !collection.getResults().isEmpty()) {
                    collection.setPosition(i);
                    collections.add(collection);
                }
            }
            return collections;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieCollection> collections) {
            progressBar.setVisibility(View.GONE);
            collections.addAll(collections);
            collectionAdapter.notifyDataSetChanged();
        }
    }

    // Todo: non-static AsyncTask might cause memory leaks. Investigate about WeakReference to the activity
    // This async task retrives a single collection and appends its contents to the end of a specific row or collection already created
    public class LoadIntoCollection extends AsyncTask<Request, Void, MovieCollection> {

        // Stores the reference to the MovieAdapter that manages the contents of a specific collection
        MovieAdapter adapter;
        // The collection's position that will 'host' the new collection's data
        private int position;

        public LoadIntoCollection(int position, MovieAdapter adapter) {
            this.position = position;
            this.adapter = adapter;
        }

        @Override
        protected MovieCollection doInBackground(Request... requests) {
            MovieCollection collection = fetchCollection(requests[0]);
            if (collection != null && !collection.getResults().isEmpty())
                return collection;
            else
                return null;
        }

        @Override
        protected void onPostExecute(MovieCollection collection) {
            collections.get(position).getResults().addAll(collection.getResults());
            adapter.notifyDataSetChanged();
        }
    }
}