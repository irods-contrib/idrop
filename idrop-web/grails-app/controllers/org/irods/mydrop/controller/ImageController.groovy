package org.irods.mydrop.controller

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.exception.JargonRuntimeException
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.datautils.image.ImageServiceFactory
import org.irods.mydrop.config.*
import org.irods.mydrop.service.ThumbnailGeneratorService
import org.irods.mydrop.service.ThumbnailProcessResult
import org.springframework.security.core.context.SecurityContextHolder

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
		response.setContentType("application/octet-stream")
		response.outputStream << thumbnailProcessResult.thumbnailStream // Performing a binary stream copy
		log.info("stream done, do any necessary cleanup")
		thumbnailProcessResult.cleanUpIfNeeded()
	}
}
