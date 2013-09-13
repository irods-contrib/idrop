package org.irods.jargon.idrop.web.controllers


import idrop.web3.AuthenticationService
import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.pub.IRODSAccessObjectFactory



class LoginController {
	
	
	IRODSAccessObjectFactory irodsAccessObjectFactory
	IRODSAccount irodsAccount
	AuthenticationService authentiationService
	
	/**
	 * Interceptor grabs IRODSAccount from the SecurityContextHolder
	 */
	def beforeInterceptor = [action:this.&auth, except:'login']

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

    def login() {
		
		log.info("login()");
		
		def host = params.host
		def port = params.port
		def zone = params.zone
		def user = params.username
		def password = params.password
		def authType = params.authtype
		
		
		
	}
}
