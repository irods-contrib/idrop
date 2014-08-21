package org.irods.jargon.idrop.web.controllers


import grails.converters.JSON
import grails.rest.RestfulController

import javax.servlet.http.Cookie

import org.irods.jargon.core.connection.AuthScheme
import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.connection.IRODSServerProperties
import org.irods.jargon.core.connection.auth.AuthResponse
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.idrop.web.authsession.UserSessionContext
import org.irods.jargon.idrop.web.services.AuthenticationService
import org.irods.jargon.idrop.web.services.EnvironmentServicesService

/**
 * Handle login management
 */
class LoginController extends RestfulController {

	static responseFormats = ['json']

	IRODSAccessObjectFactory irodsAccessObjectFactory
	AuthenticationService authenticationService
	EnvironmentServicesService environmentServicesService

	/**
	 * Before interceptor to add CXRF cookie
	 */
	def beforeInterceptor = [action: this.&generateCookie, only: ['save']]

	private generateCookie() {

		def token = authenticationService.generateXSRFToken()
		session.xsrfToken = token
		Cookie cookie = new Cookie("XSRF-TOKEN",token)
		cookie.httpOnly = false
		cookie.maxAge = (60)
		log.info("adding xsrf token cookie")
		response.addCookie(cookie)
	}

	/**
	 * Processing of POST is a login action
	 * @param command
	 * @return
	 */
	def save(LoginCommand command) {

		if (!command) {
			throw new IllegalArgumentException("null command")
		}

		if (command.hasErrors()) {
			command.password = ""
			log.warn("validation errors:${command}")
			response.status = 400
			render(command as JSON)
			return
		}

		log.info("no validation errors, authenticate")

		// In order to simplify the login process, just try all auth methods
		// and find the first one that succeeds
		
			
		
		def authScheme = AuthScheme.findTypeByString(command.authType)
		
		// Hiding the auth scheme and default port in the login view
		if (authScheme == null) {
			trySchemes = AuthScheme.authSchemeList()
			
			// XXX: resumable exceptions in Groovy?
			for (scheme in trySchemes) {
				try {
					IRODSAccount irodsAccount = IRODSAccount.instance(command.host, command.port ? command.port: 1247, command.userName, command.password, "", command.zone, "", authScheme)
					
					log.info("using authScheme:${authScheme}")		
					AuthResponse authResponse = authenticationService.authenticate(irodsAccount)
			
				} catch (e) {
					log.info("Failed to authenticate with auth scheme ${scheme}")
				}
			}
		}


		log.info("auth successful, saving response in session and returning")
		session.authenticationSession = authResponse
		UserSessionContext userSessionContext = new UserSessionContext()
		userSessionContext.userName = authResponse.authenticatedIRODSAccount.userName
		userSessionContext.zone = authResponse.authenticatedIRODSAccount.zone
		authenticationService.generateXSRFToken()
		log.info("getting irodsServerProperties")
		session.userSessionContext = userSessionContext

		IRODSServerProperties irodsServerProperties = environmentServicesService.getIrodsServerProperties(irodsAccount)

		userSessionContext.defaultStorageResource = irodsAccount.defaultStorageResource
		userSessionContext.serverVersion = irodsServerProperties.relVersion
		render userSessionContext as JSON
	}
}
@grails.validation.Validateable
class LoginCommand {
	String userName
	String password
	int port
	String zone
	String host
	String defaultStorageResource
	String authType
	boolean guestLogin
	boolean usePreset

	static constraints = {
		userName(blank: false)
		password(blank: false)
		defaultStorageResource(nullable:true)
		port(nullable:true)
		authType(blank: false)
	}
}


