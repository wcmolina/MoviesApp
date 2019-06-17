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
        initPopular();
        collectionRecyclerView = findViewById(R.id.rv_movie_collection);

        // Use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
        collectionRecyclerView.setHasFixedSize(true);

        collectionRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        collectionAdapter = new MovieCollectionAdapter(this, movies);
        collectionRecyclerView.setAdapter(collectionAdapter);
    }

    public void initPopular() {
        String baseUrl = "https://api.themoviedb.org/3/movie/popular";

        Uri buildUri = Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter("language", "en-US")
                .appendQueryParameter("api_key", getResources().getString(R.string.api_key))
                .build();

        Request request = new Request.Builder()
                .url(buildUri.toString())
                .build();

        OkHttpClient httpClient = new OkHttpClient();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                Toast.makeText(HomeActivity.this, "Popular request failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String popularResponse = response.body().string();
                movies.add(new Gson().fromJson(popularResponse, MovieCollection.class));
                movies.add(new Gson().fromJson(popularResponse, MovieCollection.class));
                movies.add(new Gson().fromJson(popularResponse, MovieCollection.class));
                movies.add(new Gson().fromJson(popularResponse, MovieCollection.class));
                movies.add(new Gson().fromJson(popularResponse, MovieCollection.class));
                HomeActivity.this.runOnUiThread(() -> collectionAdapter.notifyDataSetChanged());
            }
        });
    }
}