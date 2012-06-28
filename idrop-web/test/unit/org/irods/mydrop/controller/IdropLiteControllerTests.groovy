package org.irods.mydrop.controller

import java.util.Properties

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.pub.IRODSFileSystem
import org.irods.jargon.testutils.TestingPropertiesHelper
import grails.test.*
import grails.converters.*

class IdropLiteControllerTests extends ControllerUnitTestCase {
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

	void testAppletLoader() {
	}

	/* TODO: temp turned off,need to set param for applet code
	 void testAppletLoader() {
	 controller.params.absPath = "/"
	 controller.irodsAccessObjectFactory = irodsAccessObjectFactory
	 controller.irodsAccount = irodsAccount
	 controller.appletLoader()
	 def controllerResponse = controller.response.contentAsString
	 def jsonResult = JSON.parse(controllerResponse)
	 assertNotNull("missing applet info", jsonResult)
	 }
	 */
}
