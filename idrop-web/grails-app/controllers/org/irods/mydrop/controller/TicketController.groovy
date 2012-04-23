package org.irods.mydrop.controller

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.exception.JargonException
import org.irods.jargon.core.exception.JargonRuntimeException
import org.irods.jargon.core.pub.CollectionAndDataObjectListAndSearchAO
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.irods.jargon.ticket.*

class TicketController {

	IRODSAccessObjectFactory irodsAccessObjectFactory
	IRODSAccount irodsAccount
	TicketServiceFactory ticketServiceFactory

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
	 * Display the ticket details content for a selected absPath
	 */
	def index = {
		def absPath = params['absPath']
		if (absPath == null) {
			throw new JargonException("no absolute path passed to the method")
		}

		log.info "index for absPath: ${absPath}"

		CollectionAndDataObjectListAndSearchAO collectionAndDataObjectListAndSearchAO = irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)
		TicketAdminService ticketAdminService = ticketServiceFactory.instanceTicketAdminService(irodsAccount)
		def ticketList
		def objStat
		try {
			objStat = collectionAndDataObjectListAndSearchAO.retrieveObjectStatForPath(absPath)
			render(view:"ticketDetails", model:[objStat:objStat])
		} catch (FileNotFoundException fnf) {
			log.error "file not found for given path:${absPath}", fnf
			def message = message(code:"error.no.data.found")
			response.sendError(500,message)
		}
		catch (JargonException je) {
			log.error "exception getting tickets for :${absPath}", je
			response.sendError(500,je.message)
		}
	}

	/**
	 * Display the ticket table content
	 */
	def listTickets = {
		def absPath = params['absPath']
		if (absPath == null) {
			throw new JargonException("no absolute path passed to the method")
		}

		log.info "listTickets for absPath: ${absPath}"

		CollectionAndDataObjectListAndSearchAO collectionAndDataObjectListAndSearchAO = irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)
		TicketAdminService ticketAdminService = ticketServiceFactory.instanceTicketAdminService(irodsAccount)
		def ticketList
		def objStat
		try {
			objStat = collectionAndDataObjectListAndSearchAO.retrieveObjectStatForPath(absPath)

			if (objStat.isSomeTypeOfCollection()) {
				log.info("is a collection...")
				ticketList = ticketAdminService.listAllTicketsForGivenCollection(absPath, 0)
			} else {
				log.info("is a data object...")
				ticketList = ticketAdminService.listAllTicketsForGivenDataObject(absPath, 0)
			}

			render(view:"ticketTable", model:[tickets:ticketList, objStat:objStat])
		} catch (FileNotFoundException fnf) {
			log.error "file not found for given path:${absPath}", fnf
			def message = message(code:"error.no.data.found")
			response.sendError(500,message)
		}
		catch (JargonException je) {
			log.error "exception getting tickets for :${absPath}", je
			response.sendError(500,je.message)
		}
	}
}
