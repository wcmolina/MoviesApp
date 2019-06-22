package android.example.moviesapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MovieReviewsFragment extends Fragment {

    private static final String ARG_PAGE = "page";
    private static final String ARG_TITLE = "title";
    private static final String ARG_MOVIE_ID = "movieId";

    public MovieReviewsFragment() {
    }

    public static MovieReviewsFragment newInstance(int page, String title, String movieId) {
        MovieReviewsFragment fragment = new MovieReviewsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MOVIE_ID, movieId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_reviews, container, false);
        TextView label = view.findViewById(R.id.tv_reviews);
        label.setText("Reviews!");
        return view;
    }
}
