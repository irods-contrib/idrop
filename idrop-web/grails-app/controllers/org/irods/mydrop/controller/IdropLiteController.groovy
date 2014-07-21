package org.irods.mydrop.controller

import grails.converters.*

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.exception.JargonException
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.pub.UserAO
import org.irods.jargon.datautils.datacache.DataCacheServiceFactory
import org.irods.jargon.datautils.datacache.DataCacheServiceFactoryImpl
import org.irods.jargon.datautils.shoppingcart.FileShoppingCart
import org.irods.jargon.datautils.shoppingcart.ShoppingCartService
import org.irods.jargon.datautils.shoppingcart.ShoppingCartServiceImpl
import org.irods.mydrop.service.ShoppingCartSessionService
class IdropLiteController {

	IRODSAccessObjectFactory irodsAccessObjectFactory
	IRODSAccount irodsAccount
	ShoppingCartSessionService shoppingCartSessionService

	/**
	 * Interceptor grabs IRODSAccount from the SecurityContextHolder
	 */
	def beforeInterceptor = [action:this.&auth]

	def auth() {
		if(!session["SPRING_SECURITY_CONTEXT"]) {
			redirect(controller:"login", action:"login")
			return false
		}
		irodsAccount = session["SPRING_SECURITY_CONTEXT"]
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
			String shoppingCartFile = ""

			try {
				shoppingCartFile = shoppingCartService.serializeShoppingCartAsLoggedInUser(fileShoppingCart, key)
			} catch (Exception e) {
				if (e.message.indexOf("error creating") > -1) {
					log.error "no default resource found for copy operation"
					def message = message(code:"message.no.resource")
					response.sendError(500,message)
					return
				} else {
					log.error "error serializing shopping cart"
					def message = message(e.message)
					response.sendError(500,e.message)
					return
				}
			}
			log.info("cart serialized to file:${shoppingCartFile}")
		}

		// now generate the temp password and the applet tag

		def useThisWar = grailsApplication.config.idrop.config.idrop.lite.use.applet.dir
		def iDropLiteJar = grailsApplication.config.idrop.config.idrop.lite.applet.jar

		if (!iDropLiteJar) {
			log.error("no idrop lite jar specified in config, please add the idrop.config.idrop.lite.applet.jar value to the config.groovy or external configuaration")
			throw new JargonException("unable to find idrop lite jar in config.groovy")
		}

		String appletUrl = ""
		if (useThisWar) {
			log.info("will use the applet dir here to download idrop lite")
			String scheme = request.scheme
			String serverName = request.serverName
			int serverPort = request.serverPort
			String contextPath =request.contextPath
			// Reconstruct original requesting URL
			appletUrl = scheme+"://"+serverName+":"+serverPort+contextPath + "/applet"
		} else {
			log.info("looking for an explicitly defined url for applet codebase...")
			appletUrl = grailsApplication.config.idrop.config.idrop.lite.codebase
			if (!appletUrl) {
				log.error("unable to find the applet uri in config groovy, please configure a idrop.config.idrop.lite.codebase property")
				throw new JargonException("unable to find applet uri in config.groovy")
			}
		}

		log.info("computed applet url:${appletUrl}")

		UserAO userAO = irodsAccessObjectFactory.getUserAO(irodsAccount)
		def password = userAO.getTemporaryPasswordForConnectedUser()

		String scheme = request.scheme
		String serverName = request.serverName
		int serverPort = request.serverPort
		String contextPath =request.contextPath

		// Reconstruct original requesting URL
		//String appletUrl = scheme+"://"+serverName+":"+serverPort+contextPath + "/applet"
		//String appletUrl = "http://iren-web.renci.org/idrop-web/applet"

		/* set applet operation mode=2 to indicate temporary password is being sent */
		def mode = "2"

		log.info "temporary user password is: ${password}"
		IdropLite idropLite = new IdropLite()
		idropLite.appletUrl = appletUrl
		idropLite.appletCode = "org.irods.jargon.idrop.lite.iDropLiteApplet"
		idropLite.archive = iDropLiteJar
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

