package com.japanesehelper.presentation.screens.aiAgentScreen.components

import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.japanesehelper.R
import com.japanesehelper.domain.model.AgentContextStrategy

@Composable
private fun AgentContextStrategy.label(): String = when (this) {
    AgentContextStrategy.SLIDING_WINDOW -> stringResource(R.string.ai_agent_strategy_sliding_window)
    AgentContextStrategy.STICKY_FACTS -> stringResource(R.string.ai_agent_strategy_sticky_facts)
    AgentContextStrategy.BRANCHING -> stringResource(R.string.ai_agent_strategy_branching)
}

/**
 * Chooses which context strategy the next message is sent with, in the same
 * TabRow style the Kanji Word Set screen uses to pick an experiment type. The
 * screen only makes the choice: windowing the history, keeping the facts and
 * holding the branches all happen on the backend.
 */
@Composable
fun ContextStrategyTabRow(
    selected: AgentContextStrategy,
    onSelected: (AgentContextStrategy) -> Unit,
    modifier: Modifier = Modifier
) {
    val strategies = AgentContextStrategy.entries

    TabRow(selectedTabIndex = strategies.indexOf(selected), modifier = modifier) {
        strategies.forEach { strategy ->
            Tab(
                selected = strategy == selected,
                onClick = { onSelected(strategy) },
                text = { Text(strategy.label()) }
            )
        }
    }
}
