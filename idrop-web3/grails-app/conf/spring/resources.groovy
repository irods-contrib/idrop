// Place your Spring DSL code here
beans = {

	
	homeController(org.irods.jargon.idrop.web.controllers.HomeController) {
		irodsAccessObjectFactory = ref("irodsAccessObjectFactory")
	}

	
}
