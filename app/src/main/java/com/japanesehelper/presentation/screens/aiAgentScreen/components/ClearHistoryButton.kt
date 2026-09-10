package com.japanesehelper.presentation.screens.aiAgentScreen.components

import androidx.compose.foundation.layout.Spacer
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
 * Deliberately sized to its content, unlike AskAgentButton - this is a small,
 * secondary action that shares its row with the token readouts, not the
 * screen's main call to action.
 */
@Composable
fun ClearHistoryButton(
    enabled: Boolean,
    isLoading: Boolean,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(onClick = onClear, enabled = enabled && !isLoading, modifier = modifier) {
        if (isLoading) {
            AppProgressIndicator(size = LocalAppSize.current.indicatorSmall)
            Spacer(Modifier.width(LocalAppPadding.current.half))
        }
        Text(stringResource(R.string.ai_agent_clear_history_button))
    }
}
