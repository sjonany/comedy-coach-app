package com.comedy.suggester

class Config {
    companion object {
        // If true, then the UI will contain more diagnostics information
        const val IS_DEBUG = true

        // LLM model used to generate the responses
        // See https://platform.openai.com/docs/models
        const val LLM_MODEL = "gpt-3.5-turbo"
        //"gpt-4-turbo"
        //"gpt-3.5-turbo"
    }
}