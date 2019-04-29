Discreet App Rate
============

[![Build Status](https://travis-ci.org/diniska/discreet-app-rate.svg?branch=master)](https://travis-ci.org/diniska/discreet-app-rate) [![Release](https://jitpack.io/v/diniska/discreet-app-rate.svg)](https://jitpack.io/#User/Repo)

A lightweight non intrusive app rate reminder for Android

![Screenshot][1]

## Download

The project uses [JitPack](https://jitpack.io/#diniska/discreet-app-rate) to distribute the code. It is fairly easy to install.

### Step 1
In project build.gradle file add

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' } // Add this line to the list of the repositories
    }
}
```

### Step 2

Add dependency to your app in build.gradle file of the app module
(Replace `VERSION ` in the code below to the actual version of the library you want to use):

```
dependencies {
    ...
    implementation 'com.github.diniska:discreet-app-rate:<VERSION>@aar'
}
```

## Usage

Minimal declaration

`AppRate.with(MyActivity.this).checkAndShow();`

### Composition:

```
AppRate // the class
    .with(MyActivity.this) // The linked activity. Mandatory
    .[Settings] // see the settings section below
    .[Action] // action to be performed. Cannot be chained. See the action section below
```

## Settings

[Full list of settings](docs/Settings.md)

## Actions

[Full list of actions](docs/Actions.md)

## Changelog

### V2.1
* Positioning on screen has become more flexible by supporting `margin` paramter

### V2.0.1 - V2.0.2

* Bug fixes

### V2.0

* Support for custom views

* Deprecation of methods using time to use milliseconds
* Detects if Play Store is installed
* Better Translucent activity support

### V1.0.4

* Avoid showing anything if the Play Store is not installed
* Avoid showing anything when there is no connection (thanks to @kozaxinan)
* Translucent theme support


## License

```
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


[1]: http://nicolaspomepuy.fr/wp-content/uploads/2014/03/screenshot.png
