#!/bin/bash


#  Define variables
SwingLibraryJar=$HOME/trunk/Jars/swinglibrary.jar
#iDropJar=/Applications/iDrop\ Transfer\ Manager.app/Contents/java/app/idrop-swing-2.0.1-RC1.jar
iDropJar='/Applications/iDrop Transfer Manager.app/Contents/java/app/idrop-swing-2.0.1-RC1.jar'
iDropJarDeps=dependency/*
JarMissing=NO
TxtFiles=NO
OverRideVars=$HOME/trunk/iDropVars.txt
RegTestList=$HOME/trunk/idrop/tests/iDrop_Tests.txt


#echo "SwingLibraryJar is [$SwingLibraryJar]"
#echo "iDropJar is [$iDropJar]"


#  Verify that jar files are available
if [ ! -r "$SwingLibraryJar" ]; then
	echo "* * *   Missing SwingLibraryJar file [$SwingLibraryJar]"
	JarMissing=YES
fi
if [ ! -r "$iDropJar" ]; then
	echo "* * *   Missing iDropJar file [$iDropJar]"
	JarMissing=YES
fi
if [ "$JarMissing" == "YES" ]; then
	exit
fi


#  Define ClassPath
export CLASSPATH="$SwingLibraryJar:$iDropJar:$iDropJarDeps"
#echo "CLASSPATH is [$CLASSPATH]"


# Verify that output and downloads directories exist, create if they don't
if [ ! -d $HOME/junk/RegressionTests_iDrop ]; then
	mkdir -p $HOME/junk/RegressionTests_iDrop
else
	rm -rf $HOME/junk/RegressionTests_iDrop/*
fi


##  Checkout the code for a specific branch based on environment running in
cd $HOME/trunk/idrop/tests/RFTests
git checkout master
git pull


#  Verify that regression list is available along with Vars file
if [ ! -r "$OverRideVars" ]; then
	echo "* * *   Missing Vars file [$OverRideVars]"
	TxtFiles=YES
fi
if [ ! -r "$RegTestList" ]; then
	echo "* * *   Missing Regression list file [$RegTestList]"
	TxtFiles=YES
fi
if [ "$TxtFiles" == "YES" ]; then
	exit
fi


#  Run Regression
cd $HOME/trunk/idrop/tests
jybot -A $HOME/trunk/iDropVars.txt -e RegSkip -A iDrop_Tests.txt

