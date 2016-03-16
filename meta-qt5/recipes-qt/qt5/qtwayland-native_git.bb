require qt5-git.inc
require ${PN}.inc

# qtwayland wasn't released yet, last tag before this SRCREV is 5.0.0-beta1
# this PV is only to indicate that this recipe is compatible with qt5 5.1.0
# while qtwayland_git stays compatible with 5.0.2
PV = "5.1.0+git${SRCPV}"

SRCREV = "ede872db1cdfdc2810c2dd29edd5fb6e1cdac0f5"
