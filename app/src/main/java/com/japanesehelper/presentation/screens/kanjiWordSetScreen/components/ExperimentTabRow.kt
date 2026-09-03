package com.japanesehelper.presentation.screens.kanjiWordSetScreen.components

import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.japanesehelper.R
import com.japanesehelper.domain.model.ExperimentType

@Composable
private fun ExperimentType.label(): String = when (this) {
    ExperimentType.DIRECT -> stringResource(R.string.kanji_word_set_tab_direct)
    ExperimentType.STEP_BY_STEP -> stringResource(R.string.kanji_word_set_tab_step_by_step)
    ExperimentType.PROMPT -> stringResource(R.string.kanji_word_set_tab_prompt)
    ExperimentType.EXPERTS -> stringResource(R.string.kanji_word_set_tab_experts)
}

@Composable
fun ExperimentTabRow(
    selectedTab: ExperimentType,
    onTabSelected: (ExperimentType) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = ExperimentType.entries

    TabRow(
        selectedTabIndex = tabs.indexOf(selectedTab),
        modifier = modifier
    ) {
        tabs.forEach { experimentType ->
            Tab(
                selected = experimentType == selectedTab,
                onClick = { onTabSelected(experimentType) },
                text = { Text(experimentType.label()) }
            )
        }
    }
}
