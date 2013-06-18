
PROJECT=pauline
KEYSTORE=keystore

# unsigned APK
APK=$(PROJECT)-unsigned.apk
# signed APK
S_APK=$(PROJECT).apk

# directory to unzip apk
APK_UNZIP_DIR=apk_unzip

PACKAGE=fr.utc.assos.payutc

deploy: build reinstall

# Build the signed & aligned apk in release mode
build:
	ant release
	cp bin/$(PROJECT)-release.apk $(S_APK)

_checkapk:
	@if [ -z "$(DEVICE)" ]; then echo "Need DEVICE parameter"; exit 1; fi

apk-sign: _checkapk
	rm -f $(S_APK)
	cp $(APK) $(S_APK)
	jarsigner -verbose -sigalg MD5withRSA -digestalg SHA1 -keystore $(KEYSTORE) $(S_APK) payutc

install:
	if [ -z "$(DEVICE)" ]; then adb -d install $(S_APK); else adb -s $(DEVICE) install $(S_APK); fi

reinstall:
	if [ -z "$(DEVICE)" ]; then adb -d install -r $(S_APK); else adb -s $(DEVICE) install -r $(S_APK); fi

uninstall:
	if [ -z "$(DEVICE)" ]; then adb -d uninstall $(PACKAGE); else adb -s $(DEVICE) uninstall $(PACKAGE); fi

showdevices:
	adb devices

clean:
	ant clean
	rm -f $(S_APK)
	rm -f $(APK_UNZIP_DIR)

help:
	@cat Makefile.help
