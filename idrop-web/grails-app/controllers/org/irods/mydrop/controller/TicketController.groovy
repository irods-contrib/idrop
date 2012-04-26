package org.irods.mydrop.controller


import org.irods.jargon.ticket.*
import org.irods.jargon.ticket.packinstr.TicketCreateModeEnum
import org.irods.jargon.core.pub.*
import org.irods.jargon.core.connection.*
import org.irods.jargon.core.exception.*
import org.springframework.security.core.context.SecurityContextHolder

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
		} catch (JargonException je) {
			log.error "exception getting tickets for :${absPath}", je
			response.sendError(500,je.message)
		}
	}

	def update(TicketCommand cmd)   {

		log.info "update()"
		log.info "cmd: ${cmd}"

		/**
		 * If there is an error send back the view for redisplay with error messages
		 */
		if (cmd.hasErrors()) {
			log.info "errors occured build error messages"
			def errorMessage = message(code:"error.data.error")

			cmd.errors.allErrors.each() {
				log.info "error identified in validation: ${it}"
				errors.add(message(error:it))
			}
		}

		log.info("edits pass")

		TicketAdminService ticketAdminService = ticketServiceFactory.instanceTicketAdminService(irodsAccount)

		if (cmd.create) {
			Ticket ticket = new Ticket()
			ticket.setExpireTime(cmd.expireTime)
			ticket.setIrodsAbsolutePath(cmd.irodsAbsolutePath)
			ticket.setTicketString(cmd.ticketString)

			if (cmd.type == 'READ') {
				ticket.setType(TicketCreateModeEnum.TICKET_CREATE_READ)
			} else {
				ticket.setType(TicketCreateModeEnum.TICKET_CREATE_WRITE)
			}

			ticket.setUsesLimit(cmd.usesLimit)
			ticket.setWriteByteLimit(cmd.writeByteLimit)
			ticket.setWriteFileLimit(cmd.writeFileLimit)

			log.info("built ticket to add:${ticket}")

			Ticket createdTicket = ticketAdminService.createTicketFromTicketObject(ticket)
			log.info("created ticket:${createdTicket}")

			render "OK"
		}
	}

	/**
	 * Display a ticket pulldown meant for the ticket summary table
	 */
	def ticketPulldown = {

		log.info "ticketPulldown()"

		def ticketString = params['ticketString']
		if (ticketString == null) {
			throw new JargonException("no ticketString passed to the method")
		}

		def absPath = params['absPath']
		if (absPath == null) {
			throw new JargonException("no absPath parameter passed to the method")
		}

		log.info("ticketString: ${ticketString}")
		log.info("absPath: ${absPath}")

		TicketDistributionContext ticketDistributionContext = new TicketDistributionContext()
		String grailsServerURL =  grailsApplication.config.grails.serverURL
		log.info("server URL for context: ${grailsServerURL}")

		try {
			URL url = new URL(grailsServerURL)
			ticketDistributionContext.host = url.host
			ticketDistributionContext.port = url.port
			ticketDistributionContext.context = url.path + "/ticket/redeemTicket"
			if (url.protocol == "https") {
				ticketDistributionContext.ssl = true
			}

			log.info("ticketDistributionContext:${ticketDistributionContext}")
		} catch (MalformedURLException e) {
			log.error("malformed url from:${sb.toString()}", e)
			throw new JargonException(
			"malformed URL for ticketDistribution, probably a malformed ticketDistributionContext")
			def message = message(code:"error.invalid.ticket.url")
			response.sendError(500,message)
		}

		//ticketDistributionContext.setHost();

		TicketAdminService ticketAdminService = ticketServiceFactory.instanceTicketAdminService(irodsAccount)
		TicketDistributionService ticketDistributionService = ticketServiceFactory.instanceTicketDistributionService(irodsAccount, ticketDistributionContext)
		def ticket
		def ticketDistribution
		try {
			ticket = ticketAdminService.getTicketForSpecifiedTicketString(ticketString)
			log.info("got ticket:${ticket}")
			ticketDistribution = ticketDistributionService.getTicketDistributionForTicket(ticket)
			log.info("got ticket distribution: ${ticketDistribution}")
			render(view:"ticketPulldown", model:[ticket:ticket, ticketDistribution:ticketDistribution])
		} catch (DataNotFoundException dnf) {
			log.error "ticket not found for given ticketString:${ticketString}", fnf
			def message = message(code:"error.no.ticket.found")
			response.sendError(500,message)
		}
	}

	/**
	 * Display the ticket table content
	 */
	def ticketDetailsDialog = {
		def ticketString = params['ticketString']
		if (ticketString == null) {
			throw new JargonException("no ticketString passed to the method")
		}

		def create = params['create']
		if (create == null) {
			throw new JargonException("no create parameter passed to the method")
		}

		def absPath = params['irodsAbsolutePath']
		if (create == null) {
			throw new JargonException("no irodsAbsolutePath parameter passed to the method")
		}

		def ticketCommand

		log.info "ticketDetailsDialog for ticketString: ${ticketString} with create:${create}"
		try {
			if (create) {
				ticketCommand = new TicketCommand()
				ticketCommand.create = create
				ticketCommand.ownerName = irodsAccount.userName
				ticketCommand.ownerZone = irodsAccount.zone
				ticketCommand.irodsAbsolutePath = absPath
				ticketCommand.type = "READ"
			} else {
				TicketAdminService ticketAdminService = ticketServiceFactory.instanceTicketAdminService(irodsAccount)
				def ticket = ticketAdminService.getTicketForSpecifiedTicketString(ticketString)
				ticketCommand = new TicketCommand()
				ticketCommand.create = create
				ticketCommand.ticketString = ticket.ticketString

				if (ticket.type == TicketCreateModeEnum.TICKET_CREATE_READ) {
					ticketCommand.type = 'READ'
				} else {
					ticketCommand.type='WRITE'
				}

				ticketCommand.type = ticket.type
				ticketCommand.ownerName = ticket.ownerName
				ticketCommand.ownerZone = ticket.ownerZone
				ticketCommand.usesCount =ticket.usesCount
				ticketCommand.usesLimit = ticket.usesLimit
				ticketCommand.writeFileCount = ticket.writeFileCount
				ticketCommand.writeFileLimit = ticket.writeFileLimit
				ticketCommand.writeByteCount = ticket.writeByteCount
				ticketCommand.writeByteLimit = ticket.writeByteLimit
				ticketCommand.expireTime = ticket.expireTime
				ticketCommand.irodsAbsolutePath = ticket.irodsAbsolutePath
			}
			render(view:"ticketDetailsDialog", model:[ticket:ticketCommand, create:create])
		} catch (FileNotFoundException fnf) {
			log.error "ticket not found for given ticketString:${ticketString}", fnf
			def message = message(code:"error.no.data.found")
			response.sendError(500,message)
		}
		catch (JargonException je) {
			log.error "exception getting ticket for :${ticketString}", je
			response.sendError(500,je.message)
		}
	}
}
class TicketCommand {
	boolean create
	String ticketString
	String type
	String ownerName
	String ownerZone
	int usesCount
	int usesLimit
	int writeFileCount
	int writeFileLimit
	long writeByteCount
	long writeByteLimit
	Date expireTime
	String irodsAbsolutePath
	static constraints = {
		type(blank:false, inList:["READ", "WRITE"])
		irodsAbsolutePath(blank:false)
	}
}