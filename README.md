#### Project: iDrop Suite - iDrop Clients
#### Date: 03/31/2014 
#### Release Version: 2.0.1-beta2
#### git tag: 2.0.1-beta2

## News

This is a beta of iDrop desktop installed via Install4j, removing the dependency on Java WebStart.  After this evaluation, we will purchase a license for Install4J for
the production release.

This version of iDrop desktop is completely refactored and includes the new 'conveyor' framework that was released in beta form with Jargon 3.3.3-beta1. iDrop has been tested and is compatible 
with iRODS 3.3.1 as well as iRODS 4.0 RC1. It is expected that another beta will be released after iRODS 4.0 RC2, and then a final release will be made with 4.0 compatability.  

Standard and PAM authentication are supported in the current version of iDrop desktop.  

This release also has incremental bug fixes and improvements to iDrop web and iDrop lite.  Note that iDrop web will be replaced with a much-improved version that is being developed
on the idrop3 git branch.

See the release notes in the individual subprojects for details.  Please go to [[https://github.com/DICE-UNC/idrop]] for the latest news and info.

iDrop consists of the following libraries

* idrop-swing - Desktop transfer and synchronization manager
* idrop-lite - Applet plug-in for bulk uploads and downloads
* idrop-web - Web browser interface for file operations

Other modules are planned in later releases for REST-ful API and mobile access.

## Requirements

* Jargon depends on Java 1.6+
* Jargon is built using Apache Maven2, see POM for dependencies
* Jargon supports iRODS 2.5 through iRODS 4.0RC1

## Libraries

Jargon-core uses Maven for dependency management.  See the pom.xml file for references to various dependencies.

NOTE: bug fixes and features in individual sub-project release notes


## Enhancements and Fixes

#### set directory custom path in iDrop appears to be not working on Win7 #7

#### Flow manager integration #4

integration of flow manager framework from jargon/conveyor.  See: https://github.com/DICE-UNC/idrop/issues/4

#### Broken build/deploy script for idrop-web #16

see: https://github.com/DICE-UNC/idrop/issues/16

#### unable to view under public via idrop web #56 

Added new functional tests and cleanup for handling of listings under public, especially when anonymous

#### idrop web public login does not hide uid/password #55

Fixed login screen functionality to correctly hide or display login fields when doing guest logins


#### file not found error in GENI portal access subcollection that should have permissions #57

Added better handling of special characters, especially the + character, in file names for download operations
 
