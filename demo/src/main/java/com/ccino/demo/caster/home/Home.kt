package com.ccino.demo.caster.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MainScreen(viewModel: HomeViewModel = viewModel()) {
    Scaffold { paddingValues ->
        Text("Hello World ${viewModel.currentDate}", modifier = Modifier.padding(paddingValues))

    }
}