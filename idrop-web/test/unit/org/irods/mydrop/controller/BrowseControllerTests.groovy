package org.irods.mydrop.controller

import grails.test.*
import java.util.Properties
import org.irods.jargon.core.connection.IRODSAccount 
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.io.IRODSFile
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry
import org.irods.jargon.testutils.TestingPropertiesHelper
import org.irods.jargon.testutils.filemanip.FileGenerator
import org.irods.jargon.testutils.TestingPropertiesHelper
import org.irods.jargon.spring.security.IRODSAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import grails.converters.*



class BrowseControllerTests extends ControllerUnitTestCase {
	
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

    void testBrowse() {
		controller.params.dir = "/"
		controller.irodsAccessObjectFactory = irodsAccessObjectFactory
		controller.irodsAccount = irodsAccount
		controller.loadTree()
		def controllerResponse = controller.response.contentAsString
		def jsonResult = JSON.parse(controllerResponse)
		assertNotNull("missing json result", jsonResult)
		/*def mav = controller.modelAndView
		def name = mav.viewName
		assertNotNull("null mav", mav)
		assertEquals("view name should be loadTree", "loadTree", name)
		def parent = mav.model.linkedHashMap.parent
		assertEquals("parent dir not found", "/", parent)*/
  
    }
	
	void testAjaxDirectoryListingUnderParent() {
		controller.params.dir = "/"
		controller.irodsAccessObjectFactory = irodsAccessObjectFactory
		controller.irodsAccount = irodsAccount
		controller.ajaxDirectoryListingUnderParent()
		def controllerResponse = controller.response.contentAsString
		def jsonResult = JSON.parse(controllerResponse)
		assertNotNull("missing json result", jsonResult)
		/*def mav = controller.modelAndView
		def name = mav.viewName
		assertNotNull("null mav", mav)
		assertEquals("view name should be loadTree", "loadTree", name)
		def parent = mav.model.linkedHashMap.parent
		assertEquals("parent dir not found", "/", parent)*/
  
	}
}
