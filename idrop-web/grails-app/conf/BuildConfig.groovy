grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
//grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.project.war.file = "target/${appName}.war"


grails.project.dependency.resolution = {
	// inherit Grails' default dependencies
	inherits("global") {
		// uncomment to disable ehcace
		//excludes 'validation-api','stax-api', 'xml-apis', 'xalan', 'xml-apis-ext', 'sl4j-log4j12', 'sl4j'
		excludes   'xalan', 'xml-apis-ext','xml-apis', 'validation-api','sl4j-log4j12', 'sl4j', 'gwt-user'
	}
	log "warn" // LOG level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
	repositories {
		// uncomment the below to enable remote dependency resolution
		// from public Maven repositories
		mavenLocal()
		mavenCentral()
		mavenRepo "http://snapshots.repository.codehaus.org"
		mavenRepo "http://repository.codehaus.org"
		mavenRepo "http://download.java.net/maven/2/"
		mavenRepo "http://repository.jboss.com/maven2/"
		mavenRepo "http://ci-dev.renci.org/nexus/content/repositories/release"
		mavenRepo "http://repo.aduna-software.org/maven2/releases"
		mavenRepo "http://ci-dev.renci.org/nexus/content/repositories/snapshots"
		grailsPlugins()
		grailsHome()
		grailsCentral()
	}
	dependencies {
		// specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
		test 'org.mockito:mockito-all:1.8.1'
		compile 'commons-io:commons-io:2.1'
		provided 'junit:junit:4.8.1'

		compile ('org.irods.jargon:jargon-hive:1.0-SNAPSHOT') { excludes ("stax-api","xml-apis", "xercesImpl", "aduna-commons-lang")}
		compile ('org.irods.jargon:jargon-hive-irods:1.0-SNAPSHOT') { excludes ("stax-api", "aduna-commons-lang")}
		compile ('org.irods.jargon:jargon-core:3.3.3-SNAPSHOT') { excludes ([group:'org.jglobus'])}
		compile ('org.irods.jargon:jargon-data-utils:3.3.3-SNAPSHOT')  { excludes ([group:'org.jglobus'])}
		compile ('org.irods.jargon:jargon-ticket:3.3.3-SNAPSHOT')   { excludes ([group:'org.jglobus'])}
		compile ('org.irods.jargon:jargon-user-profile:3.3.3-SNAPSHOT')  { excludes ([group:'org.jglobus'])}
		compile ('org.irods.jargon:jargon-user-tagging:3.3.3-SNAPSHOT')  { excludes ([group:'org.jglobus'])}
		provided 'javax.servlet:servlet-api:2.5'
		compile 'org.jsoup:jsoup:0.2.2'
		compile 'xerces:xercesImpl:2.9.1'
		compile('com.google.gwt:gwt-servlet:2.4.0')
		compile('org.openrdf.sesame:sesame-sail-nativerdf:2.2.4')


	}

	plugins {
		runtime ":hibernate:$grailsVersion"

		build ":tomcat:$grailsVersion"

	}
	plugins {
		runtime ":hibernate:$grailsVersion"
		build ":tomcat:$grailsVersion"
	}
}
