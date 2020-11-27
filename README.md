Example project using the CubiCapture library module for Android
======================
Copyright 2020 CubiCasa

This project provides an example implementation and use of the CubiCapture library module.


## Documentation

To learn more about the functionalities, and for instructions on how to add the library module to your app 
see the [CubiCapture Android Library](//www.cubi.casa/developers/cubicasa-android-sdk) documentation.


## Latest release

CubiCapture 2.0.0


## Release Notes

2.0.0:
- Optimized image converting
- Fixed camera's video preview delay
- Changes to the status updates
- New status updates
- Added default warnings and graphics provided by CubiCasa
- Added an ability to change graphics and warning texts
- Added an ability to disable or enable scan timer and back button
- Added an ability to change all scans -folder's name
- Added a setWarningSound() method to change warning sound
- Added a setNewView() method to replace CubiCapture View with your own View
- Added a setAutoZippingEnabled() method to enable/disable auto zipping
- Added a zipScan() method to zip your scan data at any time
- Added a onWindowFocusChanged() method to set up the Android full screen mode
- Fixed a bug where devices with Android 11 stalled when aborting a started scan