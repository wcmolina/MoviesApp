package android.example.moviesapp.adapters;

import android.content.Context;
import android.example.moviesapp.R;
import android.example.moviesapp.models.MovieCollection;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class MovieCollectionAdapter extends RecyclerView.Adapter<MovieCollectionAdapter.MovieCollectionViewHolder> {

    private static final String TAG = MovieCollectionAdapter.class.getSimpleName();

    private List<MovieCollection> collections;
    private Context context;

    public MovieCollectionAdapter(Context context, List<MovieCollection> collections) {
        this.collections = collections;
        this.context = context;
    }

    // Inflating a layout from XML and return the holder
    @NonNull
    @Override
    public MovieCollectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");
        View movieCollectionView = LayoutInflater.from(context).inflate(R.layout.movie_collection_row, parent, false);
        return new MovieCollectionViewHolder(movieCollectionView);
    }

    // Populate data into the item through the view holder
    @Override
    public void onBindViewHolder(@NonNull MovieCollectionViewHolder collectionViewHolder, int position) {
        Log.d(TAG, "onBindViewHolder");
        MovieCollection collection = collections.get(position);
        collectionViewHolder.titleTextView.setText("Popular");
        MovieAdapter movieAdapter = new MovieAdapter(context, collection.getResults());
        collectionViewHolder.moviesRecyclerView.setHasFixedSize(true);
        collectionViewHolder.moviesRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        collectionViewHolder.moviesRecyclerView.setAdapter(movieAdapter);
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount");
        return collections.size();
    }

    class MovieCollectionViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;
        RecyclerView moviesRecyclerView;

        public MovieCollectionViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.tv_collection_title);
            moviesRecyclerView = itemView.findViewById(R.id.rv_movie_items);
        }
    }
}
