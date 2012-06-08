package org.irods.mydrop.controller


import java.net.MalformedURLException
import java.text.SimpleDateFormat
import org.irods.jargon.ticket.*
import org.irods.jargon.ticket.packinstr.TicketCreateModeEnum
import org.irods.jargon.core.pub.*
import org.irods.jargon.core.pub.domain.ObjStat
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
		if (!cmd.validate()) {
			log.info("errors in page, returning with error info:${cmd}")
			render(view:"ticketPulldown", model:[ticket:cmd])
			return
		}

		log.info("edits pass")

		TicketAdminService ticketAdminService = ticketServiceFactory.instanceTicketAdminService(irodsAccount)
		Ticket ticket
		def locale =  org.springframework.web.servlet.support.RequestContextUtils.getLocale(request)
		if (cmd.create) {
			ticket = ticketFromTicketCommand(cmd, locale)
			log.info("built ticket to add:${ticket}")
			ticket = ticketAdminService.createTicketFromTicketObject(ticket)
			log.info("created ticket:${ticket}")
		} else {
			log.info "updating ticket info for command...looking up the ticket based on the ticket string"
			ticket = ticketFromTicketCommand(cmd, locale)
			ticket = ticketAdminService.compareGivenTicketToActualAndUpdateAsNeeded(ticket)
			log.info("ticket after update:${ticket}")
		}
		redirect(action:ticketPulldown, params:[ticketString:ticket.ticketString, absPath:ticket.irodsAbsolutePath])
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

		def locale =  org.springframework.web.servlet.support.RequestContextUtils.getLocale(request)
		log.info("locale is: ${locale}")
		TicketDistributionContext ticketDistributionContext

		try {
			ticketDistributionContext = buildTicketDistributionContext()
		} catch (Exception e) {
			log.error("exception building ticketDistributionContext", e)
			def message = message(code:"error.invalid.ticket.url")
			response.sendError(500,message)
			return
		}

		TicketAdminService ticketAdminService = ticketServiceFactory.instanceTicketAdminService(irodsAccount)
		TicketDistributionService ticketDistributionService = ticketServiceFactory.instanceTicketDistributionService(irodsAccount, ticketDistributionContext)
		def ticket
		def ticketDistribution
		try {
			ticket = ticketAdminService.getTicketForSpecifiedTicketString(ticketString)
			log.info("got ticket:${ticket}")
			ticketDistribution = ticketDistributionService.getTicketDistributionForTicket(ticket)
			log.info("got ticket distribution: ${ticketDistribution}")
			TicketCommand ticketCommand = ticketCommandFromData(ticket, ticketDistribution, locale)
			ticketCommand.isDataObject = (ticket.getObjectType() == Ticket.TicketObjectType.DATA_OBJECT)
			ticketCommand.isDialog = false
			ticketCommand.create = false
			render(view:"ticketPulldown", model:[ticket:ticketCommand])
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

		def irodsAbsolutePath = params['irodsAbsolutePath']
		if (irodsAbsolutePath == null) {
			throw new JargonException("no irodsAbsolutePath parameter passed to the method")
		}

		TicketCommand ticketCommand
		TicketDistribution ticketDistribution = new TicketDistribution()

		TicketDistributionContext ticketDistributionContext
		def locale =  org.springframework.web.servlet.support.RequestContextUtils.getLocale(request)

		try {
			ticketDistributionContext = buildTicketDistributionContext()
		} catch (Exception e) {
			log.error("exception building ticketDistributionContext", e)
			def message = message(code:"error.invalid.ticket.url")
			response.sendError(500,message)
			return
		}

		TicketDistributionService ticketDistributionService = ticketServiceFactory.instanceTicketDistributionService(irodsAccount, ticketDistributionContext)

		log.info "ticketDetailsDialog for ticketString: ${ticketString} with create:${create}"
		try {
			if (create) {
				ticketCommand = new TicketCommand()
				ticketCommand.create = create
				ticketCommand.isDialog = true
				ticketCommand.ownerName = irodsAccount.userName
				ticketCommand.ownerZone = irodsAccount.zone
				ticketCommand.irodsAbsolutePath = irodsAbsolutePath
				ticketCommand.type = "READ"

				log.info "checking object type for path: ${irodsAbsolutePath}"

				CollectionAndDataObjectListAndSearchAO listAndSearchAO = irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)
				try {
					ObjStat objStat = listAndSearchAO.retrieveObjectStatForPath(irodsAbsolutePath)
					if (objStat.isSomeTypeOfCollection()) {
						ticketCommand.isDataObject = false
					} else {
						ticketCommand.isDataObject = true
					}
				} catch (DataNotFoundException dnf) {
					def message = message(code:"error.no.data.found")
					response.sendError(500,message)
					return
				}
				render(view:"ticketPulldown", model:[ticket:ticketCommand])
				return
			} else {
				TicketAdminService ticketAdminService = ticketServiceFactory.instanceTicketAdminService(irodsAccount)
				Ticket ticket = ticketAdminService.getTicketForSpecifiedTicketString(ticketString)
				ticketDistribution = ticketDistributionService.getTicketDistributionForTicket(ticket)
				ticketCommand = ticketCommandFromData(ticket, ticketDistribution, locale)
			}

			render(view:"ticketPulldown", model:[ticket:ticketCommand])
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

	private Ticket ticketFromTicketCommand(TicketCommand ticketCommand, Locale locale) {
		Ticket ticket = new Ticket()
		log.info("ticket command expire time:${ticketCommand.expireTime}")


		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", locale)

		if (ticketCommand.expireTime) {
			ticket.setExpireTime(formatter.parse(ticketCommand.expireTime))
			//log.info(*"parsed date and set in ticket:${ticket.expireTime}")
		}

		ticket.setIrodsAbsolutePath(ticketCommand.irodsAbsolutePath)
		ticket.setTicketString(ticketCommand.ticketString)

		if (ticketCommand.type == 'READ') {
			ticket.setType(TicketCreateModeEnum.READ)
		} else {
			ticket.setType(TicketCreateModeEnum.WRITE)
		}

		ticket.setUsesLimit(ticketCommand.usesLimit)
		ticket.setWriteByteLimit(ticketCommand.writeByteLimit)
		ticket.setWriteFileLimit(ticketCommand.writeFileLimit)

		log.info("built ticket data from command:${ticket}")
		return ticket
	}

	private TicketCommand ticketCommandFromData(Ticket ticket, TicketDistribution ticketDistribution, Locale locale) {
		TicketCommand ticketCommand = new TicketCommand()
		ticketCommand.create = false
		ticketCommand.ticketString = ticket.ticketString

		if (ticket.type == TicketCreateModeEnum.READ) {
			ticketCommand.type = 'READ'
		} else {
			ticketCommand.type='WRITE'
		}

		ticketCommand.ownerName = ticket.ownerName
		ticketCommand.ownerZone = ticket.ownerZone
		ticketCommand.usesCount =ticket.usesCount
		ticketCommand.usesLimit = ticket.usesLimit
		ticketCommand.writeFileCount = ticket.writeFileCount
		ticketCommand.writeFileLimit = ticket.writeFileLimit
		ticketCommand.writeByteCount = ticket.writeByteCount
		ticketCommand.writeByteLimit = ticket.writeByteLimit

		if (ticket.expireTime) {
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", locale)
			ticketCommand.expireTime  = formatter.format(ticket.expireTime)
		} else {
			ticketCommand.expireTime = ""
		}

		ticketCommand.irodsAbsolutePath = ticket.irodsAbsolutePath
		ticketCommand.showLandingPage = false
		ticketCommand.ticketURL = ticketDistribution.ticketURL
		ticketCommand.ticketURLWithLandingPage = ticketDistribution.ticketURLWithLandingPage
		return ticketCommand
	}

	/**
	 * Build a ticket distribution context object that describes how URL's should be built for this server
	 * @return
	 */
	private TicketDistributionContext buildTicketDistributionContext() {
		TicketDistributionContext ticketDistributionContext = new TicketDistributionContext()
		String grailsServerURL =  grailsApplication.config.grails.serverURL
		log.info("server URL for context: ${grailsServerURL}")

		try {
			URL url = new URL(grailsServerURL)
			ticketDistributionContext.host = url.host
			ticketDistributionContext.port = url.port
			ticketDistributionContext.context = url.path + "/ticketAccess/redeemTicket"
			if (url.protocol == "https") {
				ticketDistributionContext.ssl = true
			}

			log.info("ticketDistributionContext:${ticketDistributionContext}")
			return ticketDistributionContext
		} catch (MalformedURLException e) {
			throw new JargonException(
			"malformed URL for ticketDistribution, probably a malformed ticketDistributionContext")
		}
	}
}
class TicketCommand {
	boolean create
	boolean isDataObject
	boolean isDialog
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
	String expireTime
	String irodsAbsolutePath
	boolean showLandingPage
	String ticketURL
	String ticketURLWithLandingPage

	static constraints = {
		type(blank:false, inList:["READ", "WRITE"])
		irodsAbsolutePath(blank:false)
		usesLimit( min:0, max:Integer.MAX_VALUE)
		writeFileLimit(min:0, max:Integer.MAX_VALUE)
		writeByteLimit( min:0L, max:Long.MAX_VALUE)
	}
}