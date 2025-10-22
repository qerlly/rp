package pl.agora.radiopogoda.ui.composables.navigation

import pl.agora.radiopogoda.utils.Consts

enum class TabItem(val tabName: String, val url: String) {
    ALL("NAJNOWSZE", Consts.ALL_NEWS_URL),
    INTERESTING("CIEKAWOSTKI", Consts.INTERESTING_NEWS_URL),
    MUSIC("MUZYKA", Consts.MUSIC_NEWS_URL),
    RECOMMEND("PORADY", Consts.RECOMMEND_NEWS_URL),
    ENTERTAIMENT("ROZRYWKA", Consts.ENTERTAIMENT_NEWS_URL),
    QUIZ("QUIZY", Consts.QUIZ_NEWS_URL);


    companion object{
        fun getNewsTabs() = arrayOf(ALL, INTERESTING, MUSIC, RECOMMEND, ENTERTAIMENT,  QUIZ)
    }
}
