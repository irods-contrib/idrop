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
	/*production {  grails.serverURL = "http://lifetime-library.ils.unc.edu/${appName}" }
	production {  grails.serverURL = "http://iren-web.renci.org:8080/${appName}" } 
	production {  grails.serverURL = "http://www.irods.org" } */
	production {  grails.serverURL = "http://www.irods.org" }
	development { grails.serverURL = "http://localhost:8080/${appName}" }
	test {  grails.serverURL = "http://localhost:8080/${appName}"  }
}

/*
 * 2) If you want to set things up so that the idrop web browser automatically points to a certain grid (i.e. only user ID and password show, then do something like below.
 * Take the following outside of the comment block and configure here
 * 
 idrop.config.preset.host="diamond.ils.unc.edu
 idrop.config.preset.port="2247"
 idrop.config.preset.zone="lifelibZone"
 idrop.config.preset.resource="lifelibResc1"
 */


/*
 * 3) iDROP web includes the idrop-lite Java applet, which is launched from the iDROP web interface.  The interface needs to know where to find this jar file.
 * The Jar file should be placed on a web server in an accessible directory, and configured below
 */

idrop.config.idrop.lite.applet.jar="idrop-lite-1.0.0-SNAPSHOT-jar-with-dependencies.jar"
idrop.config.idrop.lite.codebase="http://iren-web.renci.org/idrop-web/applet"
idrop.config.idrop.jnlp="http://iren-web.renci.org:8080/idrop/idrop.jnlp"
idrop.config.idrop.lite.use.applet.dir=false

/*
 * 4) iDROP web includes a link to launch the iDROP desktop GUI, using Java WebStart.  WebStart looks for a jnlp file and the accompanying 
 * lib directories.  This should be deployed on a web server at some accessible location and referred to here
 */
idrop.config.idrop.jnlp="http://iren-web.renci.org:8080/idrop/idrop.jnlp"

/*
 * Some properties may be set in an external configuration file, as configured below
 */

// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts
/*
 grails.config.locations = [
 "file:${userHome}/.grails/${appName}-config.groovy"
 ]
 if(System.properties["${appName}.config.location"]) {
 grails.config.locations << "file:" + System.properties["${appName}.config.location"]
 }
 */

 /* ll config*/
/*idrop.config.idrop.lite.applet.jar="idrop-lite-1.0.0-SNAPSHOT-jar-with-dependencies.jar"
 idrop.config.idrop.lite.codebase="https://lifetime-library.ils.unc.edu/llclient"
 idrop.config.preset.host="diamond.ils.unc.edu"
 idrop.config.preset.port="2247"
 idrop.config.preset.zone="lifelibZone"
 idrop.config.preset.resource="lifelibResc1"
 idrop.config.idrop.lite.use.applet.dir=false
 idrop.config.idrop.jnlp="https://lifetime-library.ils.unc.edu/llclient/idrop.jnlp"
 */
grails.views.enable.jsessionid=true
grails.project.groupId = org.irods.mydrop // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [ html: [
		'text/html',
		'application/xhtml+xml'
	],
	xml: [
		'text/xml',
		'application/xml'
	],
	text: 'text/plain',
	js: 'text/javascript',
	rss: 'application/rss+xml',
	atom: 'application/atom+xml',
	css: 'text/css',
	csv: 'text/csv',
	all: '*/*',
	json: [
		'application/json',
		'text/json'
	],
	form: 'application/x-www-form-urlencoded',
	multipartForm: 'multipart/form-data'
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// whether to install the java.util.logging bridge for sl4j. Disable for AppEngine!
grails.logging.jul.usebridge = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
grails.validateable.packages = ['org.irods']

// set per-environment serverURL stem for creating absolute links

// log4j configuration
log4j = {

	root {
		warn()
		additivity = true
	}

	// Example of changing the log pattern for the default console
	// appender:
	//
	// appenders {
	//     console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
	// }

	error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
			'org.codehaus.groovy.grails.web.pages', //  GSP
			'org.codehaus.groovy.grails.web.sitemesh', //  layouts
			'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
			'org.codehaus.groovy.grails.web.mapping', // URL mapping
			'org.codehaus.groovy.grails.commons', // core / classloading
			'org.codehaus.groovy.grails.plugins', // plugins
			'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
			'org.hibernate',
			'net.sf.ehcache.hibernate'

	warn 'org.irods.mydrop'
	info 'org.irods.jargon'
	warn 'org.irods.jargon.spring.security'
	warn 'org.springframework'
	info 'grails.app'

	warn 'org.mortbay.log',
			'grails.app.controller',
			'org.springframework.security'
}
