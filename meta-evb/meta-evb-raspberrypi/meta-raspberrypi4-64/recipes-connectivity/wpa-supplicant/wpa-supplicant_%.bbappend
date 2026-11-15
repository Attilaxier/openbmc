FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += " \
    file://wpa_supplicant-wlan0.conf \
    file://dbus.conf \
"

SYSTEMD_SERVICE:${PN}:append = " wpa_supplicant@wlan0.service"
SYSTEMD_SERVICE:${PN}:remove = " wpa_supplicant.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

do_install:append() {
    install -d ${D}${sysconfdir}/wpa_supplicant
    install -m 0600 ${UNPACKDIR}/wpa_supplicant-wlan0.conf ${D}${sysconfdir}/wpa_supplicant/wpa_supplicant-wlan0.conf

    install -d ${D}${sysconfdir}/systemd/system/wpa_supplicant@.service.d
    install -m 0644 ${UNPACKDIR}/dbus.conf ${D}${sysconfdir}/systemd/system/wpa_supplicant@.service.d/dbus.conf

    rm -f ${D}${datadir}/dbus-1/system-services/fi.w1.wpa_supplicant1.service
    rm -f ${D}${systemd_system_unitdir}/wpa_supplicant.service
    rmdir --ignore-fail-on-non-empty ${D}${datadir}/dbus-1/system-services
    rmdir --ignore-fail-on-non-empty ${D}${datadir}/dbus-1
}

FILES:${PN} += "${sysconfdir}/systemd/system/wpa_supplicant@.service.d/dbus.conf"