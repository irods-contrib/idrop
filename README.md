
*'''Project''': iDrop-swing- Desktop iRODS transfer manager and supporting libraries
*'''Date''': 
*'''Release Version''': 4.2.0.1-SNAPSHOT
*'''git tag''': 


==News==

Release of new iDrop 2 web and desktop 

This is the iDrop desktop transfer manager client.  iDrop runs in the system tray of your favorite operating system, and can manage transfers between your computer and iRODS, and manage data once in iRODS.

iDrop also automates synchronization between your desktop and iRODS.  There is an initial local -> iRODS backup capability, with automatic version of files within iRODS.  Other modes are in development

GitHub:  [[https://github.com/DICE-UNC/idrop]]

==Requirements==

-iDrop depends on Java 1.8+
-iDrop is built using Maven

iDrop-swing uses Maven for dependency management.  See the pom.xml file for references to various dependencies.

Note that the following bug and feature requests are logged in GForge with related commit information [[https://code.renci.org/gf/project/irodsidrop/tracker/]]

==Features==
*[#1092] add reconnect option to iDrop
**Added preferences panel option and idrop.properties to set reconnect to 'true'.  Emulates -T option for put/get

*[#983] iDrop swing '2.0' development
**Phase I of iDrop swing GUI redesign effort.  This collaboration with iPlant is a GUI refactoring and redesign phase.

*[#1441] add diff view to idrop
**Added a tools menu and a diff view dialog to compare local and iRODS directories, viewing differences

==Bug Fixes==


*[#1340] allow switching of resource by default

*[#1268] XML Parse Exception in idrop.jnlp
**added jnlp template and improved jnlp file consistency and structure

*[#1250] jargon/idrop performance testing and optimization for 3.3.2
**Fix pom.xml for missing reference to idrop-swing module

*[#1362] apparent start-up errors idrop checking for strict acls
**Added overhead for rule errors on some servers

*[#1553] faulty sl4j bindings
**Added sl4j/log4j bindings removed in jargon-core

*[#1850] Direct login mode allows use of iDrop on a shared computer setting with a temporary set of accounts

==Outstanding Issues==

Please consult [[https://code.renci.org/gf/project/irodsidrop/tracker/]]

for the latest open bugs and Jargon feature requests
