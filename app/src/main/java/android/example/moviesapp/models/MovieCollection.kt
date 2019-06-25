package android.example.moviesapp.models

data class MovieCollection
(
        val results: ArrayList<Movie>,
        val page: Int,
        val total_results: Int,
        val total_pages: Int,
        var title: String
        // var = setter & getter
        // val = getter only
)