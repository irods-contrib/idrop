package org.irods.mydrop.controller

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.exception.JargonRuntimeException
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.datautils.image.ImageServiceFactory
import org.irods.jargon.datautils.image.ThumbnailService
import org.springframework.security.core.context.SecurityContextHolder
import org.irods.mydrop.config.*

/**
 * Controller to handle images (including thumbnails and galleries)
 * 
 * @author Mike Conway - DICE (www.irods.org)
 *
 */
class ImageController {

	IRODSAccessObjectFactory irodsAccessObjectFactory
	ImageServiceFactory imageServiceFactory
	IRODSAccount irodsAccount
	static String IMAGE_PROP = "irods_thumbnail"

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
		ThumbnailService thumbnailService = imageServiceFactory.instanceThumbnailService(irodsAccount)

		ServerPropertiesCache serverPropertiesCache = session.serverPropertiesCache

		if (!serverPropertiesCache) {
			log.info("creating serverPropertiesCache in session")
			serverPropertiesCache =  new ServerPropertiesCache()
			session.serverPropertiesCache = serverPropertiesCache
		}

		ServerProperties serverProperties = serverPropertiesCache.getServerProperties(irodsAccount)

		def goToIrods = false

		def cacheProp = serverProperties.properties[IMAGE_PROP]
		InputStream thumbnailData

		if (cacheProp != null) {
			log.info("found cacheProp: ${cacheProp}")
			if (cacheProp == "true") {
				goToIrods = true
			} else {
				goToIrods = false
			}
		} else {
			goToIrods = thumbnailService.isIRODSThumbnailGeneratorAvailable()
			log.info("isIRODSThumbnailGeneratorAvailable? ${goToIrods}")
			if (goToIrods) {
				serverProperties.properties[IMAGE_PROP] = "true"
			} else {
				serverProperties.properties[IMAGE_PROP] = "false"
			}
		}

		if  (goToIrods) {
			log.info("cacheProp is true, use thumbnail process on iRODS")
			thumbnailData = new BufferedInputStream(thumbnailService.retrieveThumbnailByIRODSAbsolutePathViaRule(absPath))
			response.setContentType("image/jpg")
			response.outputStream << thumbnailData // Performing a binary stream copy
		} else  {
			log.info("using fallback, cacheProp is false")
			File tempThumbnailFile = thumbnailService.createThumbnailLocallyViaJAI(tempDir, absPath, 300);
			thumbnailData = new BufferedInputStream(new FileInputStream(tempThumbnailFile))
			response.setContentType("image/png")
			response.outputStream << thumbnailData // Performing a binary stream copy
			tempThumbnailFile.delete()
		}

		

	}
}
