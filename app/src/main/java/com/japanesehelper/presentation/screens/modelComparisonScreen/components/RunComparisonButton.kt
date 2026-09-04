package com.japanesehelper.presentation.screens.modelComparisonScreen.components

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

/** Fires the single POST /model-comparison request for all compared models at once. */
@Composable
fun RunComparisonButton(
    isLoading: Boolean,
    onRun: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onRun,
        enabled = !isLoading,
        modifier = modifier.fillMaxWidth()
    ) {
        if (isLoading) {
            AppProgressIndicator(size = LocalAppSize.current.indicatorSmall)
            Spacer(Modifier.width(LocalAppPadding.current.half))
        }
        Text(stringResource(R.string.model_comparison_run_button))
    }
}
