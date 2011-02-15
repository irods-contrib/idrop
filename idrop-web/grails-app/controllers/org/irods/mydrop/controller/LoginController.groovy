package org.irods.mydrop.controller

class LoginController {

	def login = { render(view:"login") }

	def index ={ redirect(action: "login") }

	def logout = {}
}
