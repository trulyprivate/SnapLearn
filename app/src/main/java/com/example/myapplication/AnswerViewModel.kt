import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.SpannedString
import android.text.style.StyleSpan
import androidx.core.text.HtmlCompat
import androidx.core.text.toSpanned
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.AnswerUiState
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

class AnswerViewModel(
    private val generativeModel: GenerativeModel
) : ViewModel() {

    private val _uiState: MutableStateFlow<AnswerUiState> =
        MutableStateFlow(AnswerUiState.Initial)
    val uiState: StateFlow<AnswerUiState> =
        _uiState.asStateFlow()

    fun answerView(inputText: String) {
        if (inputText.isNotBlank()) { // Only proceed if manually typed text is present
            _uiState.value = AnswerUiState.Loading
        }

        val prompt = "Answer these questions of : $inputText in depth"

        viewModelScope.launch {
            try {
                val responseBuilder = StringBuilder()

                // Use generateContentStream for streaming partial results
                generativeModel.generateContentStream(prompt).collect { chunk ->
                    // Collect partial results and update UI
                    responseBuilder.append(chunk.text)
                    val partialResponse = responseBuilder.toString()
                    _uiState.value = AnswerUiState.Success(formatText(partialResponse))
                }

            } catch (e: Exception) {
                _uiState.value = AnswerUiState.Error(e.localizedMessage?.let { errorText ->
                    formatText(errorText)
                } ?: AnnotatedString("An error occurred"))
            }
        }
    }

    private fun formatText(inputText: String): AnnotatedString {
        return buildAnnotatedString {
            val headingRegex = Regex("\\*\\s+\\*\\*([^*]+)\\*\\*") // Matches heading (star, space, double star, text, double star)
            val boldRegex = Regex("\\s*\\*\\*([^*]+)\\*\\*\\s*") // Matches bold (optional spaces, double star, text, double star, optional spaces)
            val bulletPointRegex = Regex("^\\s*\\*\\s{0,2}(.*)$") // Matches bullet points (line starting with star and space or star and two spaces or start with tab space)
            val equationRegex = Regex("```([^`]+)```") // Matches equations (3 backticks, text, 3 backticks)

            // Append the first line as a heading
            val firstLine = inputText.substringBefore("\n", inputText)
            withStyle(style = SpanStyle(fontWeight = FontWeight.Black, fontSize = 16.sp)) {
                append(firstLine.trim()) // Trim any leading or trailing spaces
            }
            append("\n") // Add a line break after the first line

            // Loop through each match and apply formatting
            var startIndex = firstLine.length + 1 // Start from the next line after the heading
            var lastIndex = 0 // Keep track of the last index to handle line breaks
            var lastMatchEndIndex = 0 // Keep track of the end index of the last match

            while (startIndex < inputText.length) {
                val headingMatch = headingRegex.find(inputText, startIndex)
                val boldMatch = boldRegex.find(inputText, startIndex)
                val bulletPointMatch = bulletPointRegex.find(inputText, startIndex)
                val equationMatch = equationRegex.find(inputText, startIndex)

                // Find the end index of the current line
                val endOfLineIndex = inputText.indexOf('\n', startIndex)
                val endIndex = if (endOfLineIndex != -1) endOfLineIndex else inputText.length

                // Append the text with the appropriate styling
                if (boldMatch != null && boldMatch.range.first == startIndex) {
                    if (boldMatch.range.first > lastMatchEndIndex) {
                        append("\n") // Add a line break if there's a gap between previous match
                    }
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(boldMatch.groupValues[1]) // Get the text within bold
                    }
                    lastIndex = boldMatch.range.last + 1
                } else if (headingMatch != null && headingMatch.range.first == startIndex) {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Black, fontSize = 16.sp)) {
                        append("\n") // Add a line break before the heading
                        append(headingMatch.groupValues[1]) // Get the text within heading
                    }
                    lastIndex = headingMatch.range.last + 1
                } else if (bulletPointMatch != null && bulletPointMatch.range.first == startIndex) {
                    withStyle(style = SpanStyle()) {
                        append("\u2022 ") // Unicode character for bullet point
                        append(bulletPointMatch.groupValues[1]) // Get the text within bullet point
                    }
                    lastIndex = bulletPointMatch.range.last + 1
                } else if (equationMatch != null && equationMatch.range.first == startIndex) {
                    withStyle(style = SpanStyle(fontFamily = FontFamily.Monospace)) {
                        append(equationMatch.groupValues[1]) // Get the text within equation
                    }
                    lastIndex = equationMatch.range.last + 1
                } else {
                    append(inputText.substring(startIndex, endIndex)) // Append the current line
                    lastIndex = endIndex
                }

                // Append a line break if necessary
                if (endOfLineIndex != -1 && endOfLineIndex < inputText.length) {
                    append("\n")
                    lastIndex++ // Move past the line break
                }

                // Store the end index of the current match
                lastMatchEndIndex = lastIndex

                // Move to the next line
                startIndex = lastIndex
            }
        }
    }

}
