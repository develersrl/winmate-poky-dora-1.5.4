FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"
PRINC := "${@int(PRINC) + 5}"

SRC_URI = "git://github.com/develersrl/winmate-kernel.git;protocol=ssh;user=git"
SRCREV = "f8e78a38e19b3415f6bcde9ceb4a16ee37f3b97a"

FILESPATH_prepend := "${THISDIR}/files:"
SRC_URI += "file://defconfig"

