
package org.irods.mydrop.controller


import java.util.Properties;

import grails.converters.*
import grails.test.*

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.pub.IRODSFileSystem
import org.irods.jargon.testutils.TestingPropertiesHelper
import org.irods.jargon.usertagging.UserTagCloudService
import org.irods.jargon.usertagging.domain.UserTagCloudView
import org.irods.jargon.usertagging.domain.IRODSTagValue
import org.irods.jargon.usertagging.TaggingServiceFactory
import org.irods.jargon.spring.security.IRODSAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.mockito.Mockito


class TagsControllerTests extends ControllerUnitTestCase {
	
	IRODSAccessObjectFactory irodsAccessObjectFactory
	IRODSAccount irodsAccount
	Properties testingProperties
	TestingPropertiesHelper testingPropertiesHelper
	IRODSFileSystem irodsFileSystem

	protected void setUp() {
		super.setUp()
		testingPropertiesHelper = new TestingPropertiesHelper()
		testingProperties = testingPropertiesHelper.getTestProperties()
		irodsAccount = testingPropertiesHelper.buildIRODSAccountFromTestProperties(testingProperties)
		irodsFileSystem = IRODSFileSystem.instance()
		irodsAccessObjectFactory = irodsFileSystem.getIRODSAccessObjectFactory()
		def irodsAuthentication = new IRODSAuthenticationToken(irodsAccount)
		SecurityContextHolder.getContext().authentication = irodsAuthentication
	}

	protected void tearDown() {
		super.tearDown()
	}

	void testGetTagCloud() {
		controller.irodsAccessObjectFactory = irodsAccessObjectFactory
		UserTagCloudService userTagCloudService = Mockito.mock(UserTagCloudService.class)
		TaggingServiceFactory taggingServiceFactory = Mockito.mock(TaggingServiceFactory.class)
		UserTagCloudView userTagCloudView = UserTagCloudView.instance(irodsAccount.getUserName(), new ArrayList<IRODSTagValue>(), new ArrayList<IRODSTagValue>())		
		Mockito.when(userTagCloudService.getTagCloud()).thenReturn(userTagCloudView)
		Mockito.when(taggingServiceFactory.instanceUserTagCloudService(irodsAccount)).thenReturn(userTagCloudService)
		controller.irodsAccount = irodsAccount
		controller.taggingServiceFactory = taggingServiceFactory
		controller.tagCloud()
		def mav = controller.modelAndView
		def name = mav.viewName
		assertNotNull("null mav", mav)
		assertEquals("view name should be tagCloud", "tagCloud", name)
		def tagView = mav.model.tagCloud
		assertNotNull("null tagView in model", tagView)
		
	}
}
