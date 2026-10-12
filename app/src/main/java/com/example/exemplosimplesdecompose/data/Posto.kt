package com.example.exemplosimplesdecompose.data

data class Posto(
    val nome: String,
    val precoAlcool: Double,
    val precoGasolina: Double,
    val dataAtualizacao: String,
    val coordenadas: Coordenadas
){
    // Construtor secundário com coordenadas de Fortaleza
    constructor(nome: String) : this
        (
        nome = nome,
        precoAlcool = 0.0,
        precoGasolina = 0.0,
        dataAtualizacao = "",
        coordenadas = Coordenadas(41.40338, 2.17403)
        )

    constructor(nome: String, coordenadas: Coordenadas) : this(
        nome = nome,
        precoAlcool = 0.0,
        precoGasolina = 0.0,
        dataAtualizacao = "",
        coordenadas = coordenadas
    )
}