package com.comedy.suggester.generator

import com.comedy.suggester.chatparser.ChatMessages
import com.comedy.suggester.data.CharacterProfile

/**
 * Prompt strings are stored here.
 */
class PromptStrings {
    companion object {
        fun suggestionGenerationPrompt(
            chatMessages: ChatMessages,
            userHint: String,
            characterProfilesById: Map<String, CharacterProfile>
        ): String {
            val chatHistoryStr =
                chatMessages.getMessages().joinToString(separator = "\n") { message ->
                    "${message.sender}: ${message.message}"
                }

            val userHintStr = if (userHint.trim().isEmpty()) "Not provided" else userHint
            val senseOfHumorStr =
                characterProfilesById.values.joinToString("\n") { "<${it.id}>\n${it.senseOfHumor}\n</${it.id}>" }

            return trimLines(
                """
You are an AI assistant specialized in generating humorous responses for a chat application. Your task is to create 5 funny replies that fit the context of an ongoing conversation, align with the characters' senses of humor, and incorporate a user-provided hint for the joke angle.

First, review the chat history:

<chat_history>
${chatHistoryStr}
</chat_history>

Take into account all these character's senses of humor:
<sense_of_humor>
${senseOfHumorStr}
</sense_of_humor>

The user has provided a hint for the desired joke angle or theme:

<user_hint>
${userHintStr}
</user_hint>

Your task is to generate 5 distinct funny responses that meet the following criteria:
1. Fit naturally within the context of the chat history
2. Match the character's sense of humor
3. Incorporate elements from the user's hint

Before generating the responses, wrap your thought process in <brainstorming> tags:

<brainstorming>
1. Analyze the chat history and identify key themes or topics to reference.
2. Interpret the user's hint and brainstorm ways to incorporate it into the responses.
3. Plan different types of humor to use (e.g., wordplay, situational humor), taking into account my sense of humor
4. For each of the 5 responses:
 a. Choose a specific type of humor to focus on.
 b. Select elements from the chat history and user hint to incorporate.
 c. Craft a response that combines these elements in a humorous way.
 d. Evaluate how well the response meets the four criteria listed above.
5. Review all 5 responses to ensure they are distinct and cover a range of humor styles.
</brainstorming>

Now, generate 5 funny response suggestions. Present them as bullet points with hyphens, each on a new line. Do not include any explanations or additional commentary outside of the responses. Do not include the person's name in the responses. Pretend that you are the person typing out the reply.

Example format (replace with your actual funny responses):
- [Funny response 1]
- [Funny response 2]
- [Funny response 3]
- [Funny response 4]
- [Funny response 5]

Remember to make each response distinct, creative, and tailored to the specific requirements outlined above.
    """
            )
        }
    }
}

