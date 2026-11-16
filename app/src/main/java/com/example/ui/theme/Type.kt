package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val PlusJakartaSansFontFamily = FontFamily(
    Font(R.font.plus_jakarta_sans, FontWeight.Bold),
    Font(R.font.plus_jakarta_sans, FontWeight.ExtraBold)
)

val NunitoSansFontFamily = FontFamily(
    Font(R.font.nunito_sans, FontWeight.SemiBold),
    Font(R.font.nunito_sans, FontWeight.Bold)
)

val PastelPlaylandTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = PlusJakartaSansFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 40.sp,
        lineHeight = 48.sp,
        letterSpacing = (-0.5).sp
    ),
    displayMedium = TextStyle(
        fontFamily = PlusJakartaSansFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.25).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = PlusJakartaSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = PlusJakartaSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 30.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = PlusJakartaSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 26.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = NunitoSansFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 26.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = NunitoSansFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodySmall = TextStyle(
        fontFamily = NunitoSansFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontFamily = PlusJakartaSansFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.3.sp
    ),
    labelMedium = TextStyle(
        fontFamily = PlusJakartaSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    labelSmall = TextStyle(
        fontFamily = PlusJakartaSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
)
