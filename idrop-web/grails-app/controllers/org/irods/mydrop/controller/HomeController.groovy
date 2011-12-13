package org.irods.mydrop.controller

class HomeController {

	def index = {
		log.info ("in home controller index action")
		render(view: "index")
	}

	def showBrowseToolbar = {
		log.info("showBrowseToolbar")
		render(view:"browseToolbar")
	}
}
