package com.jintin.ai_droid

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class EditorTransformation(private val suggestion: String) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        if (!suggestion.startsWith(text)) return TransformedText(text, OffsetMapping.Identity)

        val builder = AnnotatedString.Builder()
        builder.append(suggestion)
        builder.addStyle(style = SpanStyle(color = Color.Black), 0, text.length)
        builder.addStyle(style = SpanStyle(color = Color.Gray), text.length, suggestion.length)

        return TransformedText(builder.toAnnotatedString(), OffsetMapping.Identity)
    }

}