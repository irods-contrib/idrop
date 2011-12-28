package org.irods.mydrop.controller

import grails.converters.*

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.exception.JargonException
import org.irods.jargon.core.exception.JargonRuntimeException
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.pub.UserAO
import org.irods.jargon.datautils.datacache.DataCacheServiceFactory
import org.irods.jargon.datautils.datacache.DataCacheServiceFactoryImpl
import org.irods.jargon.datautils.shoppingcart.FileShoppingCart
import org.irods.jargon.datautils.shoppingcart.ShoppingCartService
import org.irods.jargon.datautils.shoppingcart.ShoppingCartServiceImpl
import org.irods.mydrop.service.ShoppingCartSessionService
import org.springframework.security.core.context.SecurityContextHolder
class IdropLiteController {

	IRODSAccessObjectFactory irodsAccessObjectFactory
	IRODSAccount irodsAccount
	ShoppingCartSessionService shoppingCartSessionService

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

	/**
	 * Launch iDrop Lite in shopping cart mode
	 */
	def shoppingCartAppletLoader = {
		log.info("shoppingCartAppletLoader()")

		// TODO: add param to do the proxy user switch (gen password on behalf of a user)
		DataCacheServiceFactory dataCacheServiceFactory = new DataCacheServiceFactoryImpl(
				irodsAccessObjectFactory)

		ShoppingCartService shoppingCartService = new ShoppingCartServiceImpl(
				irodsAccessObjectFactory, irodsAccount,
				dataCacheServiceFactory)
		log.info("getting shopping cart from session")
		FileShoppingCart fileShoppingCart = shoppingCartSessionService.getCartFromSession()
		log.info("shopping cart:${fileShoppingCart}")
		String key = ""
		if (fileShoppingCart) {
			key = String.valueOf(System.currentTimeMillis())
			log.info("key:${key}")
			String shoppingCartFile = shoppingCartService
					.serializeShoppingCartAsLoggedInUser(fileShoppingCart, key)
			log.info("cart serialized to file:${shoppingCartFile}")
		}

		// now generate the temp password and the applet tag

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
		idropLite.absolutePath = ""
		idropLite.key = key

		def jsonResult = ["appletUrl" : idropLite.appletUrl, "appletCode": idropLite.appletCode, "archive": idropLite.archive, "mode":idropLite.mode,
					"host":idropLite.host, "port":idropLite.port, "zone":idropLite.zone, "user":idropLite.user, "password":idropLite.password, "defaultStorageResource":idropLite.defaultStorageResource,
					"absolutePath":idropLite.absolutePath, "key":key]

		render jsonResult as JSON
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
					"absolutePath":idropLite.absolutePath, "key":idropLite.key]

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
	String key
}
