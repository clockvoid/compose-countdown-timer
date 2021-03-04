/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class TimerState {
    PREPARE,
    READY,
    STARTED,
}

class MainViewModel : ViewModel() {
    private val timerSecFlow: MutableStateFlow<Int> = MutableStateFlow(0)
    val timerSec: StateFlow<Int> = timerSecFlow
    private val timerMinFlow: MutableStateFlow<Int> = MutableStateFlow(0)
    val timerMin: StateFlow<Int> = timerMinFlow

    private val timerStateFlow: MutableStateFlow<TimerState> = MutableStateFlow(TimerState.PREPARE)
    val timerState: StateFlow<TimerState> = timerStateFlow

    private var countDownJob: Job? = null

    fun setTimerInSec(secIn: Int) {
        if (countDownJob?.isActive == true) {
            countDownJob!!.cancel()
            timerSecFlow.value = 0
            timerMinFlow.value = 0
            timerStateFlow.value = TimerState.PREPARE
        }

        val times = listOf(
            timerMin.value / 10,
            timerMin.value - timerMin.value / 10 * 10,
            timerSec.value / 10,
            timerSec.value - timerSec.value / 10 * 10
        )

        if (times[0] != 0) return

        val newTimes = times.mapIndexed { index, _ ->
            if (index != 3) {
                times[index + 1]
            } else {
                secIn
            }
        }

        timerMinFlow.value = newTimes[0] * 10 + newTimes[1]
        timerSecFlow.value = newTimes[2] * 10 + newTimes[3]
        if (timerSec.value > 0 || timerMin.value > 0) timerStateFlow.value = TimerState.READY
    }

    fun countDownTimer() {
        if (timerState.value != TimerState.READY) return
        countDownJob = viewModelScope.launch {
            timerStateFlow.value = TimerState.STARTED
            while (timerSec.value > 0 || timerMin.value > 0) {
                delay(1000)
                if (timerSec.value == 0) {
                    timerMinFlow.value -= 1
                    timerSecFlow.value = 59
                } else {
                    timerSecFlow.value -= 1
                }
            }
            timerStateFlow.value = TimerState.PREPARE
        }
    }

    fun resetTimer() {
        when (timerState.value) {
            TimerState.READY -> {
                timerSecFlow.value = 0
                timerMinFlow.value = 0
                timerStateFlow.value = TimerState.PREPARE
            }
            TimerState.STARTED -> {
                countDownJob?.cancel()
                timerStateFlow.value = TimerState.READY
            }
            else -> {
            }
        }
    }
}
