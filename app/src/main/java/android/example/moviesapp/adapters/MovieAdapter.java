package android.example.moviesapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.example.moviesapp.MovieLandingActivity;
import android.example.moviesapp.R;
import android.example.moviesapp.models.Movie;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private static final String TAG = MovieCollectionAdapter.class.getSimpleName();
    private List<Movie> movies;
    private Context context;

    MovieAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    // This method usually involves inflating a layout from XML and returning the view holder
    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        Log.d(TAG, "onCreateViewHolder");
        View movieView = LayoutInflater.from(context).inflate(R.layout.movie_collection_item, parent, false);
        return new MovieViewHolder(movieView);
    }

    // Bind data from data source (Movie) to views contained in the movie view holder (MovieViewHolder)
    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder movieViewHolder, int position) {
        Log.d(TAG, "onBindViewHolder");
        // Set backdrop image
        Glide.with(context)
                .load("https://image.tmdb.org/t/p/w500" + movies.get(position).getPoster_path())
                .transition(withCrossFade(300))
                .centerCrop()
                .into(movieViewHolder.poster);
        movieViewHolder.poster.setOnClickListener(v -> {
            Intent intent = MovieLandingActivity.newIntent(context, movies.get(position).getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount");
        return movies.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {

        ImageView poster;

        MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.iv_movie_poster);
        }
    }
}
