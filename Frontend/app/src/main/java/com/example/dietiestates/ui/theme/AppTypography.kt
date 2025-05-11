package com.example.dietiestates.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

data class AppTypography(
    val sectionTitle: TextStyle,
    val featureTitle: TextStyle,
    val featureValue: TextStyle,
    val listingTitle: TextStyle,
    val listingAddress: TextStyle,
    val listingPrice: TextStyle,
)


val CustomTypography = AppTypography(
    sectionTitle = TextStyle(
        fontFamily = RobotoSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        color = Color.Black
    ),
    featureTitle = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp,
        lineHeight = 28.sp,
        color = Color.Black
    ),
    featureValue = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Light,
        fontSize = 12.sp,
        lineHeight = 21.sp,
        color = Color.Black
    ),
    listingTitle = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.W500,
        fontSize = 22.sp,
        lineHeight = 21.sp,
        color = Color.Black
    )
    ,
    listingAddress = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Light,
        fontSize = 16.sp,
        lineHeight = 21.sp,
        color = Color.Black
    ),
    listingPrice = TextStyle(
        fontFamily = RobotoSlab,
        fontWeight = FontWeight.SemiBold,
        fontSize = 25.sp,
        color = Color.Black
    )

)

val LocalAppTypography = staticCompositionLocalOf { CustomTypography }
