package com.comedy.suggester.data

// LLM models this app supports
enum class LlmModel {
    // Open AI models
    // https://platform.openai.com/docs/models
    GPT_4_TURBO,
    GPT_4O_MINI,

    // Anthropic models
    // https://docs.anthropic.com/en/docs/about-claude/models
    CLAUDE_3_5_SONNET,
    CLAUDE_3_5_HAIKU;

    companion object {
        val DEFAULT = CLAUDE_3_5_HAIKU
    }
}
