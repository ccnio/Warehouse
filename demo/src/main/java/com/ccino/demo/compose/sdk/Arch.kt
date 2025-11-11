package com.ccino.demo.compose.sdk

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ccino.demo.compose.ApiCaseVM
import com.ccino.demo.compose.GithubUserUiState
import com.ccino.demo.compose.data.GithubUser


/**
 * flow.collectAsState、flow.collectAsStateWithLifecycle 区别
 * collectAsState() 不感知生命周期（只感知 Compose 生命周期），
 * collectAsStateWithLifecycle() 支持 Android 生命周期（如 onStart / onStop），不会在后台继续收集 Flow。

 * val state by viewModel.stateFlow.collectAsState()
 * 当界面进入后台（按 Home 键/跳转其他 Activity）时，仍然收集 Flow 数据。
 *
 * val state by viewModel.stateFlow.collectAsStateWithLifecycle()
 * 在生命周期 STARTED 或 RESUMED 时收集, 页面不可见 → 自动停止收集
 * 回到界面 → 自动恢复，且不会造成重复收集
 */
@Composable
fun GithubUserScreen(modifier: Modifier = Modifier, vm: ApiCaseVM = viewModel()) {
    // State for the text field
    var username by remember { mutableStateOf("JakeWharton") }

    // Observe the UI state from the ViewModel in a lifecycle-aware manner
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    // Use LaunchedEffect to fetch data when the composable is first launched.
    // The key `Unit` ensures this runs only once when the screen is first displayed.
    Log.d("GithubUserScreen", "start")
    LaunchedEffect(Unit) {
        Log.d("GithubUserScreen", "fetch")
        vm.fetchGithubUser(username)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("GitHub Username") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Button(onClick = { vm.fetchGithubUser(username) }) {
                Text("Search")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display UI based on the current state
        when (val state = uiState) {
            is GithubUserUiState.Idle -> {
                Text("Enter a username and click search.")
            }

            is GithubUserUiState.Loading -> {
                CircularProgressIndicator()
            }

            is GithubUserUiState.Success -> {
                UserInfoCard(user = state.user)
            }

            is GithubUserUiState.Error -> {
                Text(text = state.message, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun UserInfoCard(user: GithubUser, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = "Name: ${user.name}", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Login: ${user.login}", style = MaterialTheme.typography.bodyLarge)
    }
}
