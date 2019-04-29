RetryPolicy describes the way the view will be shown again after the first time

* Will retry each time initial count has been triggered
Ex: if initial is set to 3, it will be shown on the 3rd, 6th, 9th, ... times

```
INCREMENTAL
```

* Will retry exponentially to be less intrusive
Ex: if initial is set to 3, it will be shown on the 3rd, 6th, 12th, ... times

```
EXPONENTIAL
```

* Will never retry

```
NONE
``` 