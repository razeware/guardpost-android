# GuardPost-Android

Welcome to GuardPost! We are excited you’ve taken the time to learn more about the framework we use for authentication on Kodeco mobile applications, and it can also be used in your own apps.

This repository contains a web view wrapper for the authentication process of the Kodeco Android app. The wrapper enables the app to seamlessly integrate a web-based authentication interface within the app itself.

## What's in this Repository?

- A sample Android application to test out the GuardPost authentication.
- A module used by the sample Android app that shows the implementation of the library.

## Features

- Display a web view within the Kodeco Android app for authentication purposes.
- Handle the communication between the app and the authentication system.
- Facilitate the login and authentication flow for Kodeco users.

## Usage

To utilize the web view wrapper in your Kodeco Android app, follow these guidelines:

1. Make sure the user has an active internet connection.
2. Launch the web view wrapper activity or fragment within your app's authentication flow.
3. Display the web view to the user, allowing them to enter their credentials.
4. Handle the response received from the authentication system, validating the user's credentials.
5. Once the authentication is successful, proceed with the appropriate actions within your Kodeco app.
6. Implement any additional logic or functionality required by your app's specific requirements.

## Setup

After cloning this project into Android Studio, you will need to perform the following steps before you can see it work successfully. Failure to do so will give you a Gradle error message referencing line 15 in the `app/build.gradle` file.

1. Create a new file named `gradle.properties` in the project-level folder.
2. Copy the contents of `gradle.properties.dist` to `gradle.properties`.

### Setup (google-services.json)

1. Open Google Firebase console.
2. Create a new project with an arbitrary name.
3. Add an Android app by following the instructions.
4. For the package name, put in `com.razeware.emitron`. You don't need the SHA-1 signing certificate.
5. Download the `google-services.json` file from the newly created app.
6. Add `google-services.json` to the `app` folder within the project.

The above steps are almost similar to the Emitron Android app setup because it uses the same library for authentication.

## Contributions

Contributions are welcome! If you have any ideas, suggestions, or improvements for the web view wrapper, please feel free to open an issue or submit a pull request. Your contribution will be greatly appreciated.
