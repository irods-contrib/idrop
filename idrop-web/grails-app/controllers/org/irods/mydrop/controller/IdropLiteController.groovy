package org.irods.mydrop.controller 

import grails.converters.*

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.exception.JargonException
import org.irods.jargon.core.exception.JargonRuntimeException
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.pub.UserAO
import org.springframework.security.core.context.SecurityContextHolder
import org.irods.jargon.datautils.datacache.DataCacheService
import org.irods.jargon.datautils.datacache.DataCacheServiceImpl
import grails.converters.JSON
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
		DataCacheService dataCacheService = new DataCacheServiceImpl()
		dataCacheService.irodsAccessObjectFactory = irodsAccessObjectFactory
		dataCacheService.irodsAccount = irodsAccount
		dataCacheService.putStringValueIntoCache(irodsAccount.password, password)
		
		/* set applet operation mode=1 to indicate temporary password is being sent */
		def mode = "1";
		
		log.info "temporary user password is: ${password}"
		IdropLite idropLite = new IdropLite()
		idropLite.appletUrl = "http://localhost:8080/idrop-web/applet"
		idropLite.appletCode = "org.irods.jargon.idrop.lite.iDropLiteApplet"
		idropLite.archive = "idrop-lite-1.0.0-beta2-SNAPSHOT-jar-with-dependencies.jar"
		idropLite.mode = mode
		idropLite.host = irodsAccount.host
		idropLite.port = irodsAccount.port
		idropLite.zone = irodsAccount.zone
		idropLite.user = irodsAccount.userName
		idropLite.password = password
		idropLite.defaultStorageResource = irodsAccount.defaultStorageResource
		idropLite.absolutePath = absPath
		
		render idropLite as JSON
				
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
