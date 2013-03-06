package org.irods.mydrop.controller

import grails.test.*

import java.util.Properties

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.pub.IRODSFileSystem
import org.irods.jargon.datautils.image.ImageServiceFactory
import org.irods.jargon.datautils.image.ThumbnailService
import org.irods.jargon.testutils.TestingPropertiesHelper
import org.mockito.Mockito

class ImageControllerTests extends ControllerUnitTestCase {
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
		irodsFileSystem.closeAndEatExceptions()
	}

	void testGenerateThumbnail() {
		def irodsAccessObjectFactory = Mockito.mock(IRODSAccessObjectFactory.class)

		def testPath = "/test/path.jpg"
		ThumbnailService thumbnailService = Mockito.mock(ThumbnailService.class)
		InputStream mockStream = Mockito.mock(InputStream.class)
		ImageServiceFactory imageServiceFactory = Mockito.mock(ImageServiceFactory.class)
		Mockito.when(imageServiceFactory.instanceThumbnailService(irodsAccount)).thenReturn(thumbnailService)
		File mockFile = Mockito.mock(File.class)
		Mockito.when(thumbnailService.createThumbnailLocallyViaJAI(mockFile, testPath, 300)).thenReturn(mockFile)
		/*
		 * FIXME: reimplement with various session states
		 * controller.irodsAccessObjectFactory = irodsAccessObjectFactory
		 controller.imageServiceFactory = imageServiceFactory
		 controller.irodsAccount = irodsAccount
		 controller.params.absPath = testPath
		 controller.generateThumbnail() */
	}
}
