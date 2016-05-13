SUMMARY = "eGalaxy Touchscreen user-space daemon"
SECTION = "graphics"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://EULA.pdf;md5=7efb93aac717b745954e9221ae5c23f1"

SRC_URI = "file://eGTouch_v${PV}.L-ma.tar.gz"

SRC_URI[md5] = ""
SRC_URI[sha256] = ""

S = "${WORKDIR}/eGTouch_v${PV}.L-ma"

do_install() {
	install -d ${D}${bindir}
	install -d ${D}${sysconfdir}

	install -m 0755 ${S}/eGTouchARMhf/eGTouchARMhfnonX/eGTouchD ${D}${bindir}
	install -m 0644 ${S}/eGTouchARMhf/eGTouchARMhfnonX/eGTouchL.ini ${D}${sysconfdir}
}

