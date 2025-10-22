package pl.agora.radiopogoda.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import pl.agora.radiopogoda.data.model.history.History
import pl.agora.radiopogoda.infrastructure.repositories.ChannelsRepository
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val channelsRepository: ChannelsRepository,
): ViewModel() {

    private val _timeSliderState = MutableStateFlow(initTimeSlider())
    val timeSliderState: StateFlow<Pair<Int, Int>> = _timeSliderState

    private val _historyChannel = MutableStateFlow<List<History>?>(null)
    val historyChannel: StateFlow<List<History>?> = _historyChannel

    fun increaseSliderTime() {
        val currentHour = timeSliderState.value.first
        val newHour = if (currentHour == 23) 0 else currentHour + 1
        _timeSliderState.value = newHour to if (newHour == 23) 0 else newHour + 1
        updateHistoryData(newHour)
    }

    fun decreaseSliderTime() {
        val currentHour = timeSliderState.value.first
        val newHour = if (currentHour == 0) 23 else currentHour - 1
        _timeSliderState.value = newHour to if (newHour == 23) 0 else newHour + 1
        updateHistoryData(newHour)
    }

    private fun updateHistoryData(hour: Int) = viewModelScope.launch {
        _historyChannel.value = null

        val dateTime = LocalDateTime.now().withHour(hour).withMinute(0).withSecond(0)
        val timestamp = ZonedDateTime.ofInstant(
            dateTime.toInstant(OffsetDateTime.now().offset),
            ZoneId.of("Europe/Warsaw")
        ).toEpochSecond().toString()

        _historyChannel.value = channelsRepository.getHistoryChannel(timestamp, hour)
    }

    fun getActualHour() = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

    private fun initTimeSlider(): Pair<Int, Int> {
        val actualHour = getActualHour()
        return Pair(actualHour, if (actualHour == 23) 0 else actualHour + 1)
    }

    private fun updateHourHistory() = viewModelScope.launch {
        updateHistoryData(getActualHour())
        while (isActive) {
            delay(UPDATE_DELAY)
            if (timeSliderState.value.first == getActualHour()) {
                updateHistoryData(getActualHour())
            }
        }
    }

    init { updateHourHistory() }

    companion object {
        private const val UPDATE_DELAY = 60_000L
    }
}