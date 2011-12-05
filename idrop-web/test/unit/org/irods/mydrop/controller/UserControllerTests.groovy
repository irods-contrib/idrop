package org.irods.mydrop.controller

import grails.test.*

import java.util.Properties

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.pub.IRODSFileSystem
import org.irods.jargon.core.pub.UserAO
import org.irods.jargon.core.pub.domain.User
import org.irods.jargon.spring.security.IRODSAuthenticationToken
import org.irods.jargon.testutils.TestingPropertiesHelper
import org.springframework.security.core.context.SecurityContextHolder
import org.mockito.Mockito

class UserControllerTests extends ControllerUnitTestCase {
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
		def irodsAuthentication = new IRODSAuthenticationToken(irodsAccount)
		SecurityContextHolder.getContext().authentication = irodsAuthentication
	}

	protected void tearDown() {
		super.tearDown()
	}

	void testSearchByNameLike() {
		def testUserName = "abc"
		def irodsAccessObjectFactory = Mockito.mock(IRODSAccessObjectFactory.class)
		def UserAO userAO = Mockito.mock(UserAO.class)
		List<User> users = new ArrayList<User>()
		Mockito.when(userAO.findWhere(testUserName)).thenReturn(users)
		Mockito.when(irodsAccessObjectFactory.getUserAO(irodsAccount)).thenReturn(userAO)
		
		controller.params.dir = "/"
		controller.irodsAccessObjectFactory = irodsAccessObjectFactory
		controller.irodsAccount = irodsAccount
		controller.params.userSearchTerm = "search"
		controller.userSearchByNameLike()
		def mav = controller.modelAndView
		def name = mav.viewName

		assertNotNull("null mav", mav)
		assertEquals("wrong view, should be userList", "userList", name)
		def userResult = mav.model.users
		assertNotNull("null users object", userResult)
		
	}

	
}
