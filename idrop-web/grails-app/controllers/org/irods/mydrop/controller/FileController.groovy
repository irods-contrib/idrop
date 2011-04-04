package org.irods.mydrop.controller


import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.exception.JargonException
import org.irods.jargon.core.exception.JargonRuntimeException
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.pub.io.IRODSFileFactory
import org.irods.jargon.core.pub.io.IRODSFileInputStream
import org.springframework.security.core.context.SecurityContextHolder

class FileController {

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

	
	def index = { 
		
		// TODO: file not found?
		
		String parseString = "/file/download"
		def parseStringLength = parseString.length()
		String fullPath = params.name
		if (fullPath == null || fullPath.isEmpty()) {
			log.error("null or missing path info")
			throw new JargonException("null or missing path info")
		}
		
		log.info("add path info is ${params.name}")
		def idx = fullPath.indexOf(parseString)
		log.debug("parsing out additional path info")
		if (idx == -1) {
			log.error("unable to parse addl path info for irods absolute path, path info was ${fullPath}")
			throw new JargonException("unable to parse addl path info for irods absolute path")
		}
		
		fullPath = fullPath.substring(idx + parseStringLength)
		log.info("iRODS path for file is: ${fullPath}")

		IRODSFileFactory irodsFileFactory = irodsAccessObjectFactory.getIRODSFileFactory(irodsAccount)
		IRODSFileInputStream irodsFileInputStream = irodsFileFactory.instanceIRODSFileInputStream(fullPath)
		log.info("opened input stream")
		
		response.setContentType("application/octet-stream")
		response.setHeader("Content-disposition", "attachment;filename=${fullPath}")
		
		response.outputStream << irodsFileInputStream // Performing a binary stream copy
		
	}
}
