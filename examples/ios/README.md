# iOS Sample App

This app demonstrates streaming from the microphone using [AVAudioEngine](https://developer.apple.com/documentation/avfoundation/avaudioengine).
* Click the globe icon in the upper left corner to enter the URL of cubic server and whether it requires a secure connection (TLS).
* Click the settings icon in the upper right corner to select from the models available on the server.
* When the microphone button is held, it streams the audio to the server, and when the button is released, it prints the results to a text area on the screen. 

## Code structure
Open this entire directory in XCode to build the sample app.  It will load its dependencies automatically, including the Cubic Swift SDK.
CubicManager.swift and ViewController.swift encapsulate most of the logic for using the SDK, connecting to cubic and generating the transcripts.

### CubicManager.swift
Provides methods for calling AVAudioEngine and the Cubic API

### ViewController
Connects the various UI elements to the appropriate actions and specifies callbacks to the methods of CubicManager to handle the transcripts coming back from Cubic.

### SettingsViewController.swift
Lists the ASR Models available on the server so user can pick the  appropriate language if there are multiple.

