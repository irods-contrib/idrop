// Place your Spring DSL code here
beans = {

	browseController(org.irods.mydrop.controller.BrowseController) {
		irodsAccessObjectFactory = ref("irodsAccessObjectFactory")
		taggingServiceFactory = ref("taggingServiceFactory")
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

	auditController(org.irods.mydrop.controller.AuditController) { irodsAccessObjectFactory = ref("irodsAccessObjectFactory") }

	loginController(org.irods.mydrop.controller.LoginController) { irodsAccessObjectFactory = ref("irodsAccessObjectFactory") }

	idropLiteController(org.irods.mydrop.controller.IdropLiteController) { irodsAccessObjectFactory = ref("irodsAccessObjectFactory") }

	imageController(org.irods.mydrop.controller.ImageController) {
		irodsAccessObjectFactory = ref("irodsAccessObjectFactory")
		imageServiceFactory = ref("imageServiceFactory")
	}

}