	/**
	 * Load the applet in 'bulk upload' mode
	 */
	def appletLoader = {

		def absPath = params['absPath']
		if (absPath == null) {
			throw new JargonException("no absolute path passed to the method")
		}

		UserAO userAO = irodsAccessObjectFactory.getUserAO(irodsAccount)
		def password = userAO.getTemporaryPasswordForConnectedUser()

		def useThisWar = grailsApplication.config.idrop.config.idrop.lite.use.applet.dir
		def iDropLiteJar = grailsApplication.config.idrop.config.idrop.lite.applet.jar

		if (!iDropLiteJar) {
			log.error("no idrop lite jar specified in config, please add the idrop.config.idrop.lite.applet.jar value to the config.groovy or external configuaration")
			throw new JargonException("unable to find idrop lite jar in config.groovy")
		}

		String appletUrl = ""
		if (useThisWar) {
			log.info("will use the applet dir here to download idrop lite")
			String scheme = request.scheme
			String serverName = request.serverName
			int serverPort = request.serverPort
			String contextPath =request.contextPath
			// Reconstruct original requesting URL
			appletUrl = scheme+"://"+serverName+":"+serverPort+contextPath + "/applet"
		} else {
			log.info("looking for an explicitly defined url for applet codebase...")
			appletUrl = grailsApplication.config.idrop.config.idrop.lite.codebase
			if (!appletUrl) {
				log.error("unable to find the applet uri in config groovy, please configure a idrop.config.idrop.lite.codebase property")
				throw new JargonException("unable to find applet uri in config.groovy")
			}
		}

		log.info("computed applet url:${appletUrl}")

		/* set applet operation mode=2 to indicate temporary password is being sent */
		def mode = "2"

		log.info "temporary user password is: ${password}"
		IdropLite idropLite = new IdropLite()
		idropLite.appletUrl = appletUrl
		idropLite.appletCode = "org.irods.jargon.idrop.lite.iDropLiteApplet"
		idropLite.archive =iDropLiteJar
		idropLite.mode =  mode
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

	/**
	 * Load the applet in 'local/irods' mode
	 */
	def localIrodsTreeViewAppletLoader = {

		def absPath = params['absPath']
		if (absPath == null) {
			throw new JargonException("no absolute path passed to the method")
		}

		UserAO userAO = irodsAccessObjectFactory.getUserAO(irodsAccount)
		def password = userAO.getTemporaryPasswordForConnectedUser()

		def useThisWar = grailsApplication.config.idrop.config.idrop.lite.use.applet.dir
		def iDropLiteJar = grailsApplication.config.idrop.config.idrop.lite.applet.jar

		if (!iDropLiteJar) {
			log.error("no idrop lite jar specified in config, please add the idrop.config.idrop.lite.applet.jar value to the config.groovy or external configuaration")
			throw new JargonException("unable to find idrop lite jar in config.groovy")
		}

		String appletUrl = ""
		if (useThisWar) {
			log.info("will use the applet dir here to download idrop lite")
			String scheme = request.scheme
			String serverName = request.serverName
			int serverPort = request.serverPort
			String contextPath =request.contextPath
			// Reconstruct original requesting URL
			appletUrl = scheme+"://"+serverName+":"+serverPort+contextPath + "/applet"
		} else {
			log.info("looking for an explicitly defined url for applet codebase...")
			appletUrl = grailsApplication.config.idrop.config.idrop.lite.codebase
			if (!appletUrl) {
				log.error("unable to find the applet uri in config groovy, please configure a idrop.config.idrop.lite.codebase property")
				throw new JargonException("unable to find applet uri in config.groovy")
			}
		}

		log.info("computed applet url:${appletUrl}")

		/* set applet operation mode=2 to indicate temporary password is being sent */
		def mode = "2"

		log.info "temporary user password is: ${password}"
		IdropLite idropLite = new IdropLite()
		idropLite.appletUrl = appletUrl
		idropLite.appletCode = "org.irods.jargon.idrop.lite.iDropLiteApplet"
		idropLite.archive =iDropLiteJar
		idropLite.mode =  mode
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
