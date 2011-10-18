package org.irods.mydrop.controller

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.exception.JargonRuntimeException
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.pub.io.IRODSFile
import org.irods.jargon.core.utils.LocalFileUtils
import org.irods.jargon.datautils.image.ThumbnailService
import org.irods.jargon.datautils.image.ThumbnailServiceImpl
import org.springframework.security.core.context.SecurityContextHolder

/**
 * Controller to handle images (including thumbnails and galleries)
 * 
 * @author Mike Conway - DICE (www.irods.org)
 *
 */
class ImageController {

	IRODSAccessObjectFactory irodsAccessObjectFactory
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
		log.info("looking up image as: ${absPath}")
		
		
	
		File tempDir =servletContext.getAttribute( "javax.servlet.context.tempdir" );
		log.info "tempdir:${tempDir}"
		
		ThumbnailService thumbnailService = new ThumbnailServiceImpl(irodsAccessObjectFactory, irodsAccount)
		InputStream thumbnailData = new BufferedInputStream(thumbnailService.retrieveThumbnailByIRODSAbsolutePathViaRule(absPath))
		
		response.setContentType("image/jpeg")
		response.outputStream << thumbnailData // Performing a binary stream copy
		
	}

}
