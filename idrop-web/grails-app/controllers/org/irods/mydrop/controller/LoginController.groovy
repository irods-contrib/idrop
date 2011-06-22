package org.irods.mydrop.controller

class LoginController {

	def login = { 
		log.info "in login"
		response.setHeader("apptimeout","apptimeout")
		render(view:"login") 
	}

	def index ={ redirect(action: "login") }

	def logout = {redirect(action: "login")}
}
