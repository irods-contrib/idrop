package org.irods.jargon.idrop.web.services



import grails.test.mixin.*
import org.irods.jargon.core.connection.IRODSServerProperties
import org.irods.jargon.core.pub.EnvironmentalInfoAO
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(EnvironmentServicesService)
class EnvironmentServicesServiceSpec {
    
    @Before
    void setup() {
    }

    void testGetIrodsServerProperties() {
        IRODSServerProperties irodsServerProperties = IRODSServerProperties.instance(IcatEnabled.ICAT_ENABLED, 100, "v1", "api1", "zone")
        EnvironmentalInfoAO environmentalInfoAO = mockFor(EnvironmentalInfoAO)
        environmentalInfoAO.demand.getIRODSServerProperties {irodsAccount -> return irodsServerProperties}
        
        IRODSAccessObjectFactory irodsAccessObjectFactory = mockFor(IRODSAccessObjectFactory)
        irodsAccessObjectFactory.demand.getEnvironmentalInfoAO{irodsAccount -> return environmentalInfoAO.createMock()}
        irodsAccessObjectFactory = irodsAccessObjectFactory.createMock
        EnvironmentServicesService envSvc = new EnvironmentServicesService()
        envSvc.irodsAccessObjectFactory = irodsAccessObjectFactory
        
        def actual = envSvc.getIrodsServerProperties(irodsAccount)
        assertNotNull(actual)
         
    }
        
        
}

