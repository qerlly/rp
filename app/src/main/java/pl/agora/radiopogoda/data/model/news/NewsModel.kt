package pl.agora.radiopogoda.data.model.news

import pl.agora.radiopogoda.data.model.news.category.CategoryNew

data class NewsModel(
    val id: Int,
    val title: String,
    val url: String,
    val image: String,
    val parent: String? = null
) {

    companion object {
        fun fromCategoryToNewsModel(categoryNew: CategoryNew): NewsModel {
            return NewsModel(
                id = categoryNew.id,
                title = categoryNew.title.textFormatter(),
                url = categoryNew.link,
                image = categoryNew.url,
                parent = categoryNew.parent
            )
        }

        private fun String.textFormatter() = this.replace("&quot;", "'")
    }
}