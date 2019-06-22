package android.example.moviesapp;

import android.content.Context;
import android.content.Intent;
import android.example.moviesapp.adapters.PagerAdapter;
import android.example.moviesapp.models.Movie;
import android.example.moviesapp.models.Video;
import android.example.moviesapp.models.VideoList;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class MovieLandingActivity extends AppCompatActivity {

    private static final String EXTRA_MOVIE_ID =
            "android.example.moviesapp.movie_id";
    String movieId;
    ImageView movieBackdrop;
    Button playTrailerBtn;
    TextView movieOverview;
    TextView runtime;
    TextView date;
    TextView avgVotes;
    PagerAdapter pagerAdapter;

    // Activity Lifecycle

    public static Intent newIntent(Context packageContext, String movieId) {
        Intent intent = new Intent(packageContext, MovieLandingActivity.class);
        intent.putExtra(EXTRA_MOVIE_ID, movieId);
        return intent;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_landing);
        initializeViews();

        // Add Back button to ActionBar and remove default text
        try {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }

        movieId = getIntent().getStringExtra(EXTRA_MOVIE_ID);
        updateViewsFromMovie(movieId);
        setupViewPager();
        playTrailerBtn.setOnClickListener(v -> {
            String baseUrl = getResources().getString(R.string.movie_detail_base_url);
            new OkHttpClient()
                    .newCall(buildRequest(baseUrl, movieId, "videos"))
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            call.cancel();
                            Toast.makeText(MovieLandingActivity.this, "Error fetching videos", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            VideoList videoList = new Gson().fromJson(response.body().string(), VideoList.class);
                            Video mainTrailer = videoList.getResults().get(0);
                            if (!mainTrailer.getKey().isEmpty()) {
                                Intent intent = YoutubePlayerActivity.newIntent(MovieLandingActivity.this, mainTrailer.getKey());
                                startActivity(intent);
                            } else {
                                Toast.makeText(MovieLandingActivity.this, "Error playing video", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });
    }

    public void updateViewsFromMovie(String movieId) {
        String baseUrl = getResources().getString(R.string.movie_detail_base_url);
        new OkHttpClient()
                .newCall(buildRequest(baseUrl, movieId))
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        call.cancel();
                        Toast.makeText(MovieLandingActivity.this, "Request failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String movieDetailResponse = response.body().string();
                        MovieLandingActivity.this.runOnUiThread(() -> {
                            // Init movie instance
                            Movie movie = new Gson().fromJson(movieDetailResponse, Movie.class);

                            // Update ActionBar text with movie title
                            try {
                                Objects.requireNonNull(getSupportActionBar()).setTitle(movie.getTitle());
                            } catch (NullPointerException npe) {
                                npe.printStackTrace();
                            }

                            // Update views with values from movie instance
                            // Todo: validations
                            movieOverview.setText(movie.getOverview());
                            runtime.setText(getString(R.string.copy_runtime_tv, movie.getRuntime()));
                            date.setText(movie.getRelease_date().split("-")[0]);
                            avgVotes.setText(getString(R.string.copy_votes_tv, movie.getVote_average()));

                            // Set backdrop image
                            Glide.with(MovieLandingActivity.this)
                                    .load("https://image.tmdb.org/t/p/w500" + movie.getBackdrop_path())
                                    .transition(withCrossFade(300))
                                    .centerCrop()
                                    .into(movieBackdrop);
                        });
                    }
                });
    }

    private void initializeViews() {
        movieBackdrop = findViewById(R.id.iv_movie_backdrop);
        playTrailerBtn = findViewById(R.id.btn_play_trailer);
        movieOverview = findViewById(R.id.tv_movie_overview);
        runtime = findViewById(R.id.tv_runtime);
        date = findViewById(R.id.tv_date);
        avgVotes = findViewById(R.id.tv_votes);
    }

    private void setupViewPager() {
        ViewPager viewPager = findViewById(R.id.pager);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(MovieCreditsFragment.newInstance(0, "Credits", movieId));
        pagerAdapter.addFragment(MovieReviewsFragment.newInstance(1, "Reviews", movieId));
        viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = findViewById(R.id.landing_tab_layout);
        tabLayout.setupWithViewPager(viewPager);
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
}
