SourceFight
===========

**Open-source distribution of SourceFight files**

This repository contains all files created as of September 17th 2014 for the SourceFight project.

While the folders have been cleaned up a bit, there are likely still a lot of redudant and obsolete files present within this repository. Sorry about that!

The workfiles folder contains a couple of files for specific software. The most common two are:
- **.pxm**: Pixelmator workfiles, which unfortunately is only available for OSX. Sorry again!
- **.blend**: Files for the free and open-source 3D suite [Blender](https://blender.org).

The final build is located in SourceFight/Build/SourceFight.jar and can be run using `java -jar SourceFight.jar` if you have JDK 1.6 installed. If you don't you'll need to make your own build, or run from an IDE like Eclipse directly. Eclipse metadata is also included.

To set up your workspace for the project, you'll need to add the following files to your Build Path:

- **lwjgl_util.jar** - lwjgl-2.8.5/jar/
- **lwjgl.jar** - lwjgl-2.8.5/jar/
- **slick.jar** - slick_dev/trunk/Slick/lib/

I have also included the open-source engine Slick2D in this repo as I have made some local changes to it. The project will fail to run using vanilla Slick.
