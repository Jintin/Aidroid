package com.jintin.ai_droid

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@OptIn(FlowPreview::class)
class SourceViewModel : ViewModel() {

    private val _codeFlow: MutableStateFlow<Code> = MutableStateFlow(Code("", ""))
    val codeFlow: StateFlow<Code> = _codeFlow.asStateFlow()

    private val _loadingFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loadingFlow: StateFlow<Boolean> = _loadingFlow.asStateFlow()

    private var job: Job? = null

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.apiKey
    )

    init {
        viewModelScope.launch {
            codeFlow.filter { it.source != it.suggestion }
                .map { it.source }
                .distinctUntilChanged()
                .debounce(1.seconds)
                .collect {
                    sendPrompt(prompt = it)
                }
        }
    }

    fun updateCode(source: String, suggestion: String) {
        _codeFlow.value = Code(source = source, suggestion = suggestion)
        job?.cancel()
        _loadingFlow.value = false
    }

    fun sendPrompt(prompt: String) {
        _loadingFlow.value = true
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(
                    content {
                        text("Imagine you're a good programmer.")
                        text("I'll give you an uncompleted code and please help me to complete it from your knowledge.")
                        text("Please give me the completed version code so I can replace mine with yours directly.")
                        text("Don't ask clarify question or explain your thought. Don't modify my existing code. Don't insert any markdown format contains ``` code block.")
                        text("Here is the code:")
                        text(prompt)
                    }
                )
                Log.e("jintin", "response " + response.text)
                response.text
                    ?.takeIf { it.startsWith(codeFlow.value.source) }
                    ?.let {
                        _codeFlow.value = codeFlow.value.copy(suggestion = it)
                    }
            } catch (e: Exception) {
                Log.e("jintin", e.toString())
            }
            _loadingFlow.value = false
        }
    }
}