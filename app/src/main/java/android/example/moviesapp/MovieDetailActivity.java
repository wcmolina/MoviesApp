package android.example.moviesapp;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MovieDetailActivity extends AppCompatActivity {

    final String MOVIE_ID = "420817";
    TextView movieOverview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);
        movieOverview = findViewById(R.id.tv_movie_overview);
        getMovieDetails(MOVIE_ID);
    }

    public void getMovieDetails(String movieId) {
        String baseUrl = getResources().getString(R.string.movie_detail_base_url);

        Uri buildUri = Uri.parse(baseUrl).buildUpon()
                .appendPath(movieId)
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
                Toast.makeText(MovieDetailActivity.this, "Failed Response", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String movieDetailResponse = response.body().string();
                MovieDetailActivity.this.runOnUiThread(() -> {
                    movieOverview.setText(movieDetailResponse);
                });
            }
        });
    }
}
