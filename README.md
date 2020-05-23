# Android AppConfig #
https://github.com/PRosenb/AppConfig

The intention of this project is to reconfigure an app under test or development. This is useful, if the app under test needs to point to a staging server or has other properties specifically activated for testing purpose.

It consists of two parts:
- The <img src="https://github.com/PRosenb/AppConfigApp/blob/master/assets/playIcon.svg" width="20"> `App Config` app
- The library `AppConfig`

## Features
- Easy to use
- Set and delete `SharedPreference` values
- Option to limit keys that can be modified
- Access authorisation with whitelist
- Controller app available on Google Play
- Everything open source
- Example app

## Overview
<p align="center">
<img src="https://github.com/PRosenb/AppConfigApp/blob/master/assets/featureGraphic.svg" width="300">
</p>

The app `App Config` connects to the app under test using a `ContentProvider`. It allows to set `SharedPrefences` in the app under test that can then be evaluated accordingly and e.g. point it to Staging or Production.

## Initial setup

### Add ConfigApp library to your app
- Add the library to your apps dependencies
``` groovy
implementation 'ch.pete.appconfig:appconfig:0.1.0'
```
- Add the supplied `ContentProvider` to your `AndroidManifest.xml` file. Inside of `<application></application>` add:
``` xml
        <provider
            android:name="ch.pete.appconfig.AppConfigContentProvider"
            android:authorities="YOUR_AUTHORITY"
            android:exported="true"
            android:readPermission="NONE" />
```
- Replace `YOUR_AUTHORITY` with the apps authority, followed by `.config`, e.g. `com.example.config`

### Setup Controller app from Google Play
- Install [App Config](https://play.google.com/store/apps/details?id=ch.pete.appconfigapp) from Google Play
- Open it and create a new config entry
- Enter a name like `MyApp Staging`
- Enter the authority you choose above
- Click the play button to test if the connection works
- Add whatever key/values you need for this specific configuration
- On the main screen, left swipe on your configuration and choose `clone`
- Rename it to e.g. `MyApp Prod`
- Modify the key/values accordingly. Usually, you want to tick `null` so that the `SharedPreference` is removed.

## Configuration Options
The library can be configured by setting properties on `AppConfig`. All configuration options should
be set early after the app starts, so e.g. in `Application.onCreate()`.

The following options are available.

### AppConfig.authorizedApps
Whitelist of apps allowed to access the app.

``` kotlin
AppConfig.authorizedApps.add(
    AuthorizedApp(
        applicationId = "AUTHORIZED_APPLICATION_ID",
        signature =
        "SHA_256_HASH_SIGNATURE"
    )
)
```

Replace `AUTHORIZED_APPLICATION_ID` and `SHA_256_HASH_SIGNATURE` with the values of the authorized app.  
You can also run both apps and try to access it. Then the `AppConfig` library logs the required details 
so that you can copy/paste them.

``` commandline
2020-05-24 10:25:37.703 14607-14654/ch.pete.appconfig.exampleapp E/AppConfig: Access denied
    To allow add (Kotlin):
    AppConfig.authorizedApps.add(
      AuthorizedApp(
        applicationId = "ch.pete.appconfigapp",
        signature = "C7:0D:90:86:6D:3F:D2:3D:4C:7C:45:64:75:B0:93:97:00:5A:1F:FD:37:FE:8B:B1:61:8C:CF:EB:80:27:19:EC"
      )
    )
```
### AppConfig.appConfigListener
Register a callback whenever a call is made by the controller app.
``` kotlin
AppConfig.appConfigListener = {
    // called whenever a call is made
}
```

### AppConfig.storeValuesToSharedPreferences
Controls if the library automatically writes the received values to `SharedPreferences`. Default: `true`
``` kotlin
AppConfig.storeValuesToSharedPreferences = false
```

### AppConfig.authorizedKeys
Controls which keys are stored to shared preferences. See also`storeValuesToSharedPreferences`. An empty list allows all keys.
``` kotlin
AppConfig.authorizedKeys.addAll(
    listOf("key0", "key1")
)
```

## Technical details
There are multiple options how apps can communicate with each other but only by using a `ContentProvider`, the called app can verify the `applicationId` and the signing certificate of the calling app.
As we don't want any malicious app to manipulate our app in the background without the users knowledge, it's very important, that the called app can verify the caller and allow access to authorized apps only.
The library implements the authorisation logic and there is a whitelist of the `applicationId` with the related signing certificate. All other apps trying to call the `ContentProvider` will receive a `SecurityException`.

The signing key of the `App Config` app available on Google Play is safe because it was created and is managed and controlled solely by `App Signing by Google Play`. Even the owner of the app cannot get access to it at any stage.
For this reason, the signing certificate of the `App Config` app is preconfigured in the library. It can be removed from the whitelist easily though.

The `App Config` app currently only supports `String` values to be sent to apps under test. The library does support other data types already, so it should be possible to extend `App Config` accordingly without causing a breaking change.

## Contributions ##
Enhancements and improvements are welcome.

## License ##
``` text
Android AppConfig
Copyright (c) 2020 Peter Rosenberg (https://github.com/PRosenb).

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
