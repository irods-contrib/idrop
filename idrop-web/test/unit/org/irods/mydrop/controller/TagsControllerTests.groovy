
package org.irods.mydrop.controller


import grails.converters.*
import grails.test.*

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.exception.*
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.pub.IRODSFileSystem
import org.irods.jargon.testutils.TestingPropertiesHelper
import org.irods.jargon.usertagging.domain.IRODSTagValue
import org.irods.jargon.usertagging.domain.UserTagCloudView
import org.irods.jargon.usertagging.tags.FreeTaggingService
import org.irods.jargon.usertagging.tags.IRODSTaggingService
import org.irods.jargon.usertagging.tags.TaggingServiceFactory
import org.irods.jargon.usertagging.tags.UserTagCloudService
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
		controller.session["SPRING_SECURITY_CONTEXT"] = irodsAccount
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

	void testUpdateTags() {
		testingPropertiesHelper = new TestingPropertiesHelper()
		testingProperties = testingPropertiesHelper.getTestProperties()
		irodsAccount = testingPropertiesHelper.buildIRODSAccountFromTestProperties(testingProperties)

		FreeTaggingService freeTaggingService = Mockito.mock(FreeTaggingService.class)
		TaggingServiceFactory taggingServiceFactory = Mockito.mock(TaggingServiceFactory.class)
		Mockito.when(taggingServiceFactory.instanceFreeTaggingService(irodsAccount)).thenReturn(freeTaggingService)

		IRODSTaggingService irodsTaggingService = Mockito.mock(IRODSTaggingService.class)
		Mockito.when(taggingServiceFactory.instanceIrodsTaggingService(irodsAccount)).thenReturn(irodsTaggingService)

		controller.irodsAccessObjectFactory = irodsAccessObjectFactory
		controller.taggingServiceFactory = taggingServiceFactory
		controller.irodsAccount = irodsAccount
		def tags = "tag1 tag2 tag3"
		def user = irodsAccount.getUserName()
		def absPath = "abspath"
		controller.params.absPath = absPath
		controller.params.tags = tags
		controller.updateTags()
		Mockito.verify(freeTaggingService).updateTagsForUserForADataObjectOrCollection(absPath, user, tags)
	}

	void testUpdateTagsNoPath() {
		testingPropertiesHelper = new TestingPropertiesHelper()
		testingProperties = testingPropertiesHelper.getTestProperties()
		irodsAccount = testingPropertiesHelper.buildIRODSAccountFromTestProperties(testingProperties)
		FreeTaggingService freeTaggingService = Mockito.mock(FreeTaggingService.class)
		TaggingServiceFactory taggingServiceFactory = Mockito.mock(TaggingServiceFactory.class)
		Mockito.when(taggingServiceFactory.instanceFreeTaggingService(irodsAccount)).thenReturn(freeTaggingService)
		controller.irodsAccessObjectFactory = irodsAccessObjectFactory
		controller.taggingServiceFactory = taggingServiceFactory
		controller.irodsAccount = irodsAccount
		def tags = "tag1 tag2 tag3"
		def user = irodsAccount.getUserName()
		def absPath = "abspath"
		controller.params.absPath = null
		controller.params.tags = tags
		shouldFail(JargonException) { controller.updateTags() }
	}

	void testUpdateTagsBlankPath() {
		testingPropertiesHelper = new TestingPropertiesHelper()
		testingProperties = testingPropertiesHelper.getTestProperties()
		irodsAccount = testingPropertiesHelper.buildIRODSAccountFromTestProperties(testingProperties)
		FreeTaggingService freeTaggingService = Mockito.mock(FreeTaggingService.class)
		TaggingServiceFactory taggingServiceFactory = Mockito.mock(TaggingServiceFactory.class)
		Mockito.when(taggingServiceFactory.instanceFreeTaggingService(irodsAccount)).thenReturn(freeTaggingService)
		controller.irodsAccessObjectFactory = irodsAccessObjectFactory
		controller.taggingServiceFactory = taggingServiceFactory
		controller.irodsAccount = irodsAccount
		def tags = "tag1 tag2 tag3"
		def user = irodsAccount.getUserName()
		def absPath = "abspath"
		controller.params.absPath = ""
		controller.params.tags = tags
		shouldFail(JargonException) { controller.updateTags() }
	}

	void testUpdateTagsNullTags() {
		testingPropertiesHelper = new TestingPropertiesHelper()
		testingProperties = testingPropertiesHelper.getTestProperties()
		irodsAccount = testingPropertiesHelper.buildIRODSAccountFromTestProperties(testingProperties)

		FreeTaggingService freeTaggingService = Mockito.mock(FreeTaggingService.class)
		TaggingServiceFactory taggingServiceFactory = Mockito.mock(TaggingServiceFactory.class)
		Mockito.when(taggingServiceFactory.instanceFreeTaggingService(irodsAccount)).thenReturn(freeTaggingService)

		IRODSTaggingService irodsTaggingService = Mockito.mock(IRODSTaggingService.class)
		Mockito.when(taggingServiceFactory.instanceIrodsTaggingService(irodsAccount)).thenReturn(irodsTaggingService)

		controller.irodsAccessObjectFactory = irodsAccessObjectFactory
		controller.taggingServiceFactory = taggingServiceFactory
		controller.irodsAccount = irodsAccount
		def tags = "tag1 tag2 tag3"
		def user = irodsAccount.getUserName()
		def absPath = "abspath"
		controller.params.absPath = "path"
		controller.params.tags = null
		shouldFail(IllegalArgumentException) { controller.updateTags() }
	}
}
