package com.comedy.suggester.data

// LLM models this app supports
enum class LlmModel {
    // Open AI models
    GPT_4_TURBO,
    GPT_3_5_TURBO,

    // Anthropic models
    CLAUDE_3_5_SONNET,
    CLAUDE_3_5_HAIKU;

    companion object {
        val DEFAULT = GPT_3_5_TURBO
    }
}
