package org.irods.mydrop.controller

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.exception.JargonException
import org.irods.jargon.core.exception.OperationNotSupportedByThisServerException
import org.irods.jargon.core.exception.SpecificQueryException
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.utils.MiscIRODSUtils
import org.irods.mydrop.service.SharingService
import org.irods.mydrop.service.StarringService

class HomeController {

	StarringService starringService
	SharingService sharingService
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

		log.info("checking to see if logged in, if already logged in, use that credential, otherwise, do an anonymous login")

		if (!irodsAccount) {
			log.info("I am not logged in, so create an anonymous account")
			IRODSAccount irodsAccount = anonymousIrodsAccountForURIString(mungedIRODSURI)
			session["SPRING_SECURITY_CONTEXT"] = irodsAccount
		}

		render(view:"link", model:[absPath:filePath])

	}

	def starredCollections() {
		log.info "starredCollections()"

<<<<<<< HEAD
		def listing = starringService.listStarredCollections(irodsAccount, 0)

		if (listing.isEmpty()) {
			render(view:"noInfo")
		} else {
			render(view:"quickViewList",model:[listing:listing])
=======
		try {
			def listing = starringService.listStarredCollections(irodsAccount, 0)

			if (listing.isEmpty()) {
				render(view:"noInfo")
			} else {
				render(view:"quickViewList",model:[listing:listing])
			}
		} catch (SpecificQueryException sqe) {
			log.error("error in specific query", sqe)
			render(view:"noSupport")
		} catch (JargonException je) {
			log.error("jargon exception", je)
			response.sendError(500,je.message)
>>>>>>> development
		}
	}

	def starredDataObjects() {
		log.info "starredDataObjects()"
		try {
			def listing = starringService.listStarredDataObjects(irodsAccount, 0)
			if (listing.isEmpty()) {
				render(view:"noInfo")
			} else {
				render(view:"quickViewList",model:[listing:listing])
			}
		} catch (SpecificQueryException sqe) {
			log.error("error in specific query", sqe)
			render(view:"noSupport")
		} catch (JargonException je) {
			log.error("jargon exception", je)
			response.sendError(500,je.message)
		}
	}

	/**
	 * Listing of collections shared by me with others
	 * @return
	 */
	def sharedCollectionsByMe() {
		log.info "sharedCollectionsByMe"

		boolean sharing = sharingService.isSharingSupported(irodsAccount)
		if (!sharing) {
			log.info("no sharing support on this grid")
			render(view:"noSupport")
			return
		}

<<<<<<< HEAD
		/*
		 * is sharing configured? 
		 */
		if (!sharing) {
			log.info("no sharing support on this grid")
			render(view:"noInfo")
			return
		}
=======
>>>>>>> development

		try {
			def listing = sharingService.listCollectionsSharedByMe(irodsAccount);
			if (listing.isEmpty()) {
				render(view:"noInfo")
			} else {
				render(view:"shareQuickViewList",model:[listing:listing])
			}
		} catch (SpecificQueryException sqe) {
			log.error("error in specific query", sqe)
			render(view:"noSupport")
		} catch (OperationNotSupportedByThisServerException sqe) {
			log.error("error in specific query", sqe)
			render(view:"noSupport")
		} catch (JargonException je) {
			log.error("jargon exception", je)
			response.sendError(500,je.message)
		}
	}

	/**
	 * Listing of collections shared by me with others
	 * @return
	 */
	def sharedCollectionsWithMe() {
		log.info "sharedCollectionsByMe"

		boolean sharing = sharingService.isSharingSupported(irodsAccount)
		if (!sharing) {
			log.info("no sharing support on this grid")
			render(view:"noSupport")
			return
		}

		try {
			def listing = sharingService.listCollectionsSharedWithMe(irodsAccount)
			if (listing.isEmpty()) {
				render(view:"noInfo")
			} else {
				render(view:"shareWithMeQuickViewList",model:[listing:listing])
			}
		} catch (SpecificQueryException sqe) {
			log.error("error in specific query", sqe)
			render(view:"noSupport")
		} catch (OperationNotSupportedByThisServerException sqe) {
			log.error("error in specific query", sqe)
			render(view:"noSupport")
		} catch (JargonException je) {
			log.error("jargon exception", je)
			response.sendError(500,je.message)
		}
<<<<<<< HEAD


=======
>>>>>>> development
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
