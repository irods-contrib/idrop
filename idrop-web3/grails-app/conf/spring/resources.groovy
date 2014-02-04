// Place your Spring DSL code here
beans = {

	loginController(org.irods.jargon.idrop.web.controllers.LoginController) { irodsAccessObjectFactory = ref("irodsAccessObjectFactory") }




	/*
	 * Services
	 */

	authenticationService(idrop.web3.AuthenticationService)  { irodsAccessObjectFactory = ref("irodsAccessObjectFactory") }

	virtualCollectionServicesCreatingFactory(idrop.web3.virtualCollectionServicesCreatingFactoryImpl) { irodsAccessObjectFactory = ref("irodsAccessObjectFactory") }

	virtualCollectionService(idrop.web3.VirtualCollectionService)  { irodsAccessObjectFactory = ref("irodsAccessObjectFactory") }



}
