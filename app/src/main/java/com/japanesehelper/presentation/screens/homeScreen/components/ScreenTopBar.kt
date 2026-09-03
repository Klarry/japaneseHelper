package com.japanesehelper.presentation.screens.homeScreen.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.japanesehelper.R

/**
 * The top bar shared by every detail screen (AI Explanation, Kanji Word Set,
 * Temperature Description): a title plus the standard Material back arrow.
 *
 * Centralizing it is what keeps the back arrow's icon, size, tint and
 * position identical everywhere in the app, and its behavior consistent -
 * every screen just pops the nav back stack.
 *
 * @param title The screen's title.
 * @param onBack Called when the back arrow is tapped - typically `navController::popBackStack`.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenTopBar(title: String, onBack: () -> Unit) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.nav_back)
                )
            }
        }
    )
}
