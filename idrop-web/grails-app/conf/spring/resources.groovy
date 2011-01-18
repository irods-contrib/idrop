// Place your Spring DSL code here
beans = {
	
	irodsAuthenticationHelperService(mydrop.IRODSAuthenticationHelperService) {
		irodsAccessObjectFactory = ref("irodsAccessObjectFactory")
	}
	
	browserController(org.irods.mydrop.controller.BrowseController) {
		irodsAccessObjectFactory = ref("irodsAccessObjectFactory")
	}
	
}
