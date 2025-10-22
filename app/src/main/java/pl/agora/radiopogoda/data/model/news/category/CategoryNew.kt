package pl.agora.radiopogoda.data.model.news.category

data class CategoryNew(
    val id: Int,
    val link: String,
    val parent: String,
    val pubDate: String,
    val pubDate2: String,
    val title: String,
    val url: String,
)