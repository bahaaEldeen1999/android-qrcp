# android-qrcp
qrcp like android app, for sharing files from android to ios via local network file sharing

## the goal is to make sharing files between android and IOS a breeze

### Idea of working 
the app spins up a HTTP server on the android device, and listens on port 1234 for any incoming request, as well as having a reference/URI to the file the user want to share, whenever a device connects to the server the app will send back a datastream with the file contents as well as the mimetype 

### all you have to do is:
1. install the apk on your android device 
1. disable any battery optimization for the app
1. choose the file you want to send 
1. from your ios device that is connected to the same network as the android, or  even connected to the android's device hotspot directly, scan the qr code 
1. enjoy you have the file now on your ios 

#### Android
![](https://github.com/bahaaEldeen1999/android-qrcp/new/master/android_demo.gif)

#### IOS
![](https://github.com/bahaaEldeen1999/android-qrcp/new/master/ios_demo.gif)

### current limitation 
- only send one file at a time (to get around this you will need to compress your files into a single zip file)
- need to select the file starting from your device root sdcard 
