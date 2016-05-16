Instructions on how to build the image
======================================

This README provides step-by-step instructions for a working Qt 5.6 installation
with LinuxFB backend, based on a Yocto minimal root filesystem.


## Clone the repository


```
$ git clone git@github.com:develersrl/winmate-poky-dora-1.5.4.git
$ cd winmate-poky-dora-1.5.4
$ source oe-init-build-env
```


## Edit the configuration files


* Replace `conf/local.conf` with the following:

```
BB_NUMBER_THREADS ?= "4"
PARALLEL_MAKE ?= "-j 4"

MACHINE ??= "imx6dlsabresd"

DISTRO ?= "poky"

PACKAGE_CLASSES ?= "package_rpm"

EXTRA_IMAGE_FEATURES = "debug-tweaks"

USER_CLASSES ?= "buildstats image-mklibs image-prelink"

PATCHRESOLVE = "noop"

BB_DISKMON_DIRS = "\
    STOPTASKS,${TMPDIR},1G,100K \
    STOPTASKS,${DL_DIR},1G,100K \
    STOPTASKS,${SSTATE_DIR},1G,100K \
    ABORT,${TMPDIR},100M,1K \
    ABORT,${DL_DIR},100M,1K \
    ABORT,${SSTATE_DIR},100M,1K" 
CONF_VERSION = "1"

IMAGE_FSTYPES ?= "tar.bz2"
ACCEPT_FSL_EULA = ""

EXTRA_IMAGE_FEATURES += "ssh-server-openssh"

IMAGE_INSTALL_append = "kernel-modules \
	gpu-viv-bin-mx6q \
	fontconfig \
	freetype \
	dbus \
	icu \
	glib-2.0 \
	libcap \
	libudev \
	tslib \
	tslib-calibrate \
	tslib-tests \
	openssl \
	liberation-fonts \
	egtouch \
	"

DISTRO_FEATURES_remove = "x11 wayland"

BB_GENERATE_MIRROR_TARBALLS = "1"
```


* Replace `conf/bblayers.conf` with the following:

```
LCONF_VERSION = "6"

BBPATH = "${TOPDIR}"
BSPDIR := "${@os.path.abspath(os.path.dirname(d.getVar('FILE', True)) + '/../..')}"

BBFILES ?= ""

BBLAYERS ?= " \
  ${BSPDIR}/meta \
  ${BSPDIR}/meta-yocto \
  \
  ${BSPDIR}/meta-openembedded/meta-oe \
  \
  ${BSPDIR}/meta-fsl-arm \
  ${BSPDIR}/meta-fsl-arm-extra \
  ${BSPDIR}/meta-fsl-demos \
  ${BSPDIR}/meta-openembedded/meta-ruby \
  ${BSPDIR}/meta-openembedded/meta-multimedia \
  \
  ${BSPDIR}/meta-abb-winmate \
  "

BBLAYERS_NON_REMOVABLE ?= " \
  ${BSPDIR}/meta \
  ${BSPDIR}/meta-yocto \
  "
```


## Build


```
$ bitbake core-image-minimal
```


## Prepare and install the SDK


```
$ bitbake core-image-minimal -c populate_sdk
$ tmp/deploy/sdk/poky-eglibc-x86_64-core-image-minimal-cortexa9hf-vfp-neon-toolchain-1.5.4.sh
```

Install in the directory of your choosing (we'll assume **/opt/poky/1.5.4** from now on)


## Configure and build Qt 5.6


* Download and extract Qt 5.6 source files (eg. in `~/Qt5.6.0`)

* Download QtWebKit in Qt 5.6 source directory (eg. in `~/Qt5.6.0/5.6/Src`)

```
$ cd ~/Qt5.6.0/5.6/Src
$ git clone git@github.com:qtproject/qtwebkit.git
$ cd qtwebkit
$ git checkout 5.6
$ cd ..
```

* Configure using the following options (modify `CROSS_COMPILE` and `-sysroot` according to the SDK install path):

```
$ ./configure -v -prefix /opt/qt5 -no-pch -device imx6 -device-option \
   CROSS_COMPILE=/opt/poky/1.5.4/sysroots/x86_64-pokysdk-linux/usr/bin/arm-poky-linux-gnueabi/arm-poky-linux-gnueabi- \
   -sysroot /opt/poky/1.5.4/sysroots/cortexa9hf-vfp-neon-poky-linux-gnueabi \
   -no-largefile \
   -qt-zlib -qt-libpng -qt-libjpeg \
   -no-nis -no-cups \
   -gui -widgets \
   -opensource -confirm-license \
   -qreal float \
   -pkg-config \
   -no-compile-examples \
   -icu -fontconfig \
   -no-xcb -linuxfb -opengl es2 \
   -dbus-linked \
   -tslib
```

* Build and install

```
$ make
$ make install
```

* Build and install QtWebKit

    **NOTE:** Make sure you have the required dependencies installed on your machine when building QtWebKit (eg. `flex`)

```
$ cd qtwebkit
$ /opt/poky/1.5.4/sysroots/cortexa9hf-vfp-neon-poky-linux-gnueabi/opt/qt5/bin/qmake
$ make
$ make install
```


## Create an SD card

* Create two partitions, one (`FAT32`) for the kernel and one for the rootfs

* Copy the kernel image `<winmate-poky-dora-1.5.4-path>/build/tmp/deploy/images/imx6dlsabresd/uImage` to the first partition

* Extract the rootfs `<winmate-poky-dora-1.5.4-path>/build/tmp/deploy/images/imx6dlsabresd/core-image-minimal-imx6dlsabresd.tar.gz`
in the second partition

* Copy the Qt installation directory `/opt/poky/1.5.4/sysroots/cortexa9hf-vfp-neon-poky-linux-gnueabi/opt/qt5`
in the `/opt` directory on the SD rootfs partition.


## Cross-compiling a Qt application

* In the application folder to be compiled:

```
$ /opt/poky/1.5.4/sysroots/cortexa9hf-vfp-neon-poky-linux-gnueabi/opt/qt5/qt5/bin/qmake
$ make
```

* Copy the generated executable on the SD card (or directly on the board)


## Running the application

* Boot into the system from u-boot:

```
run boot_yocto_sd
```

* Configure eGTouchD to behave like a proper touch panel: in `/etc/eGTouchL.ini`, change the `ReportMode` to emit a click on touch event:

```
ReportMode 1
```

* Start the touchscreen daemon:

```
$ eGTouchD
```

* Configure tslib:

```
$ export TSLIB_CALIBFILE=/etc/pointercal
$ export TSLIB_TSDEVICE=/dev/input/event6
$ export TSLIB_FBDEVICE=/dev/fb0
```

* Calibrate tslib (only required the first time):

```
$ ts_calibrate
```

* Configure Qt runtime:

```
$ export QT_QPA_PLATFORM=linuxfb
$ export QT_QPA_GENERIC_PLUGINS=tslib:/dev/input/event6
```

* Run the application


