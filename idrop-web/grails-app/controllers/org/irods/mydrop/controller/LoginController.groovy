package org.irods.mydrop.controller

class LoginController {

	def login = { 
		response.setHeader("apptimeout","apptimeout")
		render(view:"login") 
	}

	def index ={ redirect(action: "login") }

	def logout = {}
}
