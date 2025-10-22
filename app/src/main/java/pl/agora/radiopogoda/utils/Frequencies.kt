package pl.agora.radiopogoda.utils

enum class Frequencies(val city: String, val frequency: String) {

    Bydgoszcz("Bydgoszcz", "103,5"),
    Poznan("Poznań", "103,4"),
    Gdansk("Gdańsk", "87,8"),
    SIZ("Śląsk i Zagłębie", "94,5"),
    Krakow("Kraków", "102,4"),
    Warszawa("Warszawa", "88,4"),
    Opole("Opole", "104,1"),
    Wroclaw("Wrocław", "106,1");

    companion object {
        fun getLeftColumn() = values().copyOfRange(0, (values().size + 1) / 2)

        fun getRightColumn() = values().copyOfRange((values().size + 1) / 2, values().size)
    }
}