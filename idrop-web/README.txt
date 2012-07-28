iDrop Web

iDrop web is built using the Grails framework (www.grails.org). The Groovy scripting language and the Grails framework are compatible with the Java virtual machine.
Grails applications compile into a standard Java .war file that can be deployed on any standard Servlet container, such as Tomcat.  We test with Tomcat 6 and 7.

Grails has a set of configuration files found in the grails-app/conf directory.  The key configuration file can be found in config.groovy.  Here you need to set
a few options including:

-The URL to use when creating page links
-The location of the iDrop lite and iDrop desktop deployment files
-Whether to limit iDrop web to a particular iRODS grid
-Misc features and behaviors

These options are documented in comments in teh config.groovy file, and some presets are provided.  

To deploy on your server you may either directly edit the config.groovy file, then create a .war file using the Grails 'war' command.  For this option, see

https://code.renci.org/gf/project/irodsidrop/wiki/?pagename=Deploy+by+edititing+config

You may also download a pre-built .war file and then edit an /etc/idrop-web/idrop-web-config.groovy file on your server, For this option, see

https://code.renci.org/gf/project/irodsidrop/wiki/?pagename=pre-compiled+war

