package com.comedy.suggester.generator

/**
 * Utilities for dealing with LLM prompts and responses
 */
// Trim lines for each line in the string.
internal fun trimLines(input: String): String {
    return input.lines().joinToString("\n") { it.trim() }
}

val SUGGESTION_PREFIX = "-"

/**
 * We assume the llm response to be a repetition of the following segments:
 * Some kind of text \n
 * - suggestion 1 \n
 * - suggestion 2 \n
 * ending text
 *
 * And, we just want the final segment.
 * So, we look for the last hyphenated list item, and just keep going up until we hit a non-list
 * item
 */
internal fun parseLlmResponse(llmResponse: String): List<String> {
    val tokens = llmResponse.split("\n")
    val result: MutableList<String> = mutableListOf()
    // Go in reverse order
    for (tok in tokens.reversed()) {
        var curTok = tok.trim()
        if (curTok.isEmpty()) continue
        if (!curTok.startsWith(SUGGESTION_PREFIX)) {
            if (result.isNotEmpty()) {
                // The end of a hyphenated segment.
                break
            }
            continue
        }
        curTok = curTok.removePrefix(SUGGESTION_PREFIX).trim()
        result.add(curTok)
    }

    if (result.isEmpty()) {
        // Sometimes the LLM just gives a one-element suggestion.
        result.add(llmResponse.trim())
    }
    return result.reversed()
}