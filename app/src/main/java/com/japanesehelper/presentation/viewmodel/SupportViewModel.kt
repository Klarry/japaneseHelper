package com.japanesehelper.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.japanesehelper.domain.model.ModelProvider
import com.japanesehelper.domain.model.SupportRequestFailed
import com.japanesehelper.domain.model.SupportTicketNotFound
import com.japanesehelper.domain.repository.AgentRepository
import com.japanesehelper.presentation.viewmodel.screendata.SupportResult
import com.japanesehelper.presentation.viewmodel.screendata.SupportScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Day 33: the support assistant screen.
 *
 * Sends a question and a ticket id to the backend and shows what comes back.
 * The ticket lookup (MCP), the help-centre search (RAG) and the answer itself
 * all happen on the backend; when it does not answer, the screen says why
 * and shows nothing in place of an answer.
 */
@HiltViewModel
class SupportViewModel @Inject constructor(
    private val agentRepository: AgentRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SupportScreenState())
    val state: StateFlow<SupportScreenState> = _state

    fun onTicketIdChanged(ticketId: String) {
        _state.value = _state.value.copy(ticketId = ticketId)
    }

    fun onQuestionChanged(question: String) {
        _state.value = _state.value.copy(question = question)
    }

    fun onProviderChanged(provider: ModelProvider) {
        _state.value = _state.value.copy(provider = provider)
    }

    fun send() {
        val current = _state.value
        if (!current.canSend) return

        val ticketId = current.normalizedTicketId
        val question = current.question.trim()
        val provider = current.provider

        _state.value = current.copy(ticketId = ticketId, result = SupportResult.Loading)

        viewModelScope.launch {
            val result = try {
                SupportResult.Answered(agentRepository.askSupport(question, ticketId, provider))
            } catch (e: SupportTicketNotFound) {
                SupportResult.TicketNotFound(e.ticketId)
            } catch (e: SupportRequestFailed) {
                SupportResult.Failed(e.message.orEmpty(), e.error)
            } catch (e: CancellationException) {
                throw e
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                SupportResult.Failed(e.message.orEmpty())
            }

            _state.value = _state.value.copy(result = result)
        }
    }
}
