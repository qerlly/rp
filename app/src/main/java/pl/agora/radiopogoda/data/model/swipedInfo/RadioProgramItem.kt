package pl.agora.radiopogoda.data.model.swipedInfo

data class RadioProgramItem(
    val people: List<People>,
    val program: Program,
    val start: Int,
    val end: Int,
)

data class RadioProgramItemWithText(
    val people: List<People>,
    val program: Program,
    val start: String,
    val end: String,
)