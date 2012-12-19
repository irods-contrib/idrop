package org.irods.mydrop.controller

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.utils.MiscIRODSUtils
import org.irods.mydrop.service.StarringService;

class HomeController {

	StarringService starringService
	
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

	def afterInterceptor = { log.debug("closing the session") }

	def index() {
	}

	/**
	 * Link action will use an anonymous login to pre-set the view to a desired path.  This bypasss the filter
	 * 
	 * @return
	 */
	def link() {
		log.info "link()"
		def irodsURIString = params['irodsURI']
		if (irodsURIString == null) {
			def message = message(code:"error.no.uri.provided")
			response.sendError(500,message)
			return
		}

		log.info("irodsURI: ${irodsURIString}")

		// TODO: refactor, add check to see if on same grid, in which case the account should not be changed
		String mungedIRODSURI = irodsURIString.replaceAll(" ", "&&space&&")
		URI irodsURI = new URI(mungedIRODSURI)
		String filePath = irodsURI.getPath()
		log.info("irodsFilePath:${filePath}")
		filePath = filePath.replaceAll("&&space&&", " ")
		log.info("irodsFilePath:${filePath}")
		String zone = MiscIRODSUtils.getZoneInPath(filePath)
		log.info("zone:${zone}")
		IRODSAccount irodsAccount = anonymousIrodsAccountForURIString(mungedIRODSURI)
		session["SPRING_SECURITY_CONTEXT"] = irodsAccount
		/*
		 * Need to figure out how to signal interface to 'reset' to new account and path?  
		 */

		render(view:"link", model:[absPath:filePath])

	}
	
	def starredCollections() {
		log.info "starredCollections()"
		
		def listing = starringService.listStarredCollections(irodsAccount, 0)
		
		if (listing.isEmpty()) {
			render(view:"noInfo")
		} else {
			render(view:"quickViewList",model:[listing:listing])
		}
	}
	
	def starredDataObjects() {
		log.info "starredDataObjects()"
		def listing = starringService.listStarredDataObjects(irodsAccount, 0)
		if (listing.isEmpty()) {
			render(view:"noInfo")
		} else {
			render(view:"quickViewList",model:[listing:listing])
		}
	}

	// FIXME: refactor into jargon-core
	private IRODSAccount anonymousIrodsAccountForURIString(String uriString) {
		// get an anonymous account based on the provided URI
		URI irodsURI = new URI(uriString)
		String filePath = irodsURI.getPath()
		log.info("irodsFilePath:${filePath}")
		String zone = MiscIRODSUtils.getZoneInPath(filePath)
		log.info("zone:${zone}")
		IRODSAccount irodsAccount = IRODSAccount.instanceForAnonymous(irodsURI.getHost(),
				irodsURI.getPort(), "", zone,
				"")
		return irodsAccount
	}
}
