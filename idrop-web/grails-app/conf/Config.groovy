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

// config properties that can be externalized
/*
 * 
 * add the following group of properties to present a user id/password only login
 * 
 idrop.config.preset.host="diamond.ils.unc.edu"
 idrop.config.preset.port="2247"
 idrop.config.preset.zone="lifelibZone"
 idrop.config.preset.resource="lifelibResc1"
 */

// required properties for idrop lite
idrop.config.idrop.lite.applet.jar="idrop-lite-1.0.0-SNAPSHOT-jar-with-dependencies.jar"
idrop.config.idrop.lite.codebase="http://iren-web.renci.org/idrop-web/applet"
idrop.config.idrop.lite.use.applet.dir=false

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
environments {
	production {  grails.serverURL = "http://emerald.ils.unc.edu:8080/${appName}" }
	development { grails.serverURL = "http://localhost:8080/${appName}" }
	test {  grails.serverURL = "http://localhost:8080/${appName}"  }
}

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
	debug 'org.irods.jargon.spring.security'
	warn 'org.springframework'
	info 'grails.app'

	info 'org.mortbay.log',
			'grails.app.controller',
			'org.springframework.security'
}
