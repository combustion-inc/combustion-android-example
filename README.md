# Combustion Inc. Android

This is the repository for Comsbustion's Android apps.

## Repo Structure

| Directory | Description |
| -- | -- |
| `engineering` | Engineering Diagnostic Android application |

## Environment

| Tool | Description |
| -- | -- |
| Android Studio | Projects originally created using Android Studio Artic Fox 2021.3.1 Patch 3 |
| SDK | Projects originally written against Android SDK 12.0 |

Projects make use of Kotlin unsigned types (experimental feature).  The projects globaly opt-in with the following in `build.gradle`

```
    tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile).all {
        kotlinOptions {
            freeCompilerArgs += [
                    '-Xopt-in=kotlin.ExperimentalUnsignedTypes',
            ]
        }
    
```

## Conventions
| Topic | Description
| -- | -- |
| Version Control of `.idea` | Following this [guide](https://proandroiddev.com/deep-dive-into-idea-folder-in-android-studio-53f867cf7b70).| 

## Processes

| Process | Description |
| -- | -- |
| [Deployment and Release](DISTRIBUTION.md) | Tools and Process for Android App distribution |

## References

### Third Party 

| Project | Description |
| -- | -- |
| [Kable](https://github.com/JuulLabs/kable) | Kotlin Asynchronous Bluetooth Low Energy provides a simple Coroutines-powered API for interacting with Bluetooth Low Energy devices. |
| [EasyPermissions-ktx](https://github.com/VMadalin/easypermissions-ktx) | Kotlin version of Google's Easy Permissions Android library |
| [Sora](https://fonts.google.com/specimen/Sora) | Google Font used in Engineering App |
| [Google Icons](https://fonts.google.com/icons) | Material Design icons used in Engineering App |
| [Compose Settings](https://github.com/alorma/Compose-Settings) | Library of Settings like composables items |

### Guides

| Reference | Description |
| -- | -- |
| [Kotlin flows on Android](https://developer.android.com/kotlin/flow) | Coroutine that emits multiple values sequentially |
| [Coroutines guide](https://kotlinlang.org/docs/coroutines-guide.html) | Kotlin's guide to coroutines |
| [ViewModel Overview](https://developer.android.com/topic/libraries/architecture/viewmodel) | Android's ViewModel pattern. |
| [View Models and LiveData: Patterns + AntiPatterns](https://medium.com/androiddevelopers/viewmodels-and-livedata-patterns-antipatterns-21efaef74a54) | App architectural patterns |
| [Kotlin Data Classes](https://medium.com/android-news/kotlin-for-android-developers-data-class-c2ad51a32844) | Kotlin patterns for data classes |
| [Coroutines & Patterns ...](https://medium.com/androiddevelopers/coroutines-patterns-for-work-that-shouldnt-be-cancelled-e26c40f142ad) | Article on coroutine patterns on Android |
| [Kotlin coroutines with lifecycle-aware components](https://developer.android.com/topic/libraries/architecture/coroutines) | Android architecture article |
| [Sample: LiveData, ViewModels, Data Binding](https://github.com/android/architecture-components-samples/tree/main/LiveDataSample) | Sample on GitHub |
| [Compose Theme & Typography](https://alexzh.com/jetpack-compose-theme-and-typography/) | Guide on theme and typography best practices with Compose and Material design. |
| [Custom Font Family in Android](https://medium.com/geekculture/custom-font-family-in-android-jetpack-compose-d03efed193b) | Custom fonts in Jetpack Compose |
| [Material Design Type Scale](https://material.io/design/typography/the-type-system.html#type-scale) | Generates resource configuration for Material Design typography practices |
| [Material Design Icons](https://github.com:google/material-design-icons.git) | GitHub for Material Design icons.  Most (all?) already avail in Android Studio |
| [CameraX Integration w/ Compose](https://medium.com/@dpisoni/building-a-simple-photo-app-with-jetpack-compose-camerax-and-coroutines-part-2-camera-preview-cf1d795129f6) | Article on CameraX with Compose |
| [Firebase Authentication for Google Sign-In](https://medium.com/geekculture/how-to-integrate-firebase-authentication-for-google-sign-in-functionality-e955d7e549bf) | Guide on setting up Firebase and Google Authentication Sign-In. |
