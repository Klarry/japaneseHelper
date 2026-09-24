package com.japanesehelper.presentation.screens.aiAgentScreen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.japanesehelper.R
import com.japanesehelper.domain.model.AgentPipeline
import com.japanesehelper.domain.model.AgentPipelineStage
import com.japanesehelper.domain.model.AgentPipelineWord
import com.japanesehelper.presentation.theme.LocalAppPadding

/**
 * What the backend's chain did, under the answer it produced: the three
 * stages with a tick or a cross, then what each of them left behind - the
 * words search found, the summary, and the file the result was saved to.
 *
 * It displays and nothing else. The device does not run the chain, does not
 * know which stage follows which, and does not work out whether it finished:
 * a stage is shown because the backend reported it, and the stages after a
 * failed one are missing here because the backend never ran them.
 */
@Composable
fun PipelineStatus(pipeline: AgentPipeline, modifier: Modifier = Modifier) {
    val reported = pipeline.steps.associateBy { it.stage }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Column(modifier = Modifier.padding(LocalAppPadding.current.half)) {
            ReadoutCaption(stringResource(R.string.ai_agent_pipeline_title))

            AgentPipelineStage.values().forEach { stage ->
                val step = reported[stage]

                Text(
                    text = stringResource(
                        when {
                            step == null -> R.string.ai_agent_pipeline_step_skipped
                            step.ok -> R.string.ai_agent_pipeline_step_ok
                            else -> R.string.ai_agent_pipeline_step_failed
                        },
                        stringResource(stage.label())
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = when {
                        step == null -> MaterialTheme.colorScheme.outline
                        step.ok -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.error
                    }
                )
            }

            pipeline.failed?.let { step ->
                Outcome(
                    stringResource(
                        R.string.ai_agent_pipeline_stopped,
                        stringResource(step.stage.label()),
                        step.error.ifBlank { stringResource(R.string.ai_agent_pipeline_no_reason) }
                    )
                )
            }

            pipeline.found.takeIf { it.isNotEmpty() }?.let { words ->
                Outcome(stringResource(R.string.ai_agent_pipeline_found, words.joinToString("; ") { it.readable() }))
            }

            pipeline.summary.takeIf { it.isNotBlank() }?.let { summary ->
                Outcome(stringResource(R.string.ai_agent_pipeline_summary, summary), maxLines = 4)
            }

            pipeline.fileName.takeIf { it.isNotBlank() }?.let { name ->
                Outcome(stringResource(R.string.ai_agent_pipeline_saved, name))
            }
        }
    }
}

@Composable
private fun Outcome(text: String, maxLines: Int = 2) {
    Text(
        text = text,
        modifier = Modifier.padding(top = LocalAppPadding.current.quarter),
        style = MaterialTheme.typography.bodySmall,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis
    )
}

private fun AgentPipelineStage.label(): Int = when (this) {
    AgentPipelineStage.SEARCH -> R.string.ai_agent_pipeline_search
    AgentPipelineStage.SUMMARIZE -> R.string.ai_agent_pipeline_summarize
    AgentPipelineStage.SAVE -> R.string.ai_agent_pipeline_save
}

/** "学習 — がくしゅう (gakushū) — study, learning — N3", with whatever the
 * dictionary left empty left out. */
private fun AgentPipelineWord.readable(): String {
    val readAs = when {
        reading.isNotBlank() && romaji.isNotBlank() -> "$reading ($romaji)"
        else -> reading.ifBlank { romaji }
    }

    return listOf(word, readAs, meaning, level).filter { it.isNotBlank() }.joinToString(" · ")
}
