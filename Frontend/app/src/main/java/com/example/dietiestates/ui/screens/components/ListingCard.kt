package com.example.dietiestates.ui.screens.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MeetingRoom
import androidx.compose.material.icons.outlined.RealEstateAgent
import androidx.compose.material.icons.outlined.SolarPower
import androidx.compose.material.icons.outlined.ViewInAr
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.dietiestates.R
import com.example.dietiestates.data.model.Listing
import com.example.dietiestates.ui.theme.AppTypography
import com.example.dietiestates.ui.theme.LocalAppTypography
import com.example.dietiestates.utility.formatNumberWithDots

@Composable
fun ListingCard(listing: Listing, onClick: () -> Unit) {
    val imageUrl = listing.imageUrls.firstOrNull()
    val painter = if (imageUrl != null) {
        rememberAsyncImagePainter(imageUrl)
    } else {
        painterResource(R.drawable.boh)
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)

    ) {
        Column (modifier = Modifier.background(Color(0xFFF3F3F3))) {
            Image(
                painter = painter,
                contentDescription = listing.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )

            Column (modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)) {
                Text(listing.title, style = LocalAppTypography.current.listingTitle)
                Text(text = listing.address, style = LocalAppTypography.current.listingAddress, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(12.dp))

                Column (modifier = Modifier.padding(0.dp)){
                    Text(text = "€ ${formatNumberWithDots(listing.price)}", style = LocalAppTypography.current.listingPrice)
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = Color(0xFFBDBDBD))
                    Row(modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 4.dp), horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        FeatureItem(icon = Icons.Outlined.RealEstateAgent, value = listing.category)
                        FeatureItem(icon = Icons.Outlined.ViewInAr, value = "${listing.size}mq")
                        FeatureItem(icon = Icons.Outlined.MeetingRoom, value = listing.numberOfRooms)
                        FeatureItem(icon = Icons.Outlined.SolarPower, value = listing.energyClass.toString())
                    }


                }
            }
        }
    }
}

@Composable
fun FeatureItem(icon: ImageVector, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color(0xFF49454F),
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .size(26.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
            Text(
                value, style = LocalAppTypography.current.featureValue
            )

    }
}
