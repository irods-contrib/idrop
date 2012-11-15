package org.irods.mydrop.controller

import grails.test.mixin.*

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.connection.IRODSServerProperties
import org.irods.jargon.core.pub.CollectionAndDataObjectListAndSearchAO
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.pub.IRODSFileSystem
import org.irods.jargon.core.pub.domain.ObjStat
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry
import org.irods.jargon.testutils.TestingPropertiesHelper
import org.irods.jargon.ticket.Ticket
import org.irods.jargon.ticket.TicketAdminService
import org.irods.jargon.ticket.TicketServiceFactory
import org.irods.mydrop.controller.TicketController
import org.junit.*
import org.mockito.Mockito

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(TicketController)
class TicketControllerTests {

	IRODSAccessObjectFactory irodsAccessObjectFactory
	IRODSAccount irodsAccount
	Properties testingProperties
	TestingPropertiesHelper testingPropertiesHelper
	IRODSFileSystem irodsFileSystem
	TicketServiceFactory ticketServiceFactory

	protected void setUp() {
		super.setUp()
		testingPropertiesHelper = new TestingPropertiesHelper()
		testingProperties = testingPropertiesHelper.getTestProperties()
		irodsAccount = testingPropertiesHelper.buildIRODSAccountFromTestProperties(testingProperties)
		irodsFileSystem = IRODSFileSystem.instance()
		irodsAccessObjectFactory = irodsFileSystem.getIRODSAccessObjectFactory()
		controller.session["SPRING_SECURITY_CONTEXT"] = irodsAccount
	}

	protected void tearDown() {
		super.tearDown()
	}


	void testIndex() {
		def testPath = "/testpath"
		def irodsAccessObjectFactory = Mockito.mock(IRODSAccessObjectFactory.class)
		CollectionAndDataObjectListAndSearchAO collectionListAndSearchAO = Mockito.mock(CollectionAndDataObjectListAndSearchAO.class)
		IRODSServerProperties irodsServerProperties = Mockito.mock(IRODSServerProperties.class)
		Mockito.when(irodsServerProperties.isTheIrodsServerAtLeastAtTheGivenReleaseVersion("rods3.1")).thenReturn(true)
		Mockito.when(collectionListAndSearchAO.getIRODSServerProperties()).thenReturn(irodsServerProperties)

		ObjStat objStat = new ObjStat()
		objStat.setAbsolutePath(testPath)
		objStat.setObjectType(CollectionAndDataObjectListingEntry.ObjectType.COLLECTION)
		Mockito.when(collectionListAndSearchAO.retrieveObjectStatForPath(testPath)).thenReturn(objStat)
		Mockito.when(irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)).thenReturn(collectionListAndSearchAO)
		controller.irodsAccessObjectFactory = irodsAccessObjectFactory

		List<Ticket> tickets = new ArrayList<Ticket>()
		TicketAdminService ticketAdminService = Mockito.mock(TicketAdminService.class)
		Mockito.when(ticketAdminService.listAllTicketsForGivenCollection(testPath, 0)).thenReturn(tickets)

		ticketServiceFactory = Mockito.mock(TicketServiceFactory.class)
		Mockito.when(ticketServiceFactory.instanceTicketAdminService(irodsAccount)).thenReturn(ticketAdminService)

		controller.irodsAccount = irodsAccount
		controller.ticketServiceFactory = ticketServiceFactory
		controller.params.absPath = testPath
		controller.index()

