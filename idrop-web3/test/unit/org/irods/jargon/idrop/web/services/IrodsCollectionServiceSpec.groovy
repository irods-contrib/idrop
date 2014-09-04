package org.irods.jargon.idrop.web.services

import grails.test.mixin.*

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.pub.CollectionAndDataObjectListAndSearchAO
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.pub.io.IRODSFile
import org.irods.jargon.core.pub.io.IRODSFileFactory
import org.irods.jargon.core.query.PagingAwareCollectionListing
import org.irods.jargon.idrop.web.services.VirtualCollectionService.ListingType

import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(IrodsCollectionService)
class IrodsCollectionServiceSpec  extends Specification {

	void "test createcollectionListing which lists contents of a collection"() {
		given:
		IRODSAccount irodsAccount = IRODSAccount.instance("host", 1247, "user", "password", "", "zone", "")
		String uniqueName = "root"
		def irodsAccessObjectFactory = mockFor(IRODSAccessObjectFactory)
		PagingAwareCollectionListing listing = new PagingAwareCollectionListing()
		def  listAndSearchAO = mockFor(CollectionAndDataObjectListAndSearchAO)
		listAndSearchAO.demand.listDataObjectsAndCollectionsUnderPathProducingPagingAwareCollectionListing{absPath -> return listing}
		def listAndSearchAOMock = listAndSearchAO.createMock()
		irodsAccessObjectFactory.demand.getCollectionAndDataObjectListAndSearchAO{acct -> return listAndSearchAOMock}
		def iafMock = irodsAccessObjectFactory.createMock()

		IrodsCollectionService irodsCollectionService = new IrodsCollectionService()
		irodsCollectionService.irodsAccessObjectFactory = iafMock


		when:

		def actual = irodsCollectionService.collectionListing("blah", ListingType.ALL, 0, irodsAccount)

		then:

		actual != null
	}

	void "new folder action should call iRODS file mkdirs"() {
		given:
		IRODSAccount irodsAccount = IRODSAccount.instance("host", 1247, "user", "password", "", "zone", "")
		String absolutePath = "/a/path/to/a/folder"
		def irodsAccessObjectFactory = mockFor(IRODSAccessObjectFactory)
		def irodsFileFactory = mockFor(IRODSFileFactory)
		def irodsFile = mockFor(IRODSFile)


		irodsFile.demand.mkdirs{}
		irodsFileFactory.demand.instanceIRODSFile{ap -> return irodsFile.createMock()}
		irodsAccessObjectFactory.demand.getIRODSFileFactory{ir -> return irodsFileFactory.createMock()}

		IrodsCollectionService irodsCollectionService = new IrodsCollectionService()
		irodsCollectionService.irodsAccessObjectFactory = iafMock


		when:

		irodsCollectionService.newFolder(absolutePath,irodsAccount)

		then:

		actual != null
	}
}
