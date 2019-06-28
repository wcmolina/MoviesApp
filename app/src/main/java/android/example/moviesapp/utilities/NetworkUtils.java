package android.example.moviesapp.utilities;

import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Request;

public class NetworkUtils {

    public static Request buildRequest(String baseUrl, String path, HashMap<String, String> queryParams) {
        Uri buildUri = Uri.parse(baseUrl);

        // Append path (if any)
        if (!path.isEmpty()) {
            buildUri = buildUri.buildUpon()
                    .appendPath(path)
                    .build();
        }

        // Append query parameters (if any)
        if (!queryParams.isEmpty()) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                buildUri = buildUri
                        .buildUpon()
                        .appendQueryParameter(entry.getKey(), entry.getValue())
                        .build();
            }
        }
        return new Request.Builder().url(buildUri.toString()).build();
    }
}
