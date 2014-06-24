
/*
 * Configuration for idrop-web - iDROP Cloud browser
 * Project page: https://code.renci.org/gf/project/irodsidrop/
 * 
 * Deployment instructions:
 * 
 * 1) Set configuration for your deployment server.  Set the name in production below to the URL you want to use.  This value is used by Grails to 
 * compute links, so it should be the front end for your application (e.g. if you run Apache HTTP server in front of Tomcat with SSL, this would be
 * the end-users 'https://' URL
 * 
 */
environments {
	
	 production {  grails.serverURL = "http://localhost:8080/idrop-web2" } 
	 
}

/*
 * 2) If you want to set things up so that the idrop web browser automatically points to a certain grid (i.e. only user ID and password show, then do something like below.
 * Take the following outside of the comment block and configure here
 * 
 idrop.config.preset.host="someHost"
 idrop.config.preset.port="1247"
 idrop.config.preset.zone="someZone"
 idrop.config.preset.resource="someResc"
 // can be Standard or PAM right now
 idrop.config.preset.authScheme="Standard"
 */

//idrop.config.idrop.jnlp="http://iren-web.renci.org:8080/idrop/idrop.jnlp"
//drop.config.idrop.jnlp="http://iren-web.renci.org/idrop-snapshot/idrop.jnlp"
idrop.config.idrop.jnlp="http://iren-web.renci.org/idrop-snapshot/idrop.jnlp"
/*
 * 3) Customization properties
 */

// do I support tickets? This determies whether the ticket feature is available via the interface, it also requires ticket support in iRODS itself (version 3.1+)
idrop.config.use.tickets=true

// do I want to display the profile tab and maintain user profile info 
idrop.config.use.userprofile=true

// do I support sharing? Requires target server to have specific query support and sharing queries loaded from jargon-user-tagging
idrop.config.use.sharing=true

// do I want to show the gallery view?
idrop.config.use.gallery.view=false

// do I want to show the browse view?
idrop.config.use.browse.view=false
