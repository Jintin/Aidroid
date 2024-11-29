package com.jintin.ai_droid

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun BakingScreen(
    sourceViewModel: SourceViewModel = viewModel()
) {
    val code by sourceViewModel.codeFlow.collectAsState()
    val source = code.source
    val suggestion = code.suggestion
    val loading by sourceViewModel.loadingFlow.collectAsState()
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.baking_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )

        Row(modifier = Modifier.padding(all = 8.dp)) {
            Button(
                onClick = { sourceViewModel.updateCode(suggestion, suggestion) },
                enabled = suggestion.isNotEmpty() && suggestion != source && !loading,
                modifier = Modifier.padding(all = 8.dp)
            ) {
                Text(text = stringResource(R.string.action_apply))
            }
            Button(
                onClick = { sourceViewModel.updateCode(source, source) },
                enabled = suggestion.isNotEmpty() && suggestion != source && !loading,
                modifier = Modifier.padding(all = 8.dp)
            ) {
                Text(text = stringResource(R.string.action_ignore))
            }
            Button(
                onClick = { sourceViewModel.updateCode("", "") },
                enabled = source.isNotEmpty() || suggestion.isNotEmpty(),
                modifier = Modifier.padding(all = 8.dp)
            ) {
                Text(text = stringResource(R.string.action_clear))
            }
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.padding(all = 8.dp))
            }
        }

        TextField(
            value = source,
            label = { Text(stringResource(R.string.label_code)) },
            onValueChange = {
                sourceViewModel.updateCode(it, suggestion)
            },
            visualTransformation = EditorTransformation(suggestion),
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 16.dp)
                .align(Alignment.CenterHorizontally),
        )
    }
}