		assert view == "/ticket/ticketDetails"
	}


	void testListTickets() {
		def testPath = "/testpath"
		def irodsAccessObjectFactory = Mockito.mock(IRODSAccessObjectFactory.class)
		CollectionAndDataObjectListAndSearchAO collectionListAndSearchAO = Mockito.mock(CollectionAndDataObjectListAndSearchAO.class)
		IRODSServerProperties irodsServerProperties = Mockito.mock(IRODSServerProperties.class)
		Mockito.when(irodsServerProperties.isTheIrodsServerAtLeastAtTheGivenReleaseVersion("rods3.1")).thenReturn(true)
		Mockito.when(collectionListAndSearchAO.getIRODSServerProperties()).thenReturn(irodsServerProperties)

		ObjStat objStat = new ObjStat()
		objStat.setAbsolutePath(testPath)
		objStat.setObjectType(CollectionAndDataObjectListingEntry.ObjectType.COLLECTION)
		Mockito.when(collectionListAndSearchAO.retrieveObjectStatForPath(testPath)).thenReturn(objStat)
		Mockito.when(irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)).thenReturn(collectionListAndSearchAO)
		controller.irodsAccessObjectFactory = irodsAccessObjectFactory

		List<Ticket> tickets = new ArrayList<Ticket>()
		TicketAdminService ticketAdminService = Mockito.mock(TicketAdminService.class)
		Mockito.when(ticketAdminService.listAllTicketsForGivenCollection(testPath, 0)).thenReturn(tickets)

		ticketServiceFactory = Mockito.mock(TicketServiceFactory.class)
		Mockito.when(ticketServiceFactory.instanceTicketAdminService(irodsAccount)).thenReturn(ticketAdminService)

		controller.irodsAccount = irodsAccount
		controller.ticketServiceFactory = ticketServiceFactory
		controller.params.absPath = testPath
		controller.listTickets()

		assert view == "/ticket/ticketTable"
	}

	void testTicketDetailsDialog() {
		def testPath = "/testpath"
		def create = true
		def irodsAccessObjectFactory = Mockito.mock(IRODSAccessObjectFactory.class)
		CollectionAndDataObjectListAndSearchAO collectionListAndSearchAO = Mockito.mock(CollectionAndDataObjectListAndSearchAO.class)
		IRODSServerProperties irodsServerProperties = Mockito.mock(IRODSServerProperties.class)
		Mockito.when(irodsServerProperties.isTheIrodsServerAtLeastAtTheGivenReleaseVersion("rods3.1")).thenReturn(true)
		Mockito.when(collectionListAndSearchAO.getIRODSServerProperties()).thenReturn(irodsServerProperties)

		ObjStat objStat = new ObjStat()
		objStat.setAbsolutePath(testPath)
		objStat.setObjectType(CollectionAndDataObjectListingEntry.ObjectType.COLLECTION)
		Mockito.when(collectionListAndSearchAO.retrieveObjectStatForPath(testPath)).thenReturn(objStat)
		Mockito.when(irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)).thenReturn(collectionListAndSearchAO)
		controller.irodsAccessObjectFactory = irodsAccessObjectFactory

		Ticket ticket = new Ticket()
		TicketAdminService ticketAdminService = Mockito.mock(TicketAdminService.class)
		Mockito.when(ticketAdminService.getTicketForSpecifiedTicketString(testPath)).thenReturn(ticket)

		ticketServiceFactory = Mockito.mock(TicketServiceFactory.class)
		Mockito.when(ticketServiceFactory.instanceTicketAdminService(irodsAccount)).thenReturn(ticketAdminService)

		controller.irodsAccount = irodsAccount
		controller.ticketServiceFactory = ticketServiceFactory
		controller.params.ticketString = testPath
		controller.params.create = create
		controller.xxxxxticketDetailsDialog()

		assert view == "/ticket/ticketDetailsDialog"
	}

	void testTicketDetailsDialogNoCreate() {
		def testPath = "/testpath"
		def create = false
		def irodsAccessObjectFactory = Mockito.mock(IRODSAccessObjectFactory.class)
		CollectionAndDataObjectListAndSearchAO collectionListAndSearchAO = Mockito.mock(CollectionAndDataObjectListAndSearchAO.class)
		IRODSServerProperties irodsServerProperties = Mockito.mock(IRODSServerProperties.class)
		Mockito.when(irodsServerProperties.isTheIrodsServerAtLeastAtTheGivenReleaseVersion("rods3.1")).thenReturn(true)
		Mockito.when(collectionListAndSearchAO.getIRODSServerProperties()).thenReturn(irodsServerProperties)

		ObjStat objStat = new ObjStat()
		objStat.setAbsolutePath(testPath)
		objStat.setObjectType(CollectionAndDataObjectListingEntry.ObjectType.COLLECTION)
		Mockito.when(collectionListAndSearchAO.retrieveObjectStatForPath(testPath)).thenReturn(objStat)
		Mockito.when(irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)).thenReturn(collectionListAndSearchAO)
		controller.irodsAccessObjectFactory = irodsAccessObjectFactory

		Ticket ticket = new Ticket()
		TicketAdminService ticketAdminService = Mockito.mock(TicketAdminService.class)
		Mockito.when(ticketAdminService.getTicketForSpecifiedTicketString(testPath)).thenReturn(ticket)

		ticketServiceFactory = Mockito.mock(TicketServiceFactory.class)
		Mockito.when(ticketServiceFactory.instanceTicketAdminService(irodsAccount)).thenReturn(ticketAdminService)

		controller.irodsAccount = irodsAccount
		controller.ticketServiceFactory = ticketServiceFactory
		controller.params.ticketString = testPath
		controller.params.create = create
		controller.xxxxxticketDetailsDialog()

		assert view == "/ticket/ticketDetailsDialog"
		assert model.ticket
	}
}