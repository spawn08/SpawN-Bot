## SpawNAI Chatbot
**This is a chatbot application build using the Wit AI as a backend. The entry point to this application is SpawnBotActivity.**

>* **This uses retrofit for network calls.**
>* **Android Speech to Text and Text to Speech library is used for conversation.**
>* **Lottie Animation Library for Animation like Splash Screen Animation, Loading animation**

>[Splash Screen UI](https://github.com/spawn08/SpawN-Bot/blob/master/app/src/main/res/drawable/spawn_splash_screen_ui.png)

>[Spawn Bot Activity UI](https://github.com/spawn08/SpawN-Bot/blob/master/app/src/main/res/drawable/spawn_bot_activity.png)

## 1. SpawnBotActivity - 
>*_This is Main activity for this application. 
It initializes the Android Speech components along with recyclerview for chat conversation.
Please specify your Bearer token obtained from Wit AI inside this activity in method **callWitService**_*

## 2. Networking - 
>*_Retrofit 2.4.0 is used for making network calls to Wit AI service. An interceptor is written for authorization and is passed in subsequent retrofit calls. 
You can find all the code in **com.spawnai.ai.network** package inside application._*

## Updates Coming soon - 
**The application will be updated to use the ViewModels, Lifecycle components of Android Jetpack.**

## Links

>* [Wit AI](https://wit.ai)
>* [Retrofit Client](https://square.github.io/retrofit/)
>* [Android Speech](https://developer.android.com/reference/android/speech/SpeechRecognizer)
>* [Lottie Animation](https://airbnb.io/lottie/#/)

## Contributors to this project are most welcome. _Happy Coding_

