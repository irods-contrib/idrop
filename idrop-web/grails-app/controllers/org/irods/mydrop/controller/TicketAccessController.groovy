package org.irods.mydrop.controller

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.exception.JargonException
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.pub.io.IRODSFile
import org.irods.jargon.core.utils.MiscIRODSUtils
import org.irods.jargon.ticket.TicketClientOperations
import org.irods.jargon.ticket.TicketServiceFactory
import org.irods.jargon.ticket.io.FileStreamAndInfo

class TicketAccessController {

	IRODSAccessObjectFactory irodsAccessObjectFactory
	TicketServiceFactory ticketServiceFactory

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
		URI irodsURI = new URI(irodsURIString)
		String filePath = irodsURI.getPath()
		log.info("irodsFilePath:${filePath}")
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
		render(view:'ticketAccessCollection', model:[irodsURI:irodsURI, ticketString:ticketString])
	}
}
