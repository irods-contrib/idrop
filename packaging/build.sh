#!/bin/bash
#requirements:
# Jargon
# Java SDK 1.6+
# Maven 3+
# Grails
# EPM packager

# define witch idrop-lite applet to use
IDROP_LITE_APPLET=idrop-lite-1.0.1-SNAPSHOT-jar-with-dependencies.jar

# check to make sure mvn, grails, and epm commands are in path
command -v mvn >/dev/null 2>&1 || { echo "Maven commands must be in PATH to package iDrop Web. Aborting." >&2; exit 1; }

command -v grails >/dev/null 2>&1 || { echo "Grails commands must be in PATH to package iDrop Web. Aborting." >&2; exit 1; }

command -v epm >/dev/null 2>&1 || { echo "EPM commands must be in PATH to package iDrop Web. Aborting." >&2; exit 1; }

# get into the correct directory
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $DIR

# build idrop swing first
echo "Building iDrop Swing ..."
cd ../idrop-swing
#mvn assembly:assembly -Dmaven.test.skip=true
# TODO: jar (JNLP) created is signed and expire is 6 months????
mvn clean package webstart:jnlp-inline -Dmaven.test.skip=true
RETVAL=$?
if [ $RETVAL -eq 1 ]; then
  echo "iDrop Swing Build Failed - Exiting"
  echo "Packaging Failed"
  exit 1
fi

# now build idrop-lite
echo "Building iDrop Lite ..."
cd ../idrop-lite
mvn clean assembly:assembly -Dmaven.test.skip=true
RETVAL=$?
if [ $RETVAL -eq 1 ]; then
  echo "iDrop Lite Build Failed - Exiting"
  echo "Packaging Failed"
  exit 1
fi

# now build grails war file
echo "Building iDrop Web ..."
cd ../idrop-web
grails war idrop-web.war
# TODO: check return value of grails build??

#create EPM .list file
# available from: http://fossies.org/unix/privat/epm-4.2-source.tar.gz
# md5sum 3805b1377f910699c4914ef96b273943

echo "Creating Package ..."
cd ../packaging
if [ -d idrop-web ]; then
  rm -rf idrop-web
fi

if [ -f idrop-web.list ]; then
  rm idrop-web.list
fi

mkdir idrop-web
cd idrop-web
cp ../../idrop-web/idrop-web.war .
unzip idrop-web.war
mkdir idrop-web-extras
# get idrop-lite applet
cp ../../idrop-lite/target/$IDROP_LITE_APPLET idrop-web-extras
# get idrop-swing app
cp -r ../../idrop-swing/target/jnlp/* idrop-web-extras
rm -f idrop-web.war
cd ..

mkepmlist -u idropweb -g idropweb --prefix /var/lib/idrop-web idrop-web > idrop-web.list
sed 's/\$/$$/g' idrop-web.list > tmp.list
cat idrop-web.list.template tmp.list > idrop-web.list
rm tmp.list

if [ -f "/etc/redhat-release" ]; then # CentOS and RHEL and Fedora
  echo "Running EPM :: Generating RPM"
  epm -f rpm idrop-web RPM=true idrop-web.list
elif [ -f "/etc/SuSE-release" ]; then # SuSE
  echo "Running EPM :: Generating RPM"
  epm -f rpm idrop-web RPM=true idrop-web.list
elif [ -f "/etc/lsb-release" ]; then  # Ubuntu
  echo "Running EPM :: Generating DEB"
  epm -a amd64 -f deb idrop-web DEB=true idrop-web.list
elif [ -f "/usr/bin/sw_vers" ]; then  # MacOSX
  echo "TODO: generate package for MacOSX"
fi
