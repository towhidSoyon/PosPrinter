package com.towhid.posprinter

import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosPrintScreen(
    navController: NavController,
    viewModel: PosPrintViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val printOptions = listOf(
        PrintOption("Print Text", "📝") { viewModel.printText() },
        PrintOption("Print Barcode", "📊") { viewModel.printBarcode() },
        PrintOption("Print Picture", "🖼️") {
            val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.bd)
            viewModel.printPicture(bitmap)
        },
        PrintOption("Print QR Code", "📱") { viewModel.printQRCode() },
        PrintOption("Print Table", "📋") { viewModel.printTable() },

    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Print Functions") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(printOptions) { option ->
                PrintOptionCard(
                    title = option.title,
                    emoji = option.emoji,
                    onClick = option.action
                )
            }
        }
    }
}

@Composable
fun PrintOptionCard(
    title: String,
    emoji: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

data class PrintOption(
    val title: String,
    val emoji: String,
    val action: () -> Unit
)