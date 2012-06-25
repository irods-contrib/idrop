package org.irods.mydrop.controller

import org.irods.jargon.core.connection.IRODSAccount

class HomeController {


	/**
	 * Interceptor grabs IRODSAccount from the SecurityContextHolder
	 */
	def beforeInterceptor = [action:this.&auth]
	IRODSAccount irodsAccount

	def auth() {
		if(!session["SPRING_SECURITY_CONTEXT"]) {
			redirect(controller:"login", action:"login")
			return false
		}
		irodsAccount = session["SPRING_SECURITY_CONTEXT"]
	}

	def afterInterceptor = { log.debug("closing the session") }


	def index = {
		log.info ("in home controller index action")
		render(view: "index")
	}

	def showBrowseToolbar = {
		log.info("showBrowseToolbar")
		render(view:"browseToolbar")
	}
}
