package com.japanesehelper.presentation.screens.homeScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.japanesehelper.R
import com.japanesehelper.presentation.theme.LocalAppPadding

/**
 * The error state shared by every screen with a retryable request: the error
 * message (or a generic fallback when the backend didn't send one) and a
 * Retry button. One look and one behavior everywhere a request can fail.
 *
 * @param message The error message to show; falls back to a generic one when blank.
 * @param onRetry Called when the user taps Retry.
 */
@Composable
fun ErrorWithRetry(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(LocalAppPadding.current.default)
    ) {
        Text(
            text = message.ifBlank { stringResource(R.string.error_generic) },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Button(onClick = onRetry) {
            Text(stringResource(R.string.action_retry))
        }
    }
}
