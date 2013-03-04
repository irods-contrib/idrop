package org.irods.mydrop.controller

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.datautils.image.ImageServiceFactory
import org.irods.mydrop.config.*
import org.irods.mydrop.service.ThumbnailGeneratorService
import org.irods.mydrop.service.ThumbnailProcessResult

/**
 * Controller to handle images (including thumbnails and galleries)
 * 
 * @author Mike Conway - DICE (www.irods.org)
 *
 */
class ImageController {

	IRODSAccessObjectFactory irodsAccessObjectFactory
	ImageServiceFactory imageServiceFactory
	ThumbnailGeneratorService thumbnailGeneratorService
	IRODSAccount irodsAccount

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
	 * Given a path to an image in iRODS, generate a thumbnail image and return a stream
	 */
	def generateThumbnail = {

		String absPath = params.absPath
		if (absPath == null || absPath.isEmpty()) {
			log.error "no absPath in request for generateThumbnail()"
			def message = message(code:"error.no.path.provided")
			response.sendError(500,message)
		}

		File tempDir =servletContext.getAttribute("javax.servlet.context.tempdir")

		log.info("looking up image as: ${absPath}")
		ThumbnailProcessResult thumbnailProcessResult = thumbnailGeneratorService.getStreamForThumbnailImage(absPath, irodsAccount, irodsAccessObjectFactory, tempDir)
		if (thumbnailProcessResult == null) {
			log.info("no thumbnail returned, probably not configured on iRODS")
			return
		}
		response.setContentType("image/png")
		response.outputStream << thumbnailProcessResult.thumbnailStream // Performing a binary stream copy
		log.info("stream done, do any necessary cleanup")
		thumbnailProcessResult.cleanUpIfNeeded()
	}
}
