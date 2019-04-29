Discreet App Rate
============

[![Build Status](https://travis-ci.org/diniska/discreet-app-rate.svg?branch=master)](https://travis-ci.org/diniska/discreet-app-rate) [![Release](https://jitpack.io/v/diniska/discreet-app-rate.svg)]
(https://jitpack.io/#User/Repo)

A lightweight non intrusive app rate reminder for Android

![Screenshot][1]

## Download

```
repositories {
    mavenCentral()
}

dependencies {
    compile 'fr.nicolaspomepuy:discreetapprate:2.0.3@aar'
}
```

## Usage

```
AppRate.with(MyActivity.this).checkAndShow();
```

All the following settings and actions can be chained.

## API

Please visit the [Wiki section](https://github.com/PomepuyN/discreet-app-rate/wiki) to read the full API

## Sample

[Available in the Google Play Store](https://play.google.com/store/apps/details?id=com.npi.discreetapprate.sample)

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
