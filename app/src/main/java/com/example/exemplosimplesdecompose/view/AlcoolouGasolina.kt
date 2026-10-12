package com.example.exemplosimplesdecompose.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
import com.example.exemplosimplesdecompose.data.Posto
import com.example.exemplosimplesdecompose.data.Coordenadas
import com.example.exemplosimplesdecompose.data.PostosRepo
import androidx.compose.runtime.LaunchedEffect
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import com.example.exemplosimplesdecompose.R

@SuppressLint("MissingPermission")
fun pegarLocalizacao(
    fusedLocationClient: FusedLocationProviderClient,
    onLocationPronta: (Coordenadas) -> Unit
) {
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            onLocationPronta(Coordenadas(location.latitude, location.longitude))
        }
    }
}
@Composable
fun AlcoolGasolinaPreco(navController: NavHostController) {
    val context = LocalContext.current
    val repository = remember { PostosRepo(context)
    }

    var alcool by rememberSaveable { mutableStateOf("") }
    var gasolina by rememberSaveable { mutableStateOf("") }
    var nomeDoPosto by rememberSaveable { mutableStateOf("") }
    var checkedState by remember { mutableStateOf(repository.obterEstadoSwitch()) }

    val nomeParaEditar = navController.currentBackStackEntry?.savedStateHandle?.get<String>("edit_nome")


    LaunchedEffect(nomeParaEditar)
    {
        if (nomeParaEditar != null)
            {
             nomeDoPosto = nomeParaEditar
             alcool = navController.currentBackStackEntry?.savedStateHandle?.get<String>("edit_alcool") ?: ""
             gasolina = navController.currentBackStackEntry?.savedStateHandle?.get<String>("edit_gasolina") ?: ""

             navController.currentBackStackEntry?.savedStateHandle?.remove<String>("edit_nome")
            }
    }

    var coordenadasAtuais by remember  { mutableStateOf(Coordenadas(0.0,0.0))}

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { permitido ->
        if (permitido){
            pegarLocalizacao(fusedLocationClient) { coords -> coordenadasAtuais = coords}
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            pegarLocalizacao(fusedLocationClient) { coords -> coordenadasAtuais = coords }
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }


    // A surface container using the 'background' color from the theme
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize(Alignment.Center)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OutlinedTextField(
                value = alcool,
                onValueChange = { alcool = it }, // Atualiza o estado
                label = { Text(stringResource(R.string.label_alcool)) },
                modifier = Modifier.fillMaxWidth(), // Preenche a largura disponível
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number) // Configuração do teclado
            )

            OutlinedTextField(
                value = gasolina,
                onValueChange = { gasolina = it },
                label = { Text(stringResource(R.string.label_gasolina)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = nomeDoPosto,
                onValueChange = { nomeDoPosto = it },
                label = { Text("Nome do Posto (Opcional))") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = if (checkedState) "Álcool" else "Gasolina",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 16.dp, end = 16.dp)
                )
                Switch(
                    modifier = Modifier.semantics { contentDescription = "Demo with icon" },
                    checked = checkedState,
                    onCheckedChange = { novoEstado ->
                        checkedState = novoEstado
                        repository.salvarEstadoSwitch(novoEstado)
                    },
                    thumbContent = {
                        if (checkedState) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                modifier = Modifier.size(SwitchDefaults.IconSize),
                            )
                        }
                    }
                )
            }
            // Botão de cálculo

            Button(
                onClick = {
                    val precoGasolina = gasolina.replace(",", ".").toDoubleOrNull() ?: 0.0
                    val precoAlcool = alcool.replace(",", ".").toDoubleOrNull() ?: 0.0
                    val nome = if(nomeDoPosto.isNotBlank()) nomeDoPosto else "Posto ${(1000..9999).random()}"

                    val formatoData = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    val dataAtual = formatoData.format(Date())

                    val novoPosto = Posto(
                        nome = nome,
                        precoAlcool = precoAlcool,
                        precoGasolina = precoGasolina,
                        dataAtualizacao = dataAtual,
                        coordenadas = coordenadasAtuais
                    )


                    repository.salvarPosto(novoPosto)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.btn_calcular))
            }

            val precoGasolinaVisual = gasolina.replace(",", ".").toDoubleOrNull() ?: 0.0
            val precoAlcoolVisual = alcool.replace(",", ".").toDoubleOrNull() ?: 0.0
            val limiteCompensacao = precoGasolinaVisual * 0.70
            val compensaAlcool = precoAlcoolVisual < limiteCompensacao


            Text(
                text = if (compensaAlcool) stringResource(R.string.compensa_alcool) else stringResource(R.string.compensa_gasolina),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
                horizontalArrangement = Arrangement.End) {
                FloatingActionButton(
                    onClick =
                        {
                        val parametro = if (nomeDoPosto.isNotBlank()) nomeDoPosto else "Posto_Sem_Nome"

                        navController.navigate("lista/$parametro")
                        },

                    ) {
                    Icon(Icons.Filled.Add, stringResource(R.string.inserir_posto))
                }
            }
        }
    }
}
