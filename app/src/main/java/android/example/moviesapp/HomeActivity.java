package android.example.moviesapp;

import android.example.moviesapp.adapters.MovieCollectionAdapter;
import android.example.moviesapp.models.MovieCollection;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView collectionRecyclerView;
    private ArrayList<MovieCollection> movies = new ArrayList<>();
    private MovieCollectionAdapter collectionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        String[] endpoints = {
                getResources().getString(R.string.now_playing_endpoint),
                getResources().getString(R.string.popular_endpoint),
                getResources().getString(R.string.top_rated_endpoint),
                getResources().getString(R.string.upcoming_endpoint)
        };

        fetchCollections(endpoints);
        collectionRecyclerView = findViewById(R.id.rv_movie_collection);

        // Use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
        collectionRecyclerView.setHasFixedSize(true);

        collectionRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        collectionAdapter = new MovieCollectionAdapter(this, movies);
        collectionRecyclerView.setAdapter(collectionAdapter);
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

    public void fetchCollections(String[] endpoints) {
        for (String endpoint : endpoints) {
            new OkHttpClient()
                    .newCall(buildRequest(endpoint))
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            call.cancel();
                            Toast.makeText(HomeActivity.this, "Collection request failed", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                final String collectionResponse = response.body().string();
                                MovieCollection collection = new Gson().fromJson(collectionResponse, MovieCollection.class);

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
                                movies.add(collection);
                                HomeActivity.this.runOnUiThread(() -> collectionAdapter.notifyDataSetChanged());
                            }
                        }
                    });
        }
    }
}