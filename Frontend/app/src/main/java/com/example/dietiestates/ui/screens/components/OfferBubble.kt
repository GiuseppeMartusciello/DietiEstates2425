package com.example.dietiestates.ui.screens.components

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dietiestates.data.model.PropertyOffer
import com.example.dietiestates.ui.theme.LocalAppTypography
import com.example.dietiestates.ui.theme.Roboto
import com.example.dietiestates.utility.TokenManager
import com.example.dietiestates.utility.formatNumberWithDots


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OfferBubble(
    offer: PropertyOffer,
    name: String? = null,
    surname: String? = null,
    email: String? = null,
    onAccept: (() -> Unit)? = null,
    onDecline: (() -> Unit)? = null
) {

    val userRole = TokenManager.getUserRole()


    val formattedDate = offer.date.split("T")[0]


    val stato: String = when (offer.state) {
        "PENDENT" -> "In attesa"
        "ACCEPTED" -> "Accettata"
        else -> "Rifiutata"
    }

    val backgroundColor: Color = when (offer.state) {
        "ACCEPTED" -> Color(0xFFB9F6CA) // Verde chiaro
        "PENDENT" -> Color(0xFFEEEEEE) // Grigio chiaro
        else -> Color(0xFFFFCDD2)      // Rosso chiaro
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp, horizontal = 10.dp),
        horizontalArrangement =
        if (userRole == "CLIENT") {
            if (offer.madeByUser)
                Arrangement.End
            else
                Arrangement.Start
        } else {
            if (offer.madeByUser)
                Arrangement.Start
            else
                Arrangement.End
        }

    ) {
        val width = if (name != null && surname != null && email != null) 250.dp else 200.dp
        val alignment = if (name != null && surname != null && email != null) Modifier.align(Alignment.Bottom) else Modifier.align(Alignment.Top)

        Card(
            modifier = Modifier
                .width(width)
                .clip(RoundedCornerShape(12.dp)),
            border = BorderStroke(1.dp, Color.LightGray),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor) // verde chiaro o grigio
                    .padding(10.dp)
            ) {
                if (name != null && surname != null && email != null) {
                    Text(
                        text = "$name $surname",
                        style = LocalAppTypography.current.listingTitle,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "${email}",
                        style = LocalAppTypography.current.featureValue,
                        color = Color.DarkGray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 20.dp)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "€ ${formatNumberWithDots(offer.price.toLong())}",
                        style = LocalAppTypography.current.listingPrice,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = alignment
                    )
                    Column(
                        modifier = Modifier
                    ) {
                        Text(
                            text = "$stato",
                            fontFamily = Roboto,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            lineHeight = 21.sp,
                            color = Color.Black,
                            modifier = Modifier.align(Alignment.End)
                        )

                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }

                if (offer.state == "PENDENT") {
                    if (userRole == "CLIENT") {
                        if (!offer.madeByUser) {
                            Divider(
                                thickness = 0.7.dp,
                                color = Color.LightGray,
                                modifier = Modifier.padding(vertical = 10.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Text(
                                    text = "Rifiuta",
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .width(80.dp)
                                        .background(Color.Red.copy(alpha = 0.7f))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                        .clickable { onDecline?.invoke() },
                                    textAlign = TextAlign.Center,
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelMedium
                                )

                                Text(
                                    text = "Accetta",
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF4CAF50))
                                        .width(80.dp)
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                        .clickable { onAccept?.invoke() },
                                    textAlign = TextAlign.Center,
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    } else {
                        if (offer.madeByUser) {
                            Divider(
                                thickness = 0.7.dp,
                                color = Color.LightGray,
                                modifier = Modifier.padding(vertical = 10.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Text(
                                    text = "Rifiuta",
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .width(80.dp)
                                        .background(Color.Red.copy(alpha = 0.7f))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                        .clickable { onDecline?.invoke() },
                                    textAlign = TextAlign.Center,
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelMedium
                                )

                                Text(
                                    text = "Accetta",
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF4CAF50))
                                        .width(80.dp)
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                        .clickable { onAccept?.invoke() },
                                    textAlign = TextAlign.Center,
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}