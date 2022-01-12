

# NoPermission
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-NoPermission-blue.svg?style=flat)](https://android-arsenal.com/details/1/6212)
[![androidweekly.cn](https://img.shields.io/badge/androidweekly.cn-%23153-red.svg)](http://androidweekly.cn/android-dev-weekly-issue-153/)
[ ![Download](https://api.bintray.com/packages/nonews/maven/nopermission/images/download.svg) ](https://bintray.com/nonews/maven/nopermission/_latestVersion)
[![API](https://img.shields.io/badge/API-15%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=null)

Deprecated. 

Simple Android library for permissions request. Consists of only one class.




### Why NoPermission:
* Not a framework. It's just one class
* Dialog with an explanation of why the application needs permission (with custom title, message and button text and color)
* Never ask again feature
* Automatic check whether the permission is granted or not (don't need to check api version)
* Fragments support


### Gradle

    compile 'ru.alexbykov:nopermission:1.1.2'

### Install

```java
PermissionHelper permissionHelper = new PermissionHelper(this); //don't use getActivity in fragment!

        permissionHelper.check(Manifest.permission.ACCESS_COARSE_LOCATION)
                .onSuccess(this::onSuccess)
                .onDenied(this::onDenied)
                .onNeverAskAgain(this::onNeverAskAgain)
                .run();
```

##### Multiply permissions and other settings:

```java

   permissionHelper.check(Manifest.permission.READ_CONTACTS, Manifest.permission.Manifest.permission.READ_CONTACTS)
                   .withDialogBeforeRun(R.string.dialog_before_run_title, R.string.dialog_before_run_message, R.string.dialog_positive_button)
                   .setDialogPositiveButtonColor(android.R.color.holo_orange_dark)
                   .onSuccess(this::onSuccess)
                   .onDenied(this::onDenied)
                   .onNeverAskAgain(this::onNeverAskAgain)
                   .run();
```

##### onRequestPermissionsResult:

```java
 @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
   }
```

##### Open application settings activity
If user check "Never ask again", you can redirect him to application settings.

```permissionHelper.startApplicationSettingsActivity();```

Don't forget explain it manually (with dialog). 
In future this feature will be realised in library. 



#### Changelog

Be sure to review the [changes list](https://github.com/NoNews/NoPermission/releases) before updating the version

#### TODO
* Unit tests
* Getting rid of the ```onRequestPermissionsResult``` method

#### Contributing

If you find any bug, or you have suggestions, don't be shy to create [issues](https://github.com/NoNews/NoPermission/issues) or make a [PRs](https://github.com/NoNews/NoPermission/pulls) in the `develop` branch.
You can read contribution guidelines [here](https://github.com/NoNews/NoPermission/blob/master/CONTRIBUTING.md)

#### License
```
Copyright 2017 Mike Antipiev and Alex Bykov

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
