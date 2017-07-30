# NoPermission
Android library for permissions request


### Gradle

    compile 'ru.alexbykov:nopermission:1.0.0'


### Install

```java
PermissionHelper permissionHelper = new PermissionHelper(context);

permissionHelper.check(Manifest.permission.READ_CONTACTS)
                .onSuccess(this::onSuccess)
                .onFailure(this::onFailure)
                .run();
```

Multiply permissions:

```java

   permissionHelper.check(Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE)
                   .onSuccess(this::onSuccess)
                   .onFailure(this::onFailure)
                   .run();
```

Don't forget onActivityResult:

```java
 @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)

        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);

   }
```