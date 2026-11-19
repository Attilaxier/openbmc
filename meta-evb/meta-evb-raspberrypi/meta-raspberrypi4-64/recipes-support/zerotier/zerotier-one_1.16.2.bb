SUMMARY = "ZeroTier network virtualization service"
DESCRIPTION = "ZeroTier is a smart programmable Ethernet switch for planet Earth. It allows \
all networked devices, VMs, containers, and applications to communicate as if they all reside \
in the same physical data center or cloud region. This is accomplished by combining a \
cryptographically addressed and secure peer to peer network (termed VL1) with an Ethernet \
emulation layer somewhat similar to VXLAN (termed VL2). Our VL2 Ethernet virtualization layer \
includes advanced enterprise SDN features like fine grained access control rules for network \
micro-segmentation and security monitoring. All ZeroTier traffic is encrypted end-to-end using \
secret keys that only you control. Most traffic flows peer to peer, though we offer free (but \
slow) relaying for users who cannot establish peer to peer connections."
HOMEPAGE = "https://www.zerotier.com/"

# License notes:
#   COPYING, LICENSE.txt, debian/copyright -> ZeroTier BSL 1.1 (not an SPDX id)
#   ext/http-parser, ext/json, ext/libnatpmp, ext/miniupnpc -> MIT
# BSL 1.1 restricts some commercial use; verify it fits your product.
# Uncomment the next line if you want users to explicitly whitelist it
# (then add LICENSE_FLAGS_ACCEPTED += "commercial" in local.conf):
#LICENSE_FLAGS = "commercial"
LICENSE = "MIT AND LicenseRef-ZeroTier-BSL-1.1"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=10a2f6efeb9fb6ee1dd67b697c8cbc56 \
                    file://debian/copyright;md5=f4a6ffbe72aad9cedd587bcb18904813 \
                    file://ext/http-parser/LICENSE-MIT;md5=20d989143ee48a92dacde4f06bbcb59a \
                    file://ext/libnatpmp/LICENSE;md5=63b8bf0fd09f4909d823a94c6e6fc06b \
                    file://ext/miniupnpc/LICENSE;md5=4a95d5317ee6dac993b7598848c72c1e \
                    "

FILESEXTRAPATHS:prepend := "${THISDIR}/files/:"

SRC_URI = "git://github.com/zerotier/ZeroTierOne.git;protocol=https;nobranch=1;tag=1.16.2 \
           "

# On kirkstone or older, use: PV = "1.6.5+git${SRCPV}"
PV = "1.16.2+git"
SRCREV = "fc5c3ec22090b5b2a0f274e863651fe9ca489bf4"

# On scarthgap/walnascar or newer, change to: S = "${UNPACKDIR}/${BP}"
# and add ";destsuffix=${BP}" to the git SRC_URI (see your release's docs)

# ZT_DEBUG=1 removed (it builds a debug-flavoured binary). Add it back if needed.
EXTRA_OEMAKE = " \
    DESTDIR=${D} \
    STRIP=echo \
    ZT_SSO_SUPPORTED=0 \
"

do_configure:prepend() {
    sed -i 's/all:\s*one zeroidc/all: one/g' ${S}/make-linux.mk
    sed -i '/zeroidc/d' ${S}/make-linux.mk
}

do_compile() {
    oe_runmake ZT_SSO_SUPPORTED=0 one
}

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${S}/zerotier-one ${D}${sbindir}/zerotier-one
    ln -sf zerotier-one ${D}${sbindir}/zerotier-cli
    ln -sf zerotier-one ${D}${sbindir}/zerotier-idtool

    if [ -f ${S}/debian/zerotier-one.service ]; then
        install -d ${D}${systemd_system_unitdir}
        install -m 0644 ${S}/debian/zerotier-one.service ${D}${systemd_system_unitdir}/
    fi
}

inherit systemd
SYSTEMD_SERVICE:${PN} = "zerotier-one.service"

# Do not enable by default; ZeroTier requires manual setup anyway.
# On the target: systemctl enable zerotier-one ; systemctl start zerotier-one
SYSTEMD_AUTO_ENABLE = "disable"

TARGET_CC_ARCH += "${LDFLAGS}"