// Place your Spring DSL code here
beans = {

	
	homeController(org.irods.jargon.idrop.web.controllers.HomeController) {
		irodsAccessObjectFactory = ref("irodsAccessObjectFactory")
	}
	
	
	loginController(org.irods.jargon.idrop.web.controllers.LoginController) {
		irodsAccessObjectFactory = ref("irodsAccessObjectFactory")
	}
	
	
	
	
	/*
	 * Services
	 */
	
	authenticationService(idrop.web3.AuthenticationService)  { irodsAccessObjectFactory = ref("irodsAccessObjectFactory") }
	

	
}
