package com.japanesehelper.presentation.screens.aiAgentScreen.components

import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.japanesehelper.R
import com.japanesehelper.domain.model.AgentContextStrategy

@Composable
private fun AgentContextStrategy.label(): String = when (this) {
    AgentContextStrategy.SLIDING_WINDOW -> stringResource(R.string.ai_agent_strategy_sliding_window)
    AgentContextStrategy.STICKY_FACTS -> stringResource(R.string.ai_agent_strategy_sticky_facts)
    AgentContextStrategy.BRANCHING -> stringResource(R.string.ai_agent_strategy_branching)
    AgentContextStrategy.LAYERED_MEMORY -> stringResource(R.string.ai_agent_strategy_layered_memory)
}

/**
 * Chooses which context strategy the next message is sent with, in the same
 * tab style the Kanji Word Set screen uses to pick an experiment type. The
 * screen only makes the choice: windowing the history, keeping the facts,
 * holding the branches and deciding what belongs in a memory layer all happen
 * on the backend.
 */
@Composable
fun ContextStrategyTabRow(
    selected: AgentContextStrategy,
    onSelected: (AgentContextStrategy) -> Unit,
    modifier: Modifier = Modifier
) {
    val strategies = AgentContextStrategy.entries

    // Scrollable rather than fixed: four labels this long would be cut off in
    // equal quarters of a phone screen, and the tab is the only place the
    // strategy is named.
    ScrollableTabRow(
        selectedTabIndex = strategies.indexOf(selected),
        modifier = modifier,
        edgePadding = 0.dp
    ) {
        strategies.forEach { strategy ->
            Tab(
                selected = strategy == selected,
                onClick = { onSelected(strategy) },
                text = { Text(strategy.label()) }
            )
        }
    }
}
