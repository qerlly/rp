package pl.agora.radiopogoda.utils

object Consts {

    const val GEMIUS_URL: String =  "https://gapl.hit.gemius.pl"

    const val REWIND_VALUE: Int = 10000
    const val LIVE_TARGET_OFFSET_MS: Long = 5000
    const val UPDATE_DELAY: Long = 10000

    const val BASE_URL = "https://radiopogoda.pl/"
    const val CONTEST_URL = "https://radiopogoda.pl/externals/xml/konkursy.json"

    const val ALL_NEWS_URL = "https://radiopogoda.pl/externals/xml/najnowsze.json"
    const val MUSIC_NEWS_URL = "https://radiopogoda.pl/externals/xml/muzyka.json"
    const val INTERESTING_NEWS_URL = "https://radiopogoda.pl/externals/xml/ciekawostki.json"
    const val ENTERTAIMENT_NEWS_URL = "https://radiopogoda.pl/externals/xml/rozrywka.json"
    const val QUIZ_NEWS_URL = "https://radiopogoda.pl/externals/xml/quizy.json"
    const val RECOMMEND_NEWS_URL = "https://radiopogoda.pl/externals/xml/porady.json"

    const val CHANNELS_URL = "https://fm.tuba.pl/api3/ez/channels/rp"
    const val MAIN_CHANNEL = "https://fm.tuba.pl/api3/onStation?id=38&reader=true"
    const val MAIN_CHANNEL_ID = 38

    const val AUDIOBOOKS_PROGRAM_URL = "https://fm.tuba.pl/api3/ez/getAudiobooksSeries/rp"
    const val AUDIOBOOKS_URL = "https://fm.tuba.pl/api3/ez/getAudiobooks/rp?limit=500000"
    const val PROGRAMS_URL = "https://fm.tuba.pl/api3/ez/getPresenterList/rp"
    const val PODCASTS_URL = "https://fm.tuba.pl/api3/ez/getCurrentPodcastList/rp"

    const val FACEBOOK = "https://www.facebook.com/RadioPogodaNajpiekniejszeMelodie/?locale=pl_PL"
    const val INSTAGRAM = "https://www.instagram.com/radiopogoda/"
    const val SITE = "https://radiopogoda.pl/"
    const val TIKTOK = "https://www.tiktok.com/@radiopogoda"
    const val APP_LINK = "https://play.google.com/store/apps/details?id=pl.agora.radiopogoda&hl=pl"

    const val ON_RADIO_URL = "https://fm.tuba.pl/api3/ez/schedule/rp"
    const val HISTORY = "https://fm.tuba.pl/api3/ez/history/rp?startDate=date&trackCount=120&callback=jsonData"

    const val VAST_LINK= "https://pubads.g.doubleclick.net/gampad/ads?iu=/4350995/App/RadioPogoda_player&msid=pl.agora.radiopogoda&an=Radio+Pogoda&url=https%3A%2F%2Fradiopogoda.pl&npa=0&ad_type=audio_video&sz=400x300|640x480|1x1&min_ad_duration=5000&max_ad_duration=30000&gdfp_req=1&output=vast&env=instream&unviewed_position_start=1&description_url=radiopogoda&pvid=[AppSetID_value]&pvid_s=scope_app&correlator=[timestamp]"
    const val VAST_XML_TAG: String = "VAST_XML"

    const val ANIMATED_SLIDER_CARD_SIZE = 3
    const val LINK_FROM_FCM = "url"
}