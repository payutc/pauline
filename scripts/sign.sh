#!/bin/sh

set -x
DIR=$(pwd)
APK=$(readlink -f $1)
APK_NAME=$(basename $APK .apk)

KEYSTORE=$(readlink -f $2)

API_URL=$3
POI_ID=$4


TMP=$(mktemp -d)

cd $TMP
pwd

cp $APK .
mkdir apk_unzip
cd apk_unzip
unzip ../$APK_NAME.apk


cat > res/raw/config.xml << EOF
<?xml version="1.0" encoding="utf-8"?>
<config
   api_url="$API_URL"
   cas_service="https://cas.utc.fr/cas/"
   poi_id="$POI_ID"
/>
EOF

cat res/raw/config.xml

zip -r ../$APK_NAME.zip .

cd ..

rm $APK_NAME.apk
mv $APK_NAME.zip $APK_NAME.apk

jarsigner -verbose -sigalg MD5withRSA -digestalg SHA1 -keystore $KEYSTORE $APK_NAME.apk payutc

mv $APK_NAME.apk $DIR/$APK_NAME.signed.apk

rm -r $TMP



