package com.japanesehelper.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.japanesehelper.domain.model.AgentContextStrategy
import com.japanesehelper.domain.model.AgentMessage
import com.japanesehelper.domain.model.AgentMessageRole
import com.japanesehelper.domain.repository.AgentRepository
import com.japanesehelper.presentation.viewmodel.screendata.AgentHistoryUiState
import com.japanesehelper.presentation.viewmodel.screendata.AiAgentScreenState
import com.japanesehelper.presentation.viewmodel.screendata.NewBranchState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

/**
 * The AI Agent screen is a simple chat: history is loaded once on open, every
 * sent message is appended (with the agent's reply) to the same list, and
 * "Clear History" empties it.
 *
 * Everything about context management belongs to the backend. This class
 * names a strategy, asks for checkpoints and branches, and displays what the
 * backend reports it would send - it never trims the history or builds facts
 * itself. After anything that could change that, it reloads the context
 * rather than guessing at the new state.
 */
@HiltViewModel
class AiAgentViewModel @Inject constructor(
    private val agentRepository: AgentRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AiAgentScreenState())
    val state: StateFlow<AiAgentScreenState> = _state

    init {
        loadHistory()
        loadContext(alignStrategy = true)
    }

    fun loadHistory() {
        _state.value = _state.value.copy(history = AgentHistoryUiState.Loading)

        viewModelScope.launch {
            try {
                val messages = agentRepository.getHistory()
                _state.value = _state.value.copy(history = AgentHistoryUiState.Loaded(messages))
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                _state.value = _state.value.copy(history = AgentHistoryUiState.Error(e.toErrorMessage()))
            }
        }
    }

    fun loadContext() = loadContext(alignStrategy = false)

    fun onMessageChanged(message: String) {
        _state.value = _state.value.copy(message = message)
    }

    fun onStrategySelected(strategy: AgentContextStrategy) {
        if (strategy == _state.value.strategy) return

        _state.value = _state.value.copy(strategy = strategy)

        viewModelScope.launch {
            try {
                agentRepository.setStrategy(strategy)
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                _state.value = _state.value.copy(contextError = e.toErrorMessage())
                return@launch
            }

            refreshContext()
        }
    }

    fun send() {
        val message = _state.value.message.trim()

        if (message.isEmpty()) return
        if (_state.value.isSending) return

        // Read once, so a strategy switched while this request is in flight
        // applies to the next one instead of mislabelling this one.
        val strategy = _state.value.strategy

        _state.value = _state.value.copy(isSending = true, sendError = null)

        viewModelScope.launch {
            try {
                val reply = agentRepository.chat(message, strategy)
                val updatedMessages = currentMessages() +
                    AgentMessage(role = AgentMessageRole.USER, content = message) +
                    AgentMessage(role = AgentMessageRole.ASSISTANT, content = reply.text)

                _state.value = _state.value.copy(
                    message = "",
                    isSending = false,
                    history = AgentHistoryUiState.Loaded(updatedMessages),
                    lastUsage = reply.usage
                )
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                _state.value = _state.value.copy(isSending = false, sendError = e.toErrorMessage())
                return@launch
            }

            refreshContext()
        }
    }

    fun clearHistory() {
        if (_state.value.isClearingHistory) return

        _state.value = _state.value.copy(isClearingHistory = true, clearHistoryError = null)

        viewModelScope.launch {
            try {
                agentRepository.clearHistory()
                _state.value = _state.value.copy(
                    isClearingHistory = false,
                    history = AgentHistoryUiState.Loaded(emptyList()),
                    lastUsage = null
                )
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                _state.value = _state.value.copy(
                    isClearingHistory = false,
                    clearHistoryError = e.toErrorMessage()
                )
                return@launch
            }

            refreshContext()
        }
    }

    // --- branching ---------------------------------------------------------

    fun createCheckpoint() {
        if (_state.value.isBranchWorking) return

        _state.value = _state.value.copy(isBranchWorking = true, contextError = null)

        viewModelScope.launch {
            try {
                agentRepository.createCheckpoint()
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                _state.value = _state.value.copy(isBranchWorking = false, contextError = e.toErrorMessage())
                return@launch
            }

            refreshContext()
            _state.value = _state.value.copy(isBranchWorking = false)
        }
    }

    fun openNewBranch() {
        // The newest checkpoint is the one a fork usually means; any other can
        // still be picked in the dialog.
        val checkpoint = _state.value.context?.checkpoints?.lastOrNull().orEmpty()
        _state.value = _state.value.copy(newBranch = NewBranchState(checkpoint = checkpoint))
    }

    fun onNewBranchNameChanged(name: String) {
        val dialog = _state.value.newBranch ?: return
        _state.value = _state.value.copy(newBranch = dialog.copy(name = name))
    }

    fun onNewBranchCheckpointSelected(checkpoint: String) {
        val dialog = _state.value.newBranch ?: return
        _state.value = _state.value.copy(newBranch = dialog.copy(checkpoint = checkpoint))
    }

    fun dismissNewBranch() {
        _state.value = _state.value.copy(newBranch = null)
    }

    fun confirmNewBranch() {
        val dialog = _state.value.newBranch ?: return
        val name = dialog.name.trim()

        if (name.isEmpty() || dialog.checkpoint.isEmpty()) return

        _state.value = _state.value.copy(newBranch = null, isBranchWorking = true, contextError = null)

        viewModelScope.launch {
            try {
                agentRepository.createBranch(name, dialog.checkpoint)
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                _state.value = _state.value.copy(isBranchWorking = false, contextError = e.toErrorMessage())
                return@launch
            }

            refreshContext()
            _state.value = _state.value.copy(isBranchWorking = false)
        }
    }

    fun switchBranch(name: String) {
        if (_state.value.isBranchWorking || name == _state.value.context?.branch) return

        _state.value = _state.value.copy(isBranchWorking = true, contextError = null)

        viewModelScope.launch {
            try {
                agentRepository.switchBranch(name)
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                _state.value = _state.value.copy(isBranchWorking = false, contextError = e.toErrorMessage())
                return@launch
            }

            // The branch that was switched to has its own conversation, so the
            // chat itself has to be reloaded, not just the context readout.
            loadHistory()
            refreshContext()
            _state.value = _state.value.copy(isBranchWorking = false, lastUsage = null)
        }
    }

    // --- internals ---------------------------------------------------------

    private fun loadContext(alignStrategy: Boolean) {
        viewModelScope.launch {
            if (!alignStrategy) {
                refreshContext()
                return@launch
            }

            val context = try {
                agentRepository.getContext()
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                _state.value = _state.value.copy(contextError = e.toErrorMessage())
                return@launch
            }

            val known = AgentContextStrategy.fromWireName(context.strategy)

            if (known != null) {
                _state.value = _state.value.copy(context = context, strategy = known, contextError = null)
                return@launch
            }

            // The backend is on a strategy this screen does not offer. Put it
            // on the one shown here, so what is displayed and what would be
            // sent cannot disagree.
            try {
                agentRepository.setStrategy(_state.value.strategy)
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                _state.value = _state.value.copy(context = context, contextError = e.toErrorMessage())
                return@launch
            }

            refreshContext()
        }
    }

    private suspend fun refreshContext() {
        try {
            _state.value = _state.value.copy(context = agentRepository.getContext(), contextError = null)
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            _state.value = _state.value.copy(contextError = e.toErrorMessage())
        }
    }

    private fun currentMessages(): List<AgentMessage> =
        (_state.value.history as? AgentHistoryUiState.Loaded)?.messages.orEmpty()

    /** HttpException.message is only "HTTP 400 Bad Request"; the reason is in the body. */
    private fun Exception.toErrorMessage(): String {
        if (this is HttpException) {
            val body = response()?.errorBody()?.string()
            if (!body.isNullOrBlank()) return body
        }
        return message.orEmpty()
    }
}
