// Place your Spring DSL code here
beans = {

	profileService(org.irods.mydrop.service.ProfileService)  { irodsAccessObjectFactory = ref("irodsAccessObjectFactory") }

	starringService(org.irods.mydrop.service.StarringService)  { irodsAccessObjectFactory = ref("irodsAccessObjectFactory") }

	sharingService(org.irods.mydrop.service.SharingService)  { irodsAccessObjectFactory = ref("irodsAccessObjectFactory") }

	ruleProcessingService(org.irods.mydrop.service.RuleProcessingService)  { irodsAccessObjectFactory = ref("irodsAccessObjectFactory") }


	browseController(org.irods.mydrop.controller.BrowseController) {
		irodsAccessObjectFactory = ref("irodsAccessObjectFactory")
		taggingServiceFactory = ref("taggingServiceFactory")
		starringService = ref("starringService")
	}

	tagsController(org.irods.mydrop.controller.TagsController) {
		irodsAccessObjectFactory = ref("irodsAccessObjectFactory")
		taggingServiceFactory = ref("taggingServiceFactory")
	}

	searchController(org.irods.mydrop.controller.SearchController) {
		irodsAccessObjectFactory = ref("irodsAccessObjectFactory")
		taggingServiceFactory = ref("taggingServiceFactory")
	}

	metadataController(org.irods.mydrop.controller.MetadataController) { irodsAccessObjectFactory = ref("irodsAccessObjectFactory") }

	ticketController(org.irods.mydrop.controller.TicketController) { irodsAccessObjectFactory = ref("irodsAccessObjectFactory") }

	ticketAccessController(org.irods.mydrop.controller.TicketAccessController) { irodsAccessObjectFactory = ref("irodsAccessObjectFactory") }

	sharingController(org.irods.mydrop.controller.SharingController) { irodsAccessObjectFactory = ref("irodsAccessObjectFactory") }

	loginController(org.irods.mydrop.controller.LoginController) { irodsAccessObjectFactory = ref("irodsAccessObjectFactory") }

	idropLiteController(org.irods.mydrop.controller.IdropLiteController) { irodsAccessObjectFactory = ref("irodsAccessObjectFactory") }

	imageController(org.irods.mydrop.controller.ImageController) {
		irodsAccessObjectFactory = ref("irodsAccessObjectFactory")
		imageServiceFactory = ref("imageServiceFactory")
	}

	auditController(org.irods.mydrop.controller.AuditController) { irodsAccessObjectFactory = ref("irodsAccessObjectFactory") }

	profileController(org.irods.mydrop.controller.ProfileController) { irodsAccessObjectFactory = ref("irodsAccessObjectFactory") }

	homeController(org.irods.mydrop.controller.HomeController) { irodsAccessObjectFactory = ref("irodsAccessObjectFactory") }

	ruleController(org.irods.mydrop.controller.RuleController) { irodsAccessObjectFactory = ref("irodsAccessObjectFactory") }


	shoppingCartController(org.irods.mydrop.controller.ShoppingCartController) { irodsAccessObjectFactory = ref("irodsAccessObjectFactory") }


}
