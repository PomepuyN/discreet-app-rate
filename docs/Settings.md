List of library settings

### V1.0.20
:arrow_up: _2014-03-07_

* Text to be displayed in the view
```
    text(String text)
    text(int textResource)
```

* Initial times it has to be called before the view is shown
```
    initialLaunchCount(int initialLaunchCount)
```

* [RetryPolicy](https://github.com/PomepuyN/discreet-app-rate/wiki/RetryPolicy) to use to show the AppRate again
```
    retryPolicy(RetryPolicy policy)
```

* Listener used to get callback from the lifecycle
```
    listener(OnShowListener onShowListener)
```

* Delay the view's showing time
```
    delay(int delay)
```

### V1.0.3 
:arrow_up: _2014-03-08_

* Theme to use for the view (LIGHT or DARK)
```
    theme(AppRateTheme theme)
```

* Add a constraint to show the view only if the app is installed for more than a duration (in sec.)

:warning::warning::warning:**Deprecated** [See here](https://github.com/PomepuyN/discreet-app-rate/wiki/Settings#v20)
```
    atLeastInstalledSince(long installedSince)
```

* Enable debug mode which will send state when actions are triggered
```
    debug(boolean debug)
```

* Shows the view on top
```
    fromTop(boolean fromTop)
```

### V1.0.4
:arrow_up: _2014-03-15_

* Pause duration after a crash (in sec.). :warning: Calling action [initExceptionHandler()](https://github.com/PomepuyN/discreet-app-rate/wiki/Actions#v104) is mandatory to make it work.

:warning::warning::warning:**Deprecated** [See here](https://github.com/PomepuyN/discreet-app-rate/wiki/Settings#v20)
```
    pauseTimeAfterCrash(long pauseAfterCrash)
```

* Set the minimum monitoring time needed before showing the view. See [startMonitoring](https://github.com/PomepuyN/discreet-app-rate/wiki/Actions#v104) and [endMonitoring](https://github.com/PomepuyN/discreet-app-rate/wiki/Actions#v104) to understand how it works.

:warning::warning::warning:**Deprecated** [See here](https://github.com/PomepuyN/discreet-app-rate/wiki/Settings#v20)
```
    minimumMonitoringTime(long minimumMonitoringTime)
```

* Set the minimum interval between two launches to increment the count

:warning::warning::warning:**Deprecated** [See here](https://github.com/PomepuyN/discreet-app-rate/wiki/Settings#v20)
```
    minimumInterval(long minimumInterval)
```

### V2.0

**Some methods have been deprecated to use milliseconds instead of seconds:**

* Pause duration after a crash (in ms.). :warning: Calling action [initExceptionHandler()](https://github.com/PomepuyN/discreet-app-rate/wiki/Actions#v104) is mandatory to make it work.
```
    pauseAfterCrash(long pauseAfterCrash)
```

* Set the minimum monitoring time needed before showing the view. See [startMonitoring](https://github.com/PomepuyN/discreet-app-rate/wiki/Actions#v104) and [endMonitoring](https://github.com/PomepuyN/discreet-app-rate/wiki/Actions#v104) to understand how it works.
```
    minMonitoringTime(long minimumMonitoringTime)
```

* Set the minimum interval between two launches to increment the count
```
    minInterval(long minimumInterval)
```

* Add a constraint to show the view only if the app is installed for more than a duration (in ms.)
```
    installedSince(long installedSince)
```

**New feature**

* Set the view to display
``` 
public AppRate view(int view)
```
The view will be inflated and respects two view ids, `dar_close` and `dar_rate_element`. The text of the `dar_rate_element` will be set with `AppRate.setText()`. An example XML layout file might look like

```xml
<LinearLayout
    style="?android:attr/buttonBarStyle"
    android:layout_width="match_parent"
    android:layout_height="48dp"
    android:orientation="horizontal">

    <Button
        android:id="@+id/app_rate_never"
        style="?android:attr/buttonBarButtonStyle"
        android:layout_width="0dp"
        android:layout_height="match_parent"
        android:layout_weight="1"
        android:text="Never" />

    <!-- The id has been set to dar_close to get the lib behavior -->
    <Button
        android:id="@+id/dar_close"
        style="?android:attr/buttonBarButtonStyle"
        android:layout_width="0dp"
        android:layout_height="match_parent"
        android:layout_weight="1"
        android:text="Later" />

    <!-- The id has been set to dar_rate_element to get the lib behavior. Text is managed by AppRate.setText() -->
    <Button
        android:id="@+id/dar_rate_element"
        style="?android:attr/buttonBarButtonStyle"
        android:layout_width="0dp"
        android:layout_height="match_parent"
        android:layout_weight="1" />
</LinearLayout>
```