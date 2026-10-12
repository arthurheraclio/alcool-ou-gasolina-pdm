package com.example.exemplosimplesdecompose.data;

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

public class PostosRepo(context: Context) {
    private val prefs = context.getSharedPreferences("PostosAppPrefs", Context.MODE_PRIVATE)

    fun salvarEstadoSwitch(isChecked: Boolean) {
        prefs.edit().putBoolean("SWITCH_ESTADO", isChecked).apply()
    }

    fun obterEstadoSwitch(): Boolean {
        return prefs.getBoolean("SWITCH_ESTADO", true)
    }

    fun obterPostos(): List<Posto> {
        val jsonString = prefs.getString("LISTA_POSTOS", null) ?: return emptyList()
        val lista = mutableListOf<Posto>()

        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val jsonObj = jsonArray.getJSONObject(i)

                val coordObj = jsonObj.getJSONObject("coordenadas")
                val coordenadas = Coordenadas(
                    latitude = coordObj.getDouble("latitude"),
                    longitude = coordObj.getDouble("longitude")
                )


                val posto = Posto(
                    nome = jsonObj.getString("nome"),
                    precoAlcool = jsonObj.getDouble("precoAlcool"),
                    precoGasolina = jsonObj.getDouble("precoGasolina"),
                    dataAtualizacao = jsonObj.getString("dataAtualizacao"),
                    coordenadas = coordenadas
                )
                lista.add(posto)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return lista
    }

    private fun salvarLista(lista: List<Posto>) {
        val jsonArray = JSONArray()

        for (posto in lista) {
            val jsonObj = JSONObject().apply {
                put("nome", posto.nome)
                put("precoAlcool", posto.precoAlcool)
                put("precoGasolina", posto.precoGasolina)
                put("dataAtualizacao", posto.dataAtualizacao)


                val coordObj = JSONObject().apply {
                    put("latitude", posto.coordenadas.latitude)
                    put("longitude", posto.coordenadas.longitude)
                }
                put("coordenadas", coordObj)
            }
            jsonArray.put(jsonObj)
        }

        prefs.edit().putString("LISTA_POSTOS", jsonArray.toString()).apply()
    }

    fun salvarPosto(posto: Posto) {
        val postosAtuais = obterPostos().toMutableList()
        val index = postosAtuais.indexOfFirst { it.nome == posto.nome }

        if (index != -1) {
            postosAtuais[index] = posto
        } else {
            postosAtuais.add(posto)
        }

        salvarLista(postosAtuais)
    }

    fun deletarPosto(nomePosto: String) {
        val postosAtuais = obterPostos().filter { it.nome != nomePosto }
        salvarLista(postosAtuais)
    }
}
