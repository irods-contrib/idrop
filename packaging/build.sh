#!/bin/bash
#requirements:
# Jargon
# Java SDK 1.6+
# Maven 3+
# Grails
# EPM packager

SCRIPTNAME=`basename $0`

# define which idrop-lite applet to use
IDROP_LITE_APPLET=idrop-lite-1.0.2-SNAPSHOT-jar-with-dependencies.jar

# in case we need to download maven
MAVENVER=3.0.4
MAVENFILE=apache-maven-$MAVENVER
MAVENDOWNLOAD=http://www.apache.org/dyn/closer.cgi/maven/maven-3/$MAVENVER/binaries/$MAVENFILE-bin.zip

# in case we need to download grails
GRAILSVER=2.1.1
GRAILSFILE=grails-$GRAILSVER
GRAILSDOWNLOAD=http://dist.springframework.org.s3.amazonaws.com/release/GRAILS/$GRAILSFILE.zip


# define usage
USAGE="

Usage: $SCRIPTNAME [<proxy hostname> <proxy portnum>]

Example:
$SCRIPTNAME www.myhost.com 80
"

# check for correct num of args
if [[ $# -gt 0  &&  $# -lt 2 || $# -gt 2 ]]; then
	echo $USAGE
	exit 1
fi
PROXYHOST=$1
PROXYPORT=$2

# setup MAVEN settings file
UGLYSETTINGSFILESTRING='
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  mlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <proxies>
    <proxy>
      <active>true</active>
      <protocol>http</protocol>
      <host>'$PROXYHOST'</host>
      <port>'$PROXYPORT'</port>
    </proxy>
   </proxies>
</settings>'


# get into the correct directory
BUILDDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $BUILDDIR


# check to make sure mvn, grails, and epm commands are in path
MAVEN=`which mvn`
if [[ "$?" != "0" || `echo $MAVEN | awk '{print $1}'` == "no" ]] ; then
	echo "Apache Maven required to build project - downloading from $MAVENDOWNLOAD"

	# download maven
	wget $MAVENDOWNLOAD

	# install and setup environment
	unzip $MAVENFILE-bin.zip
	export M2_HOME=$BUILDDIR/$MAVENFILE
	export M2=$M2_HOME/bin
	export PATH=$M2:$PATH

	# create the .m2 dir
	mvn --version > /dev/null 2>&1
	
	# save old maven settings file if one exists
	mv ~/.m2/settings.xml save_settings.xml > /dev/null 2>&1
	echo $UGLYSETTINGSFILESTRING > ~/.m2/settings.xml
else
	MAVENVERSION=`mvn --version`
	echo "Detected maven [$MAVEN] version[$MAVENVERSION]"
fi

GRAILS=`which grails`
if [[ "$?" != "0" || `echo $GRAILS | awk '{print $1}'` == "no" ]] ; then
	echo "GRAILS required to build project - downloading from $GRAILSDOWNLOAD"

	# download grails
	wget $GRAILSDOWNLOAD

	# install and setup environment
	unzip $GRAILSFILE.zip
	export GRAILS_HOME=$BUILDDIR/$GRAILSFILE
	export PATH=$GRAILS_HOME/bin:$PATH

	# setup proxy if needed
	if [[ $PROXYHOST ]]; then
		grails add-proxy idrop_proxy --host=$PROXYHOST --port=PROXYPORT
		grails set-proxy idrop_proxy
	fi
else
	GRAILSVERSION=`grails --version`
	echo "Detected grails [$GRAILS] version[$GRAILSVERSION]"
fi

# now get our version of EPM
RENCIEPM="epm42-renci.tar.gz"
rm -rf epm
rm -f $RENCIEPM
wget ftp://ftp.renci.org/pub/e-irods/build/$RENCIEPM
tar -xf $RENCIEPM
cd $BUILDDIR/epm
echo "Configuring EPM"
./configure > /dev/null
if [ "$?" != "0" ]; then
	exit 1
fi
echo "Building EPM"
if [ "$?" != "0" ]; then
	exit 1
fi
make > /dev/null

cd $BUILDDIR

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
  ./epm/epm -f rpm idrop-web RPM=true idrop-web.list
elif [ -f "/etc/SuSE-release" ]; then # SuSE
  echo "Running EPM :: Generating RPM"
  ./epm/epm -f rpm idrop-web RPM=true idrop-web.list
elif [ -f "/etc/lsb-release" ]; then  # Ubuntu
  echo "Running EPM :: Generating DEB"
  ./epm/epm -a amd64 -f deb idrop-web DEB=true idrop-web.list
elif [ -f "/usr/bin/sw_vers" ]; then  # MacOSX
  echo "TODO: generate package for MacOSX"
fi
