package com.comedy.suggester

class Config {
    companion object {
        // If true, then the UI will contain more diagnostics information
        const val IS_DEBUG = true

        // TODO: Add a model selector
        // LLM model used to generate the responses
        // See https://platform.openai.com/docs/models
        const val OPEN_AI_MODEL = "gpt-4-turbo"
        //"gpt-4-turbo"
        //"gpt-3.5-turbo"

        // https://docs.anthropic.com/en/docs/about-claude/models
        const val CLAUDE_MODEL = "claude-3-5-sonnet-20241022"
    }
}