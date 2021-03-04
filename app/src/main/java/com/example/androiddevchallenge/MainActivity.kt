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

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androiddevchallenge.ui.theme.MyTheme

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                CountdownTimer(viewModel)
            }
        }
    }
}

@Composable
fun NumKeyPad(onClick: (Int) -> Unit) {
    val rows = ((1..9) + listOf(0)).chunked(3)

    Column {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { num ->
                    Text(
                        text = "$num",
                        fontSize = 64.sp,
                        modifier = Modifier.clickable {
                            onClick(num)
                        }
                    )
                }
            }
        }
    }
}

@Preview("Num Key Pad", widthDp = 360, heightDp = 640)
@Composable
fun NumKeyPadPreview() {
    MyTheme {
        Surface(color = MaterialTheme.colors.background) {
            NumKeyPad(onClick = { /*TODO*/ })
        }
    }
}

// Start building your app here!
@Composable
fun CountdownTimer(viewModel: MainViewModel) {
    val timerSec: State<Int> = viewModel.timerSec.collectAsState()
    val timerMin: State<Int> = viewModel.timerMin.collectAsState()
    val timerState: State<TimerState> = viewModel.timerState.collectAsState()

    Surface(color = MaterialTheme.colors.background) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Crossfade(targetState = timerSec.value) { sec ->
                Crossfade(targetState = timerMin.value) { min ->
                    Text(
                        text = "${min / 10}${min - min / 10 * 10}m${sec / 10}${sec - sec / 10 * 10}s",
                        fontSize = 64.sp
                    )
                }
            }
            NumKeyPad(
                onClick = {
                    viewModel.setTimerInSec(it)
                }
            )
            Row {
                Button(
                    onClick = { viewModel.resetTimer() },
                    enabled = timerState.value != TimerState.PREPARE,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(text = "reset/stop")
                }

                Button(
                    onClick = { viewModel.countDownTimer() },
                    enabled = timerState.value == TimerState.READY,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(text = "start")
                }
            }
        }
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        CountdownTimer(MainViewModel())
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        CountdownTimer(MainViewModel())
    }
}
