package android.example.moviesapp.models

data class Movie
(
        val id: String,
        val title: String,
        val vote_average: Double,
        val runtime: Int,
        val release_date: String,
        val overview: String,
        val backdrop_path: String,
        val poster_path: String,
        var type: String
)