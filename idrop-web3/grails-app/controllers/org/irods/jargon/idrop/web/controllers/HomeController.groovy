package org.irods.jargon.idrop.web.controllers

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.pub.IRODSAccessObjectFactory

class HomeController {

   IRODSAccessObjectFactory irodsAccessObjectFactory

	/**
	 * Interceptor grabs IRODSAccount from the SecurityContextHolder
	 */
	def beforeInterceptor = [action:this.&auth, except:'link']
	IRODSAccount irodsAccount

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
		log.info("index")
		boolean shareSupported = sharingService.isSharingSupported(irodsAccount)
		render(view:"index", model:[shareSupported:shareSupported])
	}
}
