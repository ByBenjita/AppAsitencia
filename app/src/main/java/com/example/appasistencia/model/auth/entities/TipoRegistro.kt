package contrexempie.appassistence.model.entities


enum class TipoRegistro(
    val displayName: String,
) {
    ENTRADA(
        displayName = "Entrada",
    ),
    SALIDA(
        displayName = "Salida",
    )
}