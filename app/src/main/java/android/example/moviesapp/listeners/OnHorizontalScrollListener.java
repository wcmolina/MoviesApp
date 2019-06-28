package android.example.moviesapp.listeners;

import android.support.v7.widget.RecyclerView;

public abstract class OnHorizontalScrollListener
        extends RecyclerView.OnScrollListener {

    @Override
    public final void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (!recyclerView.canScrollHorizontally(-1)) {
            onScrolledToTop();
        } else if (!recyclerView.canScrollHorizontally(1)) {
            onScrolledToBottom();
        } else if (dx < 0) {
            onScrolledUp();
        } else if (dx > 0) {
            onScrolledDown();
        }
    }

    public void onScrolledUp() {
    }

    public void onScrolledDown() {
    }

    public void onScrolledToTop() {
    }

    public void onScrolledToBottom() {
    }
}
