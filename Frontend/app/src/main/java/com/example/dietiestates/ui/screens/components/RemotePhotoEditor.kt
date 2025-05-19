package com.example.dietiestates.ui.screens.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Add
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dietiestates.ui.theme.LocalAppTypography
import com.example.dietiestates.ui.viewModel.ListingViewModel

@Composable
fun RemotePhotoEditor(
    imageUrls: List<String>,
    listingId: String,
    modifier: Modifier,
) {
    val viewModel: ListingViewModel = viewModel()
    var showConfirmDialog by remember { mutableStateOf(false) }
    var imageToDelete by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            viewModel.uploadListingImages(
                context = context,
                listingId = listingId,
                uris = uris,
            )
        }
    }

    Column(
        modifier
            .padding(10.dp)
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        if (showConfirmDialog && imageToDelete != null) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                title = { Text("Conferma eliminazione") },
                text = { Text("Sei sicuro di voler eliminare questa immagine?") },
                confirmButton = {
                    TextButton(onClick = {
                        showConfirmDialog = false
                        viewModel.deleteImage(
                            listingId = listingId,
                            filename = imageToDelete!!,
                        )
                    }) {
                        Text("Elimina")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmDialog = false }) {
                        Text("Annulla")
                    }
                }
            )
        }

        Text("Gestione foto", style = MaterialTheme.typography.titleLarge)
        Text("Attenzione, le modifiche alle foto verranno apportate subito", style = LocalAppTypography.current.featureValue)
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(imageUrls) { url ->
                Box {
                    Image(
                        painter = rememberAsyncImagePainter(url),
                        contentDescription = null,
                        modifier = Modifier
                            .size(width = 200.dp, height = 150.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = {
                            imageToDelete = estraiNomeFileDaUrl(url)
                            showConfirmDialog = true
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(24.dp)
                            .offset(x = (-4).dp, y = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Elimina",
                            tint = Color.Red
                        )
                    }
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .size(width = 200.dp, height = 150.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.LightGray)
                        .clickable {
                            launcher.launch(arrayOf("image/*"))
                        },
                    contentAlignment = Alignment.Center,

                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Aggiungi",
                        tint = Color.DarkGray,
                        modifier = Modifier.size(70.dp)
                    )
                }
            }
        }
    }

}


private fun estraiNomeFileDaUrl(url: String): String {
    return Uri.parse(url).lastPathSegment ?: ""
}