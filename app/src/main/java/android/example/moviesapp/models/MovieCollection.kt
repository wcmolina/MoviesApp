package android.example.moviesapp.models

data class MovieCollection
(
        val results: List<Movie>,
        val total_results: Int,
        val total_pages: Int,
        val max_pages: Int = 50,
        var page: Int,
        var url: String,
        var title: String,
        var position: Int
        // var = setter & getter
        // val = getter only
)