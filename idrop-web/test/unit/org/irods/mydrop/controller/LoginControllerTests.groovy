package org.irods.mydrop.controller


import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.mydrop.controller.LoginController;
import mydrop.*;
import grails.test.*

class LoginControllerTests extends ControllerUnitTestCase {
 
	protected void setUp() {
		super.setUp()
	   
	}
	
	protected void tearDown() {
		super.tearDown()
		irodsFileSystem.closeAndEatExceptions()
	}

     void testLogin() {
		def loginController = new LoginController();
		loginController.login()
		def mav = loginController.modelAndView
		def name = mav.viewName
		assertEquals("should redirect to login view", "login", name)
    }
	 
}
