ChromecastPresentation
================================
Example app for [Google Cast](https://developers.google.com/cast/) on Android and a associated [Chromecast](http://chromecast.com) receiver device. The Android app is located in the _ChromecastPresentation_ folder and the receiver (HTML/JavaScript sources) in the _receiver_ folder. The app was developed as an example for my employer [inovex](http://www.inovex.de/) to show basic features of Google Cast.

Whitelisting your Chromecast
-------------------------
The Google Cast API is a preview for development and testing. For detailed explanation about whitelisting your Chromecast device read the [Developer Setup](https://developers.google.com/cast/whitelisting). After whitelisting replace the APP_ID placeholder in the [MainActivity class](https://github.com/dbaelz/ChromecastPresentation/blob/master/ChromecastPresentation/src/main/java/de/inovex/chromecast/presentation/MainActivity.java#L30) and the [index.html](https://github.com/dbaelz/ChromecastPresentation/blob/master/receiver/index.html#L12).

Build the Example
------------------
ChromecastPresentation uses Gradle as build tool with the integrated Gradle Wrapper. Use the _build_ task to build the Android app. For more information on Gradle and Android see the [Gradle Plugin User Guide](http://tools.android.com/tech-docs/new-build-system/user-guide).

