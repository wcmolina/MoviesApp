package android.example.moviesapp;

import android.example.moviesapp.adapters.MovieCreditsAdapter;
import android.example.moviesapp.models.Credit;
import android.example.moviesapp.models.CreditList;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MovieCreditsFragment extends Fragment {

    private static final String ARG_PAGE = "page";
    private static final String ARG_TITLE = "title";
    private static final String ARG_MOVIE_ID = "movieId";
    private ArrayList<Credit> credits = new ArrayList<>();
    private MovieCreditsAdapter creditsAdapter;

    public MovieCreditsFragment() {
    }

    public static MovieCreditsFragment newInstance(int page, String title, String moveId) {
        MovieCreditsFragment fragment = new MovieCreditsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MOVIE_ID, moveId);
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
        View view = inflater.inflate(R.layout.fragment_movie_credit, container, false);
        RecyclerView creditsRecyclerView = view.findViewById(R.id.rv_movie_credits);
        creditsRecyclerView.setHasFixedSize(true);
        creditsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        creditsAdapter = new MovieCreditsAdapter(getContext(), credits);
        creditsRecyclerView.setAdapter(creditsAdapter);

        String baseUrl = getResources().getString(R.string.movie_detail_base_url);
        String moveId = getArguments().getString(ARG_MOVIE_ID);
        new OkHttpClient().newCall(buildRequest(baseUrl, moveId, "credits")).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                credits.addAll(new Gson().fromJson(response.body().string(), CreditList.class).getCast());
                getActivity().runOnUiThread(() -> creditsAdapter.notifyDataSetChanged());
            }
        });
        return view;
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
