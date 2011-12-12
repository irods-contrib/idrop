package org.irods.mydrop.controller

import grails.converters.*

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.exception.JargonException
import org.irods.jargon.core.exception.JargonRuntimeException
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.pub.UserAO
import org.springframework.security.core.context.SecurityContextHolder
class IdropLiteController {

	IRODSAccessObjectFactory irodsAccessObjectFactory
	IRODSAccount irodsAccount

	/**
	 * Interceptor grabs IRODSAccount from the SecurityContextHolder
	 */
	def beforeInterceptor = {
		def irodsAuthentication = SecurityContextHolder.getContext().authentication

		if (irodsAuthentication == null) {
			throw new JargonRuntimeException("no irodsAuthentication in security context!")
		}

		irodsAccount = irodsAuthentication.irodsAccount
		log.debug("retrieved account for request: ${irodsAccount}")
	}

	def afterInterceptor = {
		log.debug("closing the session")
		irodsAccessObjectFactory.closeSession()
	}

	def appletLoader = {

		def absPath = params['absPath']
		if (absPath == null) {
			throw new JargonException("no absolute path passed to the method")
		}

		UserAO userAO = irodsAccessObjectFactory.getUserAO(irodsAccount)
		def password = userAO.getTemporaryPasswordForConnectedUser()

		String scheme = request.scheme
		String serverName = request.serverName
		int serverPort = request.serverPort
		String contextPath =request.contextPath

		// Reconstruct original requesting URL
		//String appletUrl = scheme+"://"+serverName+":"+serverPort+contextPath + "/applet"
		String appletUrl = "http://iren-web.renci.org/idrop-web/applet"

		/* set applet operation mode=2 to indicate temporary password is being sent */
		def mode = "2"

		log.info "temporary user password is: ${password}"
		IdropLite idropLite = new IdropLite()
		idropLite.appletUrl = appletUrl
		idropLite.appletCode = "org.irods.jargon.idrop.lite.iDropLiteApplet"
		idropLite.archive = "idrop-lite-1.0.0-beta3-SNAPSHOT-jar-with-dependencies.jar"
		idropLite.mode = mode
		idropLite.host = irodsAccount.host
		idropLite.port = irodsAccount.port
		idropLite.zone = irodsAccount.zone
		idropLite.user = irodsAccount.userName
		idropLite.password = password
		idropLite.defaultStorageResource = irodsAccount.defaultStorageResource
		idropLite.absolutePath = absPath

		def jsonResult = ["appletUrl" : idropLite.appletUrl, "appletCode": idropLite.appletCode, "archive": idropLite.archive, "mode":idropLite.mode,
					"host":idropLite.host, "port":idropLite.port, "zone":idropLite.zone, "user":idropLite.user, "password":idropLite.password, "defaultStorageResource":idropLite.defaultStorageResource,
					"absolutePath":idropLite.absolutePath]

		render jsonResult as JSON

	}
}

class IdropLite {
	String appletUrl
	String archive
	String appletCode
	String mode
	String host
	String port
	String zone
	String user
	String password
	String defaultStorageResource
	String absolutePath
}
