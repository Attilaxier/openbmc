FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://0001-Add-simple-WiFi-WebuiPage.patch"

export VITE_WIFI_UI_API = "${@'true' if d.getVar('ENABLE_WIFI_UI') == '1' else 'false'}"