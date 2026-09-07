package com.japanesehelper.presentation.screens.aiAgentScreen.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.japanesehelper.R
import com.japanesehelper.presentation.screens.homeScreen.components.AppProgressIndicator
import com.japanesehelper.presentation.theme.LocalAppPadding
import com.japanesehelper.presentation.theme.LocalAppSize

/**
 * Disabled both on blank input and while a request is in flight, so it can't
 * fire an empty or a duplicate request.
 */
@Composable
fun AskAgentButton(
    canAsk: Boolean,
    isLoading: Boolean,
    onAsk: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onAsk,
        enabled = canAsk && !isLoading,
        modifier = modifier.fillMaxWidth()
    ) {
        if (isLoading) {
            AppProgressIndicator(size = LocalAppSize.current.indicatorSmall)
            Spacer(Modifier.width(LocalAppPadding.current.half))
        }
        Text(stringResource(R.string.ai_agent_ask_button))
    }
}
