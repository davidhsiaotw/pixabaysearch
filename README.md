Pixabay Search
==================
**Pixabay Search** is a mini Android application that searches for images using the [Pixabay API](https://pixabay.com/api/docs/).

# Features
**Pixabay Search** searches for images based on the keyword. It uses Google ML Kit to determine the language of the keyword, but the Pixabay API only supports twenty-six languages. Additionally, it provides two layout options: grid and list. The default layout is set via Firebase Remote Config. It also has the functionality to save search history.

## Screenshot
<img src="https://github.com/davidhsiaotw/pixabaysearch/assets/71328511/d598b858-bf1f-41d7-8a84-deb9161853c9" height="500" />
<img src="https://github.com/davidhsiaotw/pixabaysearch/assets/71328511/74baebd3-f4df-4baf-933c-4b4a780bb3b2" height="500" />
<img src="https://github.com/davidhsiaotw/pixabaysearch/assets/71328511/763ec924-e98e-4196-8e20-59dcda7023e1" height="500" />

[demo video link](https://clipchamp.com/watch/6rW27si6uVK)

# Development Environment
**Pixabay Search** uses the Gradle build system and can be imported directly into Android Studio. It requires at least the Electric Eel version of [Android Studio](https://developer.android.com/build/releases/gradle-plugin#android_gradle_plugin_and_android_studio_compatibility). To run the app, you have to sign up for a [Pixabay account](https://pixabay.com/accounts/login/?next=/api/docs/) to get API key, then add ```API_KEY=YOUR_API_KEY``` to your local.properties and use your API key to replace ```YOUR_API_KEY```.
