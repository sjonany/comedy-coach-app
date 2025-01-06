# Funny response suggestion app

This android app auto-suggests funny responses based on chat context.
The app name isn't funny because comedy is serious business.

Here's a video of it in action:

https://github.com/user-attachments/assets/81a52e64-939f-4ab3-80dd-5ea1cc99329b

See this medium post (TODO:) for more interesting examples

## How to use
- Install the APK by cloning this repo and running the project on android studio with your phone connected through USB. See [this guide](https://developer.android.com/studio/run/device).
- Enable the app on accessibility settings. Accessibility > Downloadeded apps > Comedy Coach Suggester.
- Generate an [OpenAI API key](https://platform.openai.com/settings/organization/api-keys) -- Somebody has to pay for the LLM bills and it's not me.
- Open the controller app on the phone "comedy coach suggester" and copy paste the API key.
- On discord / whatsapp, to activate the app, type "Qw", then a pop up will show.
- You can optionally type something in the textbox to give more hints on the kind of response you want to generate .
- When you click "Generate suggestions", you will get a list of suggestions that you can click on.

## How it works
- **Overview.** We scrape the screen-visible text, which includes the chat context and the user hint in the text editor, then call Open AI's gpt-4-turbo.
- **Chat context**. We use android's accessibility service to get a DOM-tree-like object of what's visible on screen (we do not have access to non-visible chat messages).
  Right now we only support whatsapp and discord 1-1 chats. See these parser codes:
  [Discord](https://github.com/sjonany/comedy-coach-app/blob/deac8b4be8ca801385c388a85a73563698576844/comedycoachsuggester/src/main/java/com/comedy/suggester/chatparser/DiscordChatParser.kt),
  [Whatsapp](https://github.com/sjonany/comedy-coach-app/blob/deac8b4be8ca801385c388a85a73563698576844/comedycoachsuggester/src/main/java/com/comedy/suggester/chatparser/WhatsAppChatParser.kt).
- **Suggestion generation**. We call openai gpt-4-turbo with the following [prompt](https://github.com/sjonany/comedy-coach-app/blob/deac8b4be8ca801385c388a85a73563698576844/comedycoachsuggester/src/main/java/com/comedy/suggester/generator/OpenAiSuggestionGenerator.kt#L90).

## Limitations
- 1-1 chats only, on whatsapp and discord.
- Crappy code because this is just a fun side project. Definitely don't refer to this repo for Android dev best practices.

## QnA
- **Privacy.** This app looks scary, what information is it harvesting?
 **A:** Yeah I wouldn't have installed this app if it's written by someone else too, so use your own judgment (or if you want to trust me? Though you probably shouldn't trust strangers on the internet:)).
  Here's the [accessibility config](https://github.com/sjonany/comedy-coach-app/blob/deac8b4be8ca801385c388a85a73563698576844/comedycoachsuggester/src/main/res/xml/accessibility_service_config.xml).
  The app listens to text edits in discord + whatsapp only. It also does have internet connection permission so we can call OpenAI API. 
- **What's next?** See the medium post (TODO:). But TL;DR - not planning on publishing it to the app store. My focus is to make this app funnier **to me**.
