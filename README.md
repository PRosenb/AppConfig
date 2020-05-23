# Android AppConfig #
https://github.com/PRosenb/AppConfig

The app `App Config` is intended to reconfigure an app under test or development. This is useful if the app under test needs to pointed to a staging server or has other properties specifically activated for testing purpose.
`App Config` consists of two parts.
- The app [<img src="https://github.com/PRosenb/AppConfigApp/blob/master/assets/playIcon.svg" width="20"> App Config](https://github.com/PRosenb/AppConfigApp) available at [Google Play](https://play.google.com/store/apps/details?id=ch.pete.appconfigapp)
- The library [AppConfig](https://github.com/PRosenb/AppConfig) available at [Maven Central](https://search.maven.org/search?q=g:ch.pete.appconfig)

## Overview
<p align="center">
<img src="https://github.com/PRosenb/AppConfigApp/blob/master/assets/featureGraphic.svg" width="300">
</p>

The app `App Config` connects to the app under test using a `ContentProvider`. It allows to set `SharedPrefences` in the app under test that can then be evaluated accordingly and e.g. point it to Staging or Production.

## Configuration

### Add ConfigApp library to your app

- Add the library to your app's dependencies
```
implementation 'ch.pete.appconfig:appconfig:0.1.0'
```
- Add it to your `AndroidManifest.xml`. Inside of `<application></application>` add
```
        <provider
            android:name="ch.pete.appconfig.AppConfigContentProvider"
            android:authorities="YOUR_AUTHORITY"
            android:exported="true"
            android:readPermission="NONE" />
```
- Replace `YOUR_AUTHORITY` with the app's authority, followed by `.config`, so e.g. `com.example.config`

### Setup `App Config` from Google Play
- Install [App Config](https://play.google.com/store/apps/details?id=ch.pete.appconfigapp) from Google Play
- Open it and create a new config entry
- Enter a name like `MyApp Staging`
- Enter the authority you choose above
- Click the play button to test if the connection works
- Add what ever key/values you need for this specific configuration
- On the main screen, left swipe on your configuration and choose `clone`
- Rename it to e.g. `MyApp Prod`
- Modify the key/values accordinly, usually you want to tick `null` so that they are set back to default
