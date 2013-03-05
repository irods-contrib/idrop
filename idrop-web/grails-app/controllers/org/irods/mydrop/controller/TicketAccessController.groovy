package org.irods.mydrop.controller

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.exception.JargonException
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.pub.io.IRODSFile
import org.irods.jargon.core.pub.io.IRODSFileFactory
import org.irods.jargon.core.utils.MiscIRODSUtils
import org.irods.jargon.ticket.TicketClientOperations
import org.irods.jargon.ticket.TicketServiceFactory
import org.irods.jargon.ticket.io.FileStreamAndInfo
import org.springframework.web.multipart.MultipartFile

class TicketAccessController {

	IRODSAccessObjectFactory irodsAccessObjectFactory
	TicketServiceFactory ticketServiceFactory
	long MAX_UPLOAD = 32 * 1024 * 1024

	/**
	 * Process an actual call to upload data to iRODS as a multi-part file
	 */

	def uploadViaTicket = {
		log.info("uploadViaTicket")

		def ticketString = params['ticketString']
		if (ticketString == null) {
			throw new JargonException("no ticketString passed to the method")
		}

		def irodsURIString = params['irodsURI']
		if (irodsURIString == null) {
			throw new JargonException("no irodsURI parameter passed to the method")
		}

		log.info("ticketString: ${ticketString}")
		log.info("irodsURIString: ${irodsURIString}")

		File tempDir =servletContext.getAttribute("javax.servlet.context.tempdir")
		log.info("tempDir:${tempDir}")

		MultipartFile f = request.getFile('file')
		if (f == null) {
			throw new JargonException("no file parameter passed to the method")
		}

		def name = f.getOriginalFilename()
		log.info("f is ${f}")
		log.info("length of f is ${f.size}")
		log.info("max upload size is ${MAX_UPLOAD}")
		if (f.size > MAX_UPLOAD) {
			log.error("file size is too large, send error message to use bulk upload")
			def message = message(code:"error.use.bulk.upload")
			response.sendError(500,message)
			return
		} else if (f.size == 0) {
			log.error("file is zero length")
			def message = message(code:"error.zero.length.upload")
			response.sendError(500,message)
			return
		}
		log.info("name is : ${name}")

		InputStream fis = null
		log.info("building irodsFile for file name: ${name}")
		try {
			fis = new BufferedInputStream(f.getInputStream())
			// FIXME: formalize (?) this path munging monstrosity
			// get an anonymous account based on the provided URI
			String mungedIRODSURI = irodsURIString.replaceAll(" ", "&&space&&")
			URI irodsURI = new URI(mungedIRODSURI)
			String filePath = irodsURI.getPath()
			log.info("irodsFilePath:${filePath}")
			filePath = filePath.replaceAll("&&space&&", " ")
			log.info("irodsFilePath:${filePath}")
			String zone = MiscIRODSUtils.getZoneInPath(filePath)
			log.info("zone:${zone}")
			IRODSAccount irodsAccount = anonymousIrodsAccountForURIString(mungedIRODSURI)
			IRODSFileFactory irodsFileFactory = irodsAccessObjectFactory.getIRODSFileFactory(irodsAccount)
			IRODSFile targetFile = irodsFileFactory.instanceIRODSFile(filePath, name)
			targetFile.setResource(irodsAccount.defaultStorageResource)

			TicketClientOperations ticketClientOperations = ticketServiceFactory.instanceTicketClientOperations(irodsAccount)

			ticketClientOperations.redeemTicketAndStreamToIRODSCollection(
					ticketString, filePath, name,
					fis, tempDir)


		} catch (Exception e) {
			log.error("exception in upload transfer", e)
			response.sendError(500,e.message)
		} finally {
		}
		render "{\"name\":\"${name}\",\"type\":\"image/jpeg\",\"size\":\"1000\"}"
	}

	/**
	 * Delete the given file or folder
	 */
	def deleteFileOrFolder = {
		log.info("delete file or folder")
		String absPath = params['absPath']
		if (!absPath) {
			log.error "no absPath in request"
			def message = message(code:"error.no.path.provided")
			response.sendError(500,message)
		}

		absPath = absPath.trim()

		log.info("name for delete folder:${absPath}")
		IRODSFileFactory irodsFileFactory = irodsAccessObjectFactory.getIRODSFileFactory(irodsAccount)
		IRODSFile targetFile = irodsFileFactory.instanceIRODSFile(absPath)

		targetFile.deleteWithForceOption()
		log.info("file deleted")
		render targetFile.getParent()
	}

