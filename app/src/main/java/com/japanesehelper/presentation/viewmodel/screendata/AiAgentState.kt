package com.japanesehelper.presentation.viewmodel.screendata

import com.japanesehelper.domain.model.AgentContext
import com.japanesehelper.domain.model.AgentContextStrategy
import com.japanesehelper.domain.model.AgentInvariant
import com.japanesehelper.domain.model.AgentInvariantCategory
import com.japanesehelper.domain.model.AgentMemory
import com.japanesehelper.domain.model.AgentMessage
import com.japanesehelper.domain.model.AgentTaskState
import com.japanesehelper.domain.model.AgentTokenUsage
import com.japanesehelper.domain.model.AgentUserProfile

sealed class AgentHistoryUiState {
    data object Loading : AgentHistoryUiState()
    data class Error(val message: String) : AgentHistoryUiState()
    data class Loaded(val messages: List<AgentMessage>) : AgentHistoryUiState()
}

/** The invariants editor while it is open. ``editingId`` is null while a new
 * rule is being written and the rule's id once one is picked for changing. */
data class InvariantEditorState(
    val editingId: String? = null,
    val category: AgentInvariantCategory = AgentInvariantCategory.ARCHITECTURE,
    val rule: String = ""
)

/** The profile editor while it is open: the settings as they are being
 * edited, before they are sent. */
data class ProfileEditorState(
    val profile: AgentUserProfile
)

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
    /** How the learner wants to be answered. Kept and applied on the
     * backend; the screen shows it and sends changes. */
    val profile: AgentUserProfile? = null,
    val isProfileWorking: Boolean = false,
    val profileError: String? = null,
    val profileEditor: ProfileEditorState? = null,
    /** Where the task in progress has got to. The stages and the moves
     * between them belong to the backend; this is what it reports. */
    val taskState: AgentTaskState? = null,
    val isTaskWorking: Boolean = false,
    /** The rules the backend will not let the agent break. Shown here and
     * edited through the backend; nothing is checked against them on the
     * device. */
    val invariants: List<AgentInvariant>? = null,
    val isInvariantsWorking: Boolean = false,
    val invariantsError: String? = null,
    val invariantEditor: InvariantEditorState? = null,
    /** Token usage for the last successful send this session - not part of
     * persisted history, so it starts empty on every screen open. */
    val lastUsage: AgentTokenUsage? = null
)
