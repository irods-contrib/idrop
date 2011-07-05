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
		DataCacheService dataCacheService = new DataCacheServiceImpl();
		dataCacheService.irodsAccessObjectFactory = irodsAccessObjectFactory
		dataCacheService.irodsAccount = irodsAccount
		dataCacheService.putStringValueIntoCache(irodsAccount.password, password)
		
		/* set applet operation mode to indicate temporary password is being sent */
		def mode = "1";
		
		log.info "temporary user password is: ${password}"
		render(view:"appletLoader", model:[mode:mode, password:password, account:irodsAccount, absPath:absPath])
		
	}
}
