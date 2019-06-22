package android.example.moviesapp.adapters;

import android.content.Context;
import android.example.moviesapp.R;
import android.example.moviesapp.models.Credit;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class MovieCreditsAdapter extends RecyclerView.Adapter<MovieCreditsAdapter.MovieCreditViewHolder> {

    private List<Credit> credits;
    private Context context;

    public MovieCreditsAdapter(Context context, List<Credit> credits) {
        this.context = context;
        this.credits = credits;
    }

    @NonNull
    @Override
    public MovieCreditViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View creditView = LayoutInflater.from(context).inflate(R.layout.movie_credit_item, viewGroup, false);
        return new MovieCreditViewHolder(creditView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieCreditViewHolder movieCreditViewHolder, int position) {
        Glide.with(context)
                .load("https://image.tmdb.org/t/p/w500" + credits.get(position).getProfile_path())
                .transition(withCrossFade(300))
                .centerCrop()
                .into(movieCreditViewHolder.picture);
        movieCreditViewHolder.name.setText(credits.get(position).getName());
        String character = credits.get(position).getCharacter();
        if (!character.isEmpty()) {
            movieCreditViewHolder.character.setText("as " + character);
        }
    }

    @Override
    public int getItemCount() {
        return (credits != null) ? credits.size() : 0;
    }

    class MovieCreditViewHolder extends RecyclerView.ViewHolder {

        ImageView picture;
        TextView name;
        TextView character;

        MovieCreditViewHolder(@NonNull View itemView) {
            super(itemView);
            picture = itemView.findViewById(R.id.iv_credit);
            name = itemView.findViewById(R.id.tv_credit);
            character = itemView.findViewById(R.id.tv_character);
        }
    }
}
