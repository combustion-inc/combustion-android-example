# Combustion Inc. Android Example

[![License](https://img.shields.io/github/license/combustion-inc/combustion-android-ble?color=red)](LICENSE)
[![Release](https://img.shields.io/github/v/release/combustion-inc/combustion-android-ble?color=red&include_prereleases)](https://github.com/combustion-inc/combustion-android-example/releases)
[![Twitter](https://img.shields.io/badge/Twitter-@inccombustion-blue.svg?style=flat)](https://twitter.com/intent/tweet?screen_name=inccombustion)

## About Combustion Inc.

We build nice things that make cooking more enjoyable. Like a thermometer that's wireless, oven-safe, and uses machine learning to do what no other thermometer can: predict your food’s cooking and resting times with uncanny accuracy. 

Our Predictive Thermometer's eight temperature sensors measure the temp outside and inside the food, in the center and at the surface, and nearly everywhere in between. So you know what’s really happening in and around your food. There's a display Timer that's big and bold—legible even through tears of joy and chopped onions—and a mobile app. 

Or you can create your own mobile app to work with the Predictive Thermometer using our open source libraries for [Android](https://github.com/combustion-inc/combustion-android-ble) and [iOS](https://github.com/combustion-inc/combustion-ios-ble).

Visit [www.combustion.inc](https://www.combustion.inc) to sign up to be notified when they're available to order in early 2022.

Head on over to our [FAQ](https://combustion.inc/faq.html) for more product details.

Ask us a quick question on [Twitter](https://twitter.com/intent/tweet?screen_name=inccombustion).

Email [hello@combustion.inc](mailto:hello@combustion.inc) for OEM partnership information.

## Overview
The project is an example Android app for the [combustion-android-ble](https://github.com/combustion-inc/combustion-android-ble) open source library.

The example uses [Jetpack Compose](https://developer.android.com/jetpack/compose) and follows Android's [Guide to App Architecture](https://developer.android.com/jetpack/guide#ui-layer) with example UI Layer code for interacting with the Combustion Library as the Data Layer.  

The example shows how to use the `Flow` instances produced by the library to handle and present probe data and state updates to your user.  If you are looking for more information on how to use our library or the API, head over to the [combustion-android-ble](https://github.com/combustion-inc/combustion-android-ble) repo.

## Example

### Initialization & Setup
The example uses a single [`Activity`](https://developer.android.com/guide/components/activities/intro-activities).  See the source comments in [`MainActivity`](app/src/main/java/inc/combustion/example/MainActivity.kt) for a walk-through of the following:
* How to initialize the library.
* How to request Bluetooth permissions from the user so that your app can access Android Bluetooth resources.  
* How to discover devices and be notified of global changes such as Bluetooth on/off and BLE scanning state changes.
* How to create simulated probes to support development without real hardware in the loop.
* How to configure debugging logging from the library.

### Device Discovery & State Updates
The example uses a [`ViewModel`](https://developer.android.com/topic/libraries/architecture/viewmodel) to adapt data and state changes produced by the library to the app's View.  See the source comments in [`DevicesViewModel`](app/src/main/java/inc/combustion/example/devices/DevicesViewModel.kt) for a walk-through of the following:
* How to subscribe to and handle device discovery events.
* How to connect to and disconnect from temperature probes.
* How to collect probe state and data updates in real-time from the library.
* How to start a transfer of the device's temperature log to the library.
* How to dependency inject the `DeviceManager` instance into a `ViewModel` for easier unit testing.

### Displaying Data & State 
Following the [State and Jetpack Compose](https://developer.android.com/jetpack/compose/state) guidelines, the example shows how to bind the View and probe's state changes using the observables updated by the [`DevicesViewModel`](app/src/main/java/inc/combustion/example/devices/DevicesViewModel.kt).  See the [`DevicesScreen`](app/src/main/java/inc/combustion/example/devices/DevicesScreen.kt), [`ProbeState`](app/src/main/java/inc/combustion/example/devices/ProbeState.kt) and [`DevicesScreenState`](app/src/main/java/inc/combustion/example/devices/DevicesScreenState.kt) classes for more details on:
* How to display the real-time temperature updates of a probe.
* How to monitor the upload progress of a record transfer from a probe.
* How to display a list of all discovered probes.
* How to display static properties of a probe, such as serial number and firmware version.
* How to display dynamic BLE properties of a probe, such as connection state and RSSI.

<p align="center">  
<img src="https://github.com/combustion-inc/combustion-android-example/blob/main/docs/screenshot.png" width=40% height=40%>
</p>

## Reporting Issues
Your feedback is important.  For reporting issues use the [issue tracker](https://github.com/combustion-inc/combustion-android-example/issues).  
