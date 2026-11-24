package ru.nto.guestmanagement.mobile.task.a3

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.nto.guestmanagement.mobile.data.PassRepository
import ru.nto.guestmanagement.mobile.model.PassData
import ru.nto.guestmanagement.mobile.model.PassScreenState

/** A3. Логика загрузки и обновления данных пропуска. */
class PassLoadController(
    private val repository: PassRepository,
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow<PassScreenState>(PassScreenState.InitialLoading)
    val state: StateFlow<PassScreenState> = _state.asStateFlow()

    private var started = false
    private var requestInFlight = false

    fun start() {
        if (started || requestInFlight) return
        started = true
        loadInitial()
    }

    fun refresh() {
        if (requestInFlight) return
        val current = _state.value as? PassScreenState.Content ?: return
        _state.value = current.copy(isRefreshing = true)
        loadRefresh(current.data)
    }

    fun retry() {
        if (requestInFlight) return
        if (_state.value !is PassScreenState.InitialError) return
        _state.value = PassScreenState.InitialLoading
        loadInitial()
    }

    private fun loadInitial() {
        requestInFlight = true
        scope.launch {
            try {
                repository.load()
                    .onSuccess { data ->
                        _state.value = PassScreenState.Content(
                            data = data,
                            isRefreshing = false,
                            isOffline = false,
                        )
                    }
                    .onFailure { error ->
                        _state.value = PassScreenState.InitialError(
                            message = error.message?.takeIf { it.isNotBlank() } ?: DEFAULT_ERROR_MESSAGE,
                        )
                    }
            } finally {
                requestInFlight = false
            }
        }
    }

    private fun loadRefresh(previousData: PassData) {
        requestInFlight = true
        scope.launch {
            try {
                repository.load()
                    .onSuccess { data ->
                        _state.value = PassScreenState.Content(
                            data = data,
                            isRefreshing = false,
                            isOffline = false,
                        )
                    }
                    .onFailure {
                        _state.value = PassScreenState.Content(
                            data = previousData,
                            isRefreshing = false,
                            isOffline = true,
                        )
                    }
            } finally {
                requestInFlight = false
            }
        }
    }

    private companion object {
        const val DEFAULT_ERROR_MESSAGE = "Не удалось загрузить пропуск"
    }
}
