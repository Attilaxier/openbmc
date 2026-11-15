FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://0001-Add-simple-WiFi-redfish-API.patch"

# Use flag:ENABLE_WIFI_UI to control env:redfish-wifi
PACKAGECONFIG[redfish-wifi] = "-Dredfish-wifi=enabled,-Dredfish-wifi=disabled"
PACKAGECONFIG:append = "${@' redfish-wifi' if d.getVar('ENABLE_WIFI_UI') == '1' else ''}"
