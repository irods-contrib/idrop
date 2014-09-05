grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

// uncomment (and adjust settings) to fork the JVM to isolate classpaths
//grails.project.fork = [
//   run: [maxMemory:1024, minMemory:64, debug:false, maxPerm:256]
//]

grails.project.dependency.resolution = {
	// inherit Grails' default dependencies
	inherits("global") {
		// specify dependency exclusions here; for example, uncomment this to disable ehcache:
		// excludes 'ehcache'
	}
	log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
	checksums true // Whether to verify checksums on resolve
	legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

	repositories {
		// uncomment the below to enable remote dependency resolution
		// from public Maven repositories
		mavenLocal()
		mavenCentral()
		mavenRepo "http://snapshots.repository.codehaus.org"
		mavenRepo "http://repository.codehaus.org"
		mavenRepo "http://download.java.net/maven/2/"
		mavenRepo "http://repository.jboss.com/maven2/"
		mavenRepo "http://ci-dev.renci.org/nexus/content/repositories/public"
		mavenRepo "https://raw.github.com/DICE-UNC/DICE-Maven/master/releases"
		mavenRepo "https://raw.github.com/DICE-UNC/DICE-Maven/master/snapshots"
		grailsPlugins()
		grailsHome()
		grailsCentral()
	}
	dependencies {
		// specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
		compile 'commons-io:commons-io:2.1'
		provided 'junit:junit:4.8.1'
		compile ('org.irods.jargon:jargon-core:4.0.3-SNAPSHOT') { excludes ([group:'org.jglobus'])}
		compile ('org.irods.jargon:jargon-data-utils:4.0.3-SNAPSHOT')  { excludes ([group:'org.jglobus'])}
		compile ('org.irods.jargon:jargon-ticket:4.0.3-SNAPSHOT')   { excludes ([group:'org.jglobus'])}
		compile ('org.irods.jargon:jargon-user-profile:4.0.3-SNAPSHOT')  { excludes ([group:'org.jglobus'])}
		compile ('org.irods.jargon:jargon-user-tagging:4.0.3-SNAPSHOT')  { excludes ([group:'org.jglobus'])}
		compile ('org.irods.jargon:virtual-collections:4.0.3-SNAPSHOT')  { excludes ([group:'org.jglobus'])}
		compile ('org.irods.jargon:dot-irods-utilities:4.0.3-SNAPSHOT')  { excludes ([group:'org.jglobus'])}
		compile ('org.irods.jargon:data-profile:4.0.3-SNAPSHOT')  { excludes ([group:'org.jglobus'])}
		runtime 'org.springframework:spring-expression:4.0.5.RELEASE'
		runtime 'org.springframework:spring-aop:4.0.5.RELEASE'
		provided 'javax.servlet:servlet-api:2.5'

		compile( group: 'log4j', name: 'log4j', version: '1.2.16', export: false )
	}


	plugins {
		// plugins for the build system only
		build ':tomcat:7.0.52.1'
		// plugins for the compile step
		compile ':scaffolding:2.1.0'
		compile ':cache:1.1.3'
		compile ':asset-pipeline:1.8.3'

		// plugins needed at runtime but not for compilation
		//runtime ':hibernate4:4.3.5.2' // or ':hibernate:3.6.10.14'
		//runtime ':database-migration:1.4.0'
		//runtime ':jquery:1.11.0.2'

	}
}
