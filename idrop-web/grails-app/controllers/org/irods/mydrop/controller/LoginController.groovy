package org.irods.mydrop.controller

import org.codehaus.groovy.grails.validation.Validateable;

class LoginController {
	
	
	def login = {
		render(view:"login")
	}
	
	def index ={
		redirect(action: "login")
	}
	
	def logout = {}
	
}
