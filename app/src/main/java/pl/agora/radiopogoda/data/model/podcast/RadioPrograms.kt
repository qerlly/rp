package pl.agora.radiopogoda.data.model.podcast

data class RadioPrograms(
    val node_id: String,
    val title: String,
    val url: String,
    val desc: String,
    val image: Image,
)