package com.example.dietiestates.ui.screens.components

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dietiestates.AppContainer
import com.example.dietiestates.data.model.PropertyOffer
import com.example.dietiestates.utility.TokenManager

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


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
            .fillMaxWidth(),
        horizontalArrangement =
        if (userRole == "CLIENT") {
                if (offer.madeByUser)
                    Arrangement.End
                else
                    Arrangement.Start
            }
        else {
            if (offer.madeByUser)
                Arrangement.Start
            else
                Arrangement.End
        }

    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(backgroundColor) // verde chiaro o grigio
                .padding(12.dp)
        ) {
//            if(userRole != "CLIENT") {
//                Text(text = "Autore Offerta", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
//            }

            if (name != null && surname != null && email != null) {
                Text(text = "$name $surname", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(text = "${email}", style = MaterialTheme.typography.bodyLarge)
            }
            Text(text = "€ ${offer.price}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)

            Text(text = "$stato", style = MaterialTheme.typography.bodySmall)
            Text(text = formattedDate, style = MaterialTheme.typography.labelSmall, color = Color.Gray)

            if( offer.state == "PENDENT") {
                if(userRole == "CLIENT") {
                    if (!offer.madeByUser) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Rifiuta",
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Red.copy(alpha = 0.7f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                    .clickable { onDecline?.invoke() },
                                color = Color.White,
                                style = MaterialTheme.typography.labelMedium
                            )

                            Text(
                                text = "Accetta",
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF4CAF50))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                    .clickable { onAccept?.invoke() },
                                color = Color.White,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
                else {
                    if (offer.madeByUser) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Rifiuta",
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Red.copy(alpha = 0.7f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                    .clickable { onDecline?.invoke() },
                                color = Color.White,
                                style = MaterialTheme.typography.labelMedium
                            )

                            Text(
                                text = "Accetta",
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF4CAF50))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                    .clickable { onAccept?.invoke() },
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
