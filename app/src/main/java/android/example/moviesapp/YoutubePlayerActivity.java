package android.example.moviesapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.pierfrancescosoffritti.androidyoutubeplayer.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.AbstractYouTubePlayerListener;

import java.util.Objects;

public class YoutubePlayerActivity extends AppCompatActivity {

    YouTubePlayerView youtubePlayerView;
    YouTubePlayer youtubePlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_youtube_player);
        initPlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        youtubePlayerView.release();
    }

    public void initPlayer() {
        youtubePlayerView = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youtubePlayerView);
        try {
            Objects.requireNonNull(YoutubePlayerActivity.this.getSupportActionBar()).hide();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
        youtubePlayerView.initialize(player -> {
            youtubePlayer = player;
            youtubePlayer.addListener(new AbstractYouTubePlayerListener() {

                @Override
                public void onReady() {
                    youtubePlayerView.getPlayerUIController().showFullscreenButton(false);
                    youtubePlayerView.getPlayerUIController().showYouTubeButton(false);
                    youtubePlayerView.enterFullScreen();
                    youtubePlayer.loadVideo("hA6hldpSTF8", 0);
                    youtubePlayer.play();
                }

                @Override public void onStateChange(@NonNull PlayerConstants.PlayerState state) {
                    if (state == PlayerConstants.PlayerState.ENDED) {
                        youtubePlayer.seekTo(0);
                        youtubePlayer.pause();
                    }
                }
            });
        }, true);
    }
}