	/**
	 * Use a direct data url to stream back data for a ticket
	 */
	def redeemTicket = {
		log.info("redeemTicket()")

		def ticketString = params['ticketString']
		if (ticketString == null) {
			throw new JargonException("no ticketString passed to the method")
		}

		def irodsURIString = params['irodsURI']
		if (irodsURIString == null) {
			throw new JargonException("no irodsURI parameter passed to the method")
		}

		log.info("ticketString: ${ticketString}")
		log.info("irodsURIString: ${irodsURIString}")

		def useLandingPage = params['landingPage']

		if (useLandingPage) {
			log.info("reroute to landing page")
			redirect(action:"landingPage", params:params)
			return
		}

		// get an anonymous account based on the provided URI
		//URI irodsURI = new URI(URLDecoder.decode(irodsURIString))
		String mungedIRODSURI = irodsURIString.replaceAll(" ", "&&space&&")
		URI irodsURI = new URI(mungedIRODSURI)
		String filePath = irodsURI.getPath()
		log.info("irodsFilePath:${filePath}")
		filePath = filePath.replaceAll("&&space&&", " ")
		String zone = MiscIRODSUtils.getZoneInPath(filePath)
		log.info("zone:${zone}")
		IRODSAccount irodsAccount = IRODSAccount.instanceForAnonymous(irodsURI.getHost(),
				irodsURI.getPort(), "", zone,
				"")

		File tempDir =servletContext.getAttribute("javax.servlet.context.tempdir")
		log.info("temp dir:${tempDir}")

		TicketClientOperations ticketClientOperations = ticketServiceFactory.instanceTicketClientOperations(irodsAccount)
		IRODSFile irodsFile = irodsAccessObjectFactory.getIRODSFileFactory(irodsAccount).instanceIRODSFile(filePath)
		FileStreamAndInfo info = ticketClientOperations.redeemTicketGetDataObjectAndStreamBack(ticketString, irodsFile, tempDir)

		log.info("got input stream, ready to pipe")

		def length = info.length

		log.info("file length = ${length}")
		log.info("opened input stream")

		response.setContentType("application/octet-stream")
		response.setContentLength((int) length)
		response.setHeader("Content-disposition", "attachment;filename=\"${irodsFile.name}\"")

		response.outputStream << info.inputStream // Performing a binary stream copy

	}

	def landingPage = {
		log.info("landingPage()")

		def ticketString = params['ticketString']
		if (ticketString == null) {
			throw new JargonException("no ticketString passed to the method")
		}

		def irodsURIString = params['irodsURI']
		if (irodsURIString == null) {
			throw new JargonException("no irodsURI parameter passed to the method")
		}

		def objectType = params['objectType']
		if (objectType == null) {
			throw new JargonException("no objectType parameter passed to the method")
		}

		def ticketType = params['ticketType']
		if (ticketType == null) {
			throw new JargonException("no ticketType parameter passed to the method")
		}

		log.info("ticketString: ${ticketString}")
		log.info("irodsURIString: ${irodsURIString}")
		log.info("objectType:${objectType}")
		log.info("ticketType:${ticketType}")
		// FIXME: formalize (?) this path munging monstrosity
		// get an anonymous account based on the provided URI
		String mungedIRODSURI = irodsURIString.replaceAll(" ", "&&space&&")
		URI irodsURI = new URI(mungedIRODSURI)
		String filePath = irodsURI.getPath()
		log.info("irodsFilePath:${filePath}")
		filePath = filePath.replaceAll("&&space&&", " ")
		log.info("irodsFilePath:${filePath}")
		String zone = MiscIRODSUtils.getZoneInPath(filePath)
		log.info("zone:${zone}")
		IRODSAccount irodsAccount = anonymousIrodsAccountForURIString(mungedIRODSURI)

		if (objectType == "DATA_OBJECT") {
			render(view:'ticketAccessDataObject', model:[irodsURI:irodsURIString, ticketString:ticketString, filePath:filePath, ticketType:ticketType])
		} else {
			render(view:'ticketAccessCollection', model:[irodsURI:irodsURIString, ticketString:ticketString, filePath:filePath, ticketType:ticketType])
		}

	}

	/**
	 * Prepare a dialog to upload a file into the given collection
	 *
	 */
	def prepareUploadDialog = {
		log.info("prepareUploadDialog")
		def ticketString = params['ticketString']
		if (ticketString == null) {
			throw new JargonException("no ticketString passed to the method")
		}

		def irodsURI = params['irodsURI']
		if (irodsURI == null) {
			throw new JargonException("no irodsURI parameter passed to the method")
		}
		render(view:"uploadToTicketCollection", model:[irodsURI:irodsURI, ticketString:ticketString])
	}

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
