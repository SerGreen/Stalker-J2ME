# Stalker the game
__Very__ old J2ME game (for old cell phones), originally was a university project, then evolved into full game.  
Why is it called 'Stalker'? Well, that's how it was called in the uni task, so the name stuck.

[View full animation __somewhere__](link_to_somewhere)  
![[Stalker demo](link_to_small_gif)](link_to_somewhere)

## Download
You can download jar and jad files [here](link_to_release). Also i included J2ME emulator, that can run this game almost perfectly.

## How to open source project
Note for my future self or anyone interested.  
The problem is that J2ME is so dead now, that you can't even download nesessary plugins for Eclipse from Oracle website. Well, shit.  

So here's the list of things needed in order to open and compile project:
* [Eclipse Indigo](http://www.eclipse.org/downloads/packages/release/indigo/sr2)  
  _Why **Indigo**? Well, i tried at Neon first and got many unobvious errors. Maybe i did something wrong or maybe that's Eclipse being fragile piece of shit as usual. Anyways, i finally managed to make it work with Indigo, so Indigo it be._
* [Java ME SDK 3.0.5](http://www.oracle.com/technetwork/java/embedded/javame/javame-sdk/downloads/java-me-sdk-3-0-5-download-1849683.html) (It includes Wireless Toolkit = WTK)
* [Antenna jar library](https://sourceforge.net/projects/antenna/files) (Just save it somewhere, e.g. into Java ME SDK directory)
* [EclipseME](http://eclipseme.org/docs/installEclipseME.html) plugin  
  _After installation of the plugin go to Preferences and set paths to WTK root and antenna.jar_
* ~~Fail to install [Oracle Java ME SDK Eclipse Plugin](http://docs.oracle.com/javame/config/cldc/rel/3.3/win/gs/html/getstart_win32/setup_eclipseenv.htm) because download link on Oracle site is dead. Apparently, you will be fine without it.~~
* [MIDP 2.1 jar](http://www.java2s.com/Code/Jar/m/Downloadmidp21jar.htm) (I included it right into the project in the lib directory, so there's no need to download it. Mentioned it just in case.)
* Now go to Preferences > J2ME > Device Management > Import > Browse > _Select WTK directory_ > Refresh > Select All > Finish
* Now go to Preferences > J2ME > Preverification > Select 'Use JAD file setting'
* If it says that it can't resolve some imports like `java.util.*` or `javax.microedition.*`, make sure that JRE and MIDP 2.1 are linked in Build Path Configuration > Libraries.
* Finally, create Run Configuration as Wireless Toolkit Emulator

Now you should be able to launch it. However there're problems with this emulator: it will throw OutOfMemory if you open Options/About screen and highly possible after completing a level too. But you will get compiled jar + jad files in 'deployed' folder. You can now launch game with any other emulator (like KEmulator i attached to [release](link_to_release)) or even upload it to your good old Nokia.
