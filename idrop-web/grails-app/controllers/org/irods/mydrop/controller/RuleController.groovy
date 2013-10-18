package org.irods.mydrop.controller

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.mydrop.service.ProfileService;
import org.irods.mydrop.service.RuleProcessingService

class RuleController {
	
	IRODSAccessObjectFactory irodsAccessObjectFactory
	IRODSAccount irodsAccount
	RuleProcessingService ruleProcessingService
	def grailsApplication

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

    def index() {
		
		log.info("index()")
		
		def absPath = params['absPath']
		if (absPath == null) {
			log.error "no absPath in request "
			def message = message(code:"error.no.path.provided")
			response.sendError(500,message)
		}
		
		//def rule = ruleProcessingService.
		
		
		
	}
}
