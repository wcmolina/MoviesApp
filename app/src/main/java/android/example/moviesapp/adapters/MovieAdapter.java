package android.example.moviesapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.example.moviesapp.HomeActivity;
import android.example.moviesapp.MovieLandingActivity;
import android.example.moviesapp.R;
import android.example.moviesapp.models.Movie;
import android.example.moviesapp.models.MovieCollection;
import android.example.moviesapp.utilities.NetworkUtils;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.List;

import okhttp3.Request;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private final int TYPE_MOVIE = 0;
    private final int TYPE_LOADER = 1;
    private MovieCollection collection;

    MovieAdapter(Context context, MovieCollection collection) {
        this.context = context;
        this.collection = collection;
    }

    // This method usually involves inflating a layout from XML and returning the view holder
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == TYPE_MOVIE) {
            return new MovieViewHolder(inflater.inflate(R.layout.movie_collection_item, parent, false));
        } else {
            return new LoadViewHolder(inflater.inflate(R.layout.movie_collection_item_loading, parent, false));
        }
    }

    // Bind data from data source (Movie) to views contained in the movie view holder (MovieViewHolder)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (position >= getItemCount() - 1) {
            collection.setPage(collection.getPage() + 1);

            // Prepare next page request
            HashMap<String, String> queryParams = new HashMap<>();
            queryParams.put("language", "en-US");
            queryParams.put("api_key", context.getResources().getString(R.string.api_key));
            queryParams.put("page", String.valueOf(collection.getPage()));
            Request request = NetworkUtils.buildRequest(collection.getUrl(), "", queryParams);

            // Fetch movies from the specified page to the current collection
            ((HomeActivity) context).fetchIntoCollection(this, request, collection.getPosition());
        }
        if (getItemType(position) == TYPE_MOVIE) {
            // Set backdrop image
            Glide.with(context)
                    .load("https://image.tmdb.org/t/p/w500" + getMovies().get(position).getPoster_path())
                    .transition(withCrossFade(300))
                    .centerCrop()
                    .into(((MovieViewHolder) viewHolder).poster);

            ((MovieViewHolder) viewHolder).poster.setOnClickListener(v -> {
                Intent intent = MovieLandingActivity.newIntent(context, getMovies().get(position).getId());
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return getMovies().size();
    }

    private List<Movie> getMovies() {
        return collection.getResults();
    }

    private int getItemType(int position) {
        if (getMovies().get(position).getType().equals("movie"))
            return TYPE_MOVIE;
        else
            return TYPE_LOADER;
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {

        ImageView poster;

        MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.iv_movie_poster);
        }
    }

    class LoadViewHolder extends RecyclerView.ViewHolder {
        public LoadViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
