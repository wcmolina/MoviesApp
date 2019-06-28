package android.example.moviesapp.models

data class Movie
(
        var id: String = "",
        var title: String = "",
        var vote_average: Double = 0.0,
        var runtime: Int = 0,
        var release_date: String = "",
        var overview: String = "",
        var backdrop_path: String = "",
        var poster_path: String = "",
        var type: String = ""
) {
}