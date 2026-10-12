package com.example.exemplosimplesdecompose.view

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.exemplosimplesdecompose.data.Coordenadas
import com.example.exemplosimplesdecompose.data.Posto
import com.example.exemplosimplesdecompose.data.PostosRepo
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.res.stringResource
import com.example.exemplosimplesdecompose.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaDePostos(navController: NavHostController, nomeDoPosto: String) {
    val context= LocalContext.current
    val repository = remember { PostosRepo(context) }

    var postosSalvos by remember { mutableStateOf(repository.obterPostos()) }


    // val postoSP = Posto("Posto SP", Coordenadas(41.40338, 2.17403))
    // val postoNY = Posto("Posto em NY", Coordenadas(40.7128, -74.0060))
    // val postoN= Posto(nomeDoPosto)
    // val postos = listOf(nomeDoPosto, "Outro posto", "Mais um posto")
    // val postosComp = listOf(postoN, postoSP, postoNY)

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.titulo_lista)) }) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(postosSalvos) { item ->
                Card(
                    onClick = {
                        val uri = Uri.parse("geo:${item.coordenadas.latitude},${item.coordenadas.longitude}?q=${item.coordenadas.latitude},${item.coordenadas.longitude}(${Uri.encode(item.nome)})")
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        context.startActivity(intent)

                    },
                    modifier = Modifier.fillMaxWidth().padding(10.dp)
                ) {
                    Box(Modifier.fillMaxSize()) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.nome,
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(text = "${stringResource(R.string.alcool)}: R$ ${item.precoAlcool} \n${stringResource(R.string.gasolina)}: R$ ${item.precoGasolina}")
                                Text(text = "${stringResource(R.string.data)}: ${item.dataAtualizacao}")

                                Text(
                                    text = "${stringResource(R.string.gps)}: ${"%.4f".format(item.coordenadas.latitude)}, ${"%.4f".format(item.coordenadas.longitude)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }

                            Row {
                                IconButton(onClick = {
                                    val uri = Uri.parse("geo:${item.coordenadas.latitude},${item.coordenadas.longitude}?q=${item.coordenadas.latitude},${item.coordenadas.longitude}(${Uri.encode(item.nome)})")
                                    val intent = Intent(Intent.ACTION_VIEW, uri)
                                    context.startActivity(intent)
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.LocationOn,
                                        contentDescription = stringResource(R.string.ver_mapa),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }

                                IconButton(onClick = {
                                    navController.previousBackStackEntry?.savedStateHandle?.set("edit_nome", item.nome)
                                    navController.previousBackStackEntry?.savedStateHandle?.set("edit_alcool", item.precoAlcool.toString())
                                    navController.previousBackStackEntry?.savedStateHandle?.set("edit_gasolina", item.precoGasolina.toString())
                                    navController.popBackStack()
                                }) {
                                    Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.editar))
                                }

                                IconButton(onClick = {
                                    repository.deletarPosto(item.nome)
                                    postosSalvos = repository.obterPostos()
                                }) {
                                    Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.excluir))
                                }
                            }
                        }}


                }
            }
        }
    }
}