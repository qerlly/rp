package pl.agora.radiopogoda.data.model.news.category

data class CategoryNews(
    val item: List<CategoryNew>,
    val lastBuildDate: String,
    val lastBuildDate2: String,
    val title: String
)