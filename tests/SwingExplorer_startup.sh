#!/bin/bash


#  Define variables
SwingLibraryJar=$HOME/trunk/Jars/swinglibrary.jar
SwingExplorerJar=$HOME/trunk/Jars/swexpl.jar
SwagJar=$HOME/trunk/Jars/swag.jar
iDropJar=idrop-swing-2.0.1-RC1.jar
iDropJarDeps=dependency/*
JarMissing=NO


#  Verify that jar files are available
if [ ! -r "$SwingLibraryJar" ]; then
	echo "* * *   Missing SwingLibraryJar file [$SwingLibraryJar]"
	JarMissing=YES
fi
if [ ! -r "$SwingExplorerJar" ]; then
	echo "* * *   Missing SwingExplorerJar file [$SwingExplorerJar]"
	JarMissing=YES
fi
if [ ! -r "$SwagJar" ]; then
	echo "* * *   Missing SwagJar file [$SwagJar]"
	JarMissing=YES
fi
if [ ! -r "/Applications/iDrop Transfer Manager.app/Contents/java/app/$iDropJar" ]; then
	echo "* * *   Missing iDropJar file [/Applications/iDrop Transfer Manager.app/Contents/java/app/$iDropJar]"
	JarMissing=YES
fi
if [ "$JarMissing" == "YES" ]; then
	exit
fi


cd /Applications/iDrop\ Transfer\ Manager.app/Contents/java/app
java -cp $SwingExplorerJar:$SwagJar:$iDropJar:$iDropJarDeps org.swingexplorer.Launcher org.irods.jargon.idrop.desktop.systraygui.IDROPDesktop

