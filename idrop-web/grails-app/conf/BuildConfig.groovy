grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
//grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.project.war.file = "target/${appName}.war"
grails.project.dependency.resolution = {
	// inherit Grails' default dependencies
	inherits("global") { // uncomment to disable ehcace
		excludes 'ehcache'}
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
		compile ('org.irods.jargon:jargon-core:3.3.3-SNAPSHOT') { excludes ([group:'org.jglobus'])}
		compile ('org.irods.jargon:jargon-data-utils:3.3.3-SNAPSHOT')  { excludes ([group:'org.jglobus'])}
		compile ('org.irods.jargon:jargon-ticket:3.3.3-SNAPSHOT')   { excludes ([group:'org.jglobus'])}
		compile ('org.irods.jargon:jargon-user-profile:3.3.3-SNAPSHOT')  { excludes ([group:'org.jglobus'])}
		compile ('org.irods.jargon:jargon-user-tagging:3.3.3-SNAPSHOT')  { excludes ([group:'org.jglobus'])}
		compile ('org.irods.jargon:jargon-workflow:3.3.3-SNAPSHOT')  { excludes ([group:'org.jglobus'])}
		compile ('org.irods.jargon:jargon-ruleservice:3.3.3-SNAPSHOT')  { excludes ([group:'org.jglobus'])}


		provided 'javax.servlet:servlet-api:2.5'
		compile 'org.jsoup:jsoup:0.2.2'

		compile( group: 'log4j', name: 'log4j', version: '1.2.16', export: false )

	}
	plugins {
		runtime ":hibernate:$grailsVersion"
		build ":tomcat:$grailsVersion"
	}
}
