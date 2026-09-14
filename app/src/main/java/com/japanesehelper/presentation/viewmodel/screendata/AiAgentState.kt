package com.japanesehelper.presentation.viewmodel.screendata

import com.japanesehelper.domain.model.AgentContext
import com.japanesehelper.domain.model.AgentContextStrategy
import com.japanesehelper.domain.model.AgentMemory
import com.japanesehelper.domain.model.AgentMessage
import com.japanesehelper.domain.model.AgentTokenUsage

sealed class AgentHistoryUiState {
    data object Loading : AgentHistoryUiState()
    data class Error(val message: String) : AgentHistoryUiState()
    data class Loaded(val messages: List<AgentMessage>) : AgentHistoryUiState()
}

/** The "new branch" dialog while it is open. */
data class NewBranchState(
    val name: String = "",
    val checkpoint: String = ""
)

data class AiAgentScreenState(
    val message: String = "",
    val history: AgentHistoryUiState = AgentHistoryUiState.Loading,
    val isSending: Boolean = false,
    val sendError: String? = null,
    val isClearingHistory: Boolean = false,
    val clearHistoryError: String? = null,
    /** Which strategy the next message is sent with. Choosing is all this
     * screen does with it - the strategy itself runs on the backend. */
    val strategy: AgentContextStrategy = AgentContextStrategy.SLIDING_WINDOW,
    /** What the backend reports it would send next: the window, the facts,
     * the branches. Never assembled here. */
    val context: AgentContext? = null,
    val isBranchWorking: Boolean = false,
    val contextError: String? = null,
    val newBranch: NewBranchState? = null,
    /** The three memory layers as the backend reports them. Loaded only
     * while the layered-memory strategy is chosen, since that is the only
     * strategy that writes to them. */
    val memory: AgentMemory? = null,
    val isMemoryWorking: Boolean = false,
    /** Token usage for the last successful send this session - not part of
     * persisted history, so it starts empty on every screen open. */
    val lastUsage: AgentTokenUsage? = null
)
