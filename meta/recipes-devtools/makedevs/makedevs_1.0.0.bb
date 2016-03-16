DESCRIPTION = "A tool to make device nodes"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=393a5ca445f6965873eca0259a17f833"
SECTION = "base"
SRC_URI = "file://makedevs.c \
           file://COPYING.patch"
PR = "r7"

FILES_${PN}_append_class-nativesdk = " ${datadir}"

do_configure() {
	install -m 0644 ${WORKDIR}/makedevs.c ${S}/
}

do_compile() {
	${CC} ${CFLAGS} -o ${S}/makedevs ${S}/makedevs.c
}

do_install() {
	install -d ${D}${base_sbindir}
	install -m 0755 ${S}/makedevs ${D}${base_sbindir}/makedevs
}

do_install_append_class-nativesdk() {
        install -d ${D}${datadir}
        install -m 644 ${COREBASE}/meta/files/device_table-minimal.txt ${D}${datadir}/
}

BBCLASSEXTEND = "native nativesdk"
