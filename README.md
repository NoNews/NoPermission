[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-NoPermission-blue.svg?style=flat)](https://android-arsenal.com/details/1/6212)

# NoPermission
Android library for permissions request


### Gradle

    compile 'ru.alexbykov:nopermission:1.0.7'


### Install

```java
PermissionHelper permissionHelper = new PermissionHelper(context);

permissionHelper.check(Manifest.permission.READ_CONTACTS)
                .onSuccess(this::onSuccess)
                .onFailure(this::onFailure)
                .onNewerAskAgain(this::onNewerAskAgain)
                .run();
```

Multiply permissions:

```java

   permissionHelper.check(Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE)
                   .onSuccess(this::onSuccess)
                   .onFailure(this::onFailure)
                   .onNewerAskAgain(this::onNewerAskAgain)
                   .run();
```

Don't forget onActivityResult:

```java
 @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
   }
```


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
