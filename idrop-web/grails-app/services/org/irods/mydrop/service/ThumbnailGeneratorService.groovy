package org.irods.mydrop.service

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.datautils.image.ThumbnailService
import org.irods.jargon.datautils.image.ThumbnailServiceImpl
import org.irods.mydrop.config.ServerProperties
import org.irods.mydrop.config.ServerPropertiesCache

class ThumbnailGeneratorService {

	static transactional = false
	static String IMAGE_PROP = "irods_thumbnail"
	private ServerPropertiesCache serverPropertiesCache = new ServerPropertiesCache()
	private String tempDir = null

	public ThumbnailProcessResult getStreamForThumbnailImage(String irodsAbsolutePath, IRODSAccount irodsAccount, IRODSAccessObjectFactory irodsAccessObjectFactory, File tempDir) {

		if (irodsAbsolutePath == null || irodsAbsolutePath.isEmpty()) {
			throw new IllegalArgumentException("null or empty irodsAbsolutePath")
		}

		if (irodsAccount == null) {
			throw new IllegalArgumentException("null irodsAccount")
		}

		if (irodsAccessObjectFactory == null) {
			throw new IllegalArgumentException("null or empty irodsAccessObjectFactory")
		}

		if (tempDir == null) {
			throw new IllegalArgumentException("null tempDir")
		}

		boolean supportsImageMagik = isServerSupportImageMagik(irodsAccount, irodsAccessObjectFactory)

		ThumbnailProcessResult thumbnailProcessResult = null
		ThumbnailService thumbnailService = new ThumbnailServiceImpl(irodsAccessObjectFactory, irodsAccount)

		if  (supportsImageMagik) {
			log.info("cacheProp is true, use thumbnail process on iRODS")
			InputStream thumbnailData = new BufferedInputStream(thumbnailService.retrieveThumbnailByIRODSAbsolutePathViaRule(irodsAbsolutePath))
			thumbnailProcessResult = new ThumbnailProcessResult()
			thumbnailProcessResult.thumbnailStream = thumbnailData
		}  else {
			log.warn("cannot process thumbnail, imagemagik is not supported")
			return null
		}
		return thumbnailProcessResult
	}

	/**
	 * Synchronized check to see if server supports imageMagik
	 * @param irodsAccount
	 * @param IRODSAccessObjectFactory
	 * @return
	 */
	private synchronized boolean isServerSupportImageMagik(IRODSAccount irodsAccount, IRODSAccessObjectFactory irodsAccessObjectFactory) {

		if (irodsAccount == null) {
			throw new IllegalArgumentException("irodsAccount is null")
		}

		if (irodsAccessObjectFactory == null) {
			throw new IllegalArgumentException("null irodsAccessObjectFactory")
		}

		boolean goToIrods = false

		ServerProperties serverProperties =  serverPropertiesCache.getServerProperties(irodsAccount)
		if (serverProperties.properties[IMAGE_PROP] == null) {
			log.info("go to server to get cache")
			ThumbnailService thumbnailService = new ThumbnailServiceImpl(irodsAccessObjectFactory, irodsAccount)
			goToIrods = thumbnailService.isIRODSThumbnailGeneratorAvailable()
			log.info("isIRODSThumbnailGeneratorAvailable? ${goToIrods}")
			if (goToIrods) {
				serverProperties.properties[IMAGE_PROP] = true
			} else {
				serverProperties.properties[IMAGE_PROP] = false
			}
		} else {
			log.info("server properties:${serverProperties}")
			def cacheProp = serverProperties.properties[IMAGE_PROP]
			if (cacheProp) {
				goToIrods = true
			}
		}

		log.info("Derived goToIrods value to determine whether imageMagik is on iRODS = ${goToIrods}")
		return goToIrods
	}
}

public class ThumbnailProcessResult {
	InputStream thumbnailStream
	File cleanupFile

	public void cleanUpIfNeeded() {
		if (cleanupFile) {
			try {
				cleanupFile.delete()
			} catch (Exception e) {
				log.warn("exception closing up cleanup file: ${e}")
			}
		}
	}
}
