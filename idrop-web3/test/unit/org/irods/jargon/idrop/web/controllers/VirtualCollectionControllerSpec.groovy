package org.irods.jargon.idrop.web.controllers



import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.vircoll.AbstractVirtualCollection
import org.irods.jargon.vircoll.VirtualCollectionContextImpl
import org.irods.jargon.vircoll.impl.CollectionBasedVirtualCollection
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(VirtualCollectionController)
//@Mock([AuthenticationFilters, ConnectionClosingFilterFilters])
class VirtualCollectionControllerSpec {

    void testListVirtualCollections() {
        given:
        
        //IRODSAccount irodsAccount = IRODSAccount.instance("host", port, "user", "password", "zone", "", AuthScheme.STANDARD)
      
        /*
        def accessObjFactory = mockFor(IRODSAccessObjectFactory)

        def virtualCollectionContext = new VirtualCollectionContextImpl(accessObjFactory.createMock());
        def virtualCollectionFactory = new VirtualCollectionFactoryImpl()
        
        */
       
        def virtualCollectionFactory = mockFor(VirtualCollectionFactory)
        virtualCollectionFactory.demand
        
        def vcServiceMock = mockFor(VirtualCollectionService)

        CollectionBasedVirtualCollection rootColl = new CollectionBasedVirtualCollection("/")
        CollectionBasedVirtualCollection homeColl = new CollectionBasedVirtualCollection("/test/home/userhome")
        def virtualCollections = new ArrayList<AbstractVirtualCollection>()
        virtualCollections.add(rootColl)
        virtualCollections.add(homeColl)
        
        vcServiceMock.demand.virtualCollectionHomeListingForUser { irodsAccount ->
            return virtualCollections
        }

        controller.virtualCollectionService = vcServiceMock.createMock()

        when:
        controller.index()

        then:
        controller.response.status == 200
        log.info("response:${response.text}")
        assert '{"authMessage":"","authenticatedIRODSAccount":null,"authenticatingIRODSAccount":null,"challengeValue":"","class":"org.irods.jargon.core.connection.auth.AuthResponse","responseProperties":{},"startupResponse":null,"successful":false}' == response.text
        
        
        
    }
}
