# Screen Stabilization for Android

Here is a solution for Android screen stabilization. It consists of Android core application and two AOSP patches that adds API for screen shift into Android internals.

Android application contains a service that handles device movements through accelerometer sensor and controls screen shift using mentioned API.

To try this out you will need to setup [AOSP](https://source.android.com/source/index.html) development environment and integrate this project there.

Android [application](https://github.com/ryanchyshyn/aosp_screen_stabilization/tree/master/ScreenStabilization) should be placed into **{aosp}/packages/apps** directory and AOSP patches [0001-ScreenStabilization-application-added.patch](https://github.com/ryanchyshyn/aosp_screen_stabilization/blob/master/aosp_patches/0001-ScreenStabilization-application-added.patch) should be applied to **{aosp}/build** directory, [0001-Translate-methods-added.patch](https://github.com/ryanchyshyn/aosp_screen_stabilization/blob/master/aosp_patches/0001-Translate-methods-added.patch) should be applied to **{aosp}/frameworks/native** directory.

After building and flashing ROM you can adjust parameters and enable screen stabilization for whole device using "Screen Stabilization" application:
![screen-shot](https://github.com/ryanchyshyn/aosp_screen_stabilization/blob/master/screenshot1.png)
