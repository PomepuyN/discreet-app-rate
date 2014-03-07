Discreet App Rate
============

A lightweight non intrusive app rate reminder for Android

![Screenshot][1]

## Download

```
repositories {
    mavenCentral()
}

dependencies {
    compile 'fr.nicolaspomepuy:discreetapprate:1.0.+@aar'
}
```

## Usage

```
AppRate.with(MyActivity.this).checkAndShow();
```

## Settings

```
AppRate.with(this).text(R.string.app_rate); // Change the displayed text
```

```
AppRate.with(this).initialLaunchCount(3); // How many times has it to be called before being displayed
```

```
AppRate.with(this).retryPolicy(RetryPolicy.INCREMENTAL); // Retry policy
```

```
AppRate.with(this).delay(1000); // Delay before showing the view
```

```
AppRate.with(this).listener(new AppRate.OnShowListener() {
            @Override
            public void onRateAppShowing() {
                // View is shown
            }

            @Override
            public void onRateAppDismissed() {
                // User has dismissed it
            }

            @Override
            public void onRateAppClicked() {
                // User has clicked the rate part
            }
        });
```

## Actions

```
AppRate.with(this).checkAndShow(); // Check if showing is needed and display the view
```

```
AppRate.with(this).reset(); // Reset the count launch
```

```
AppRate.with(this).forceShow(); // Force displaying the view
```

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

[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/PomepuyN/discreet-app-rater/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

[1]: http://nicolaspomepuy.fr/wp-content/uploads/2014/03/screenshot.png