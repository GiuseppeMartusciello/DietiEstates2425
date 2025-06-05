package com.example.dietiestates.ui.screens.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.dietiestates.R
import com.example.dietiestates.data.model.Listing


val BASE_IMAGE_URL = "http://dietiestates.duckdns.org:3000"



@Composable
fun ListingCardMini(listing: Listing, onClick: () -> Unit) {

    val imageUrl = listing.imageUrls.firstOrNull()?.let { url ->
        if (url.startsWith("http")) url else "$BASE_IMAGE_URL$url"
    }


    val painter = if (imageUrl != null) {
        rememberAsyncImagePainter(imageUrl)
    } else {
        painterResource(R.drawable.no_image)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
               .height(150.dp)
                .padding(8.dp)
        ) {
            Image(
                painter = painter,
                contentDescription = listing.title,
                modifier = Modifier
                    .width(120.dp)
                    //.height(150.dp),
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(vertical = 4.dp)
            ) {
                Text(text = listing.title, style = MaterialTheme.typography.titleMedium, maxLines = 2,  overflow = TextOverflow.Ellipsis)
                Text(text = listing.address, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "€ ${listing.price.toInt()}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
            }
        }

    }
}
