
PROJECT=pauline
KEYSTORE=keystore

# unsigned APK
APK=$(PROJECT)-unsigned.apk
# signed APK
S_APK=$(PROJECT).apk

# directory to unzip apk
APK_UNZIP_DIR=apk_unzip

# parameters for config
POSS_API_URL=https://assos.utc.fr/buckutt/POSS3
KEY_API_URL=https://assos.utc.fr/buckutt/KEY
PACKAGE=fr.utc.assos.payutc

deploy: setconfig build reinstall

# change the config (API_URL and POI_ID)
setconfig:
	echo '<?xml version="1.0" encoding="utf-8"?>\n<config\n  poss_api_url="$(POSS_API_URL)"\n  key_api_url="$(KEY_API_URL)"\n/>' > res/raw/config.xml

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

apk-setconfig: _checkapk
	mkdir $(APK_UNZIP_DIR)
	unzip $(abspath $(APK)) -d $(APK_UNZIP_DIR)
	echo '<?xml version="1.0" encoding="utf-8"?>\n<config\n  poss_api_url="$(POSS_API_URL)"\n  key_api_url="$(KEY_API_URL)"\n/>' > $(APK_UNZIP_DIR)/res/raw/config.xml
	rm -f $(APK)~
	mv $(APK) $(APK)~
	cd $(APK_UNZIP_DIR) && zip -r ../$(APK) .
	rm -r $(APK_UNZIP_DIR)

apk-showconfig: _checkapk
	mkdir $(APK_UNZIP_DIR)
	unzip $(abspath $(APK)) -d $(APK_UNZIP_DIR)
	cat $(APK_UNZIP_DIR)/res/raw/config.xml; echo
	rm -r $(APK_UNZIP_DIR)


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
