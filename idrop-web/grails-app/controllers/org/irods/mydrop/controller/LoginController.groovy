package org.irods.mydrop.controller

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.exception.JargonException
import org.irods.jargon.core.pub.IRODSAccessObjectFactory

class LoginController {

	IRODSAccessObjectFactory irodsAccessObjectFactory
	IRODSAccount irodsAccount

	static allowedMethods = [authenticate:'POST']

	def beforeInterceptor = [action:this.&auth, except:[
			'login',
			'index',
			'authenticate'
		]]

	def auth() {
		if(!session["SPRING_SECURITY_CONTEXT"]) {
			redirect(controller:"login", action:"login")
			return false
		}
		irodsAccount = session["SPRING_SECURITY_CONTEXT"]
	}

	def login = {
		log.info "in login"
		//log.info  "params:${request.parameterMap}"
		log.info "params:${params}"
		log.info("config is:${grailsApplication.config}")
		def presetHost = grailsApplication.config.idrop.config.preset.host
		def presetPort = grailsApplication.config.idrop.config.preset.port
		def presetZone = grailsApplication.config.idrop.config.preset.zone
		def presetResource = grailsApplication.config.idrop.config.preset.resource

		response.setHeader("apptimeout","apptimeout")

		LoginCommand loginCommand = new LoginCommand()

		if (presetHost) {
			loginCommand.host = presetHost
			loginCommand.usePresets = true
		} else {
			loginCommand.usePresets = false
		}

		if (presetPort) {
			loginCommand.port = presetPort
		} else {
			loginCommand.port = 1247
		}

		if (presetZone) {
			loginCommand.zone = presetZone
		}

		if (presetResource) {
			loginCommand.defaultStorageResource = presetResource
		}

		render(view:"login", model:[loginCommand:loginCommand])

	}

	def index ={ redirect(action: "login") }

	def authenticate(LoginCommand loginCommand) {

		log.info "cmd: ${loginCommand}"

		/**
		 * If there is an error send back the view for redisplay with error messages
		 */
		if (!loginCommand.validate()) {
			log.info("errors in page, returning with error info:${loginCommand}")
			flash.error =  message(code:"error.data.error")
			render(view:"login",  model:[loginCommand:loginCommand])
			return
		}

		log.info("edits pass")

		def resource =  loginCommand.defaultStorageResource ? loginCommand.defaultStorageResource : ""
		def userName =  loginCommand.user ? loginCommand.user : ""
		def password =  loginCommand.password ? loginCommand.password : ""

		boolean success = true

		if (userName == "" && !loginCommand.useGuestLogin) {
			loginCommand.errors.reject("error.auth.invalid.user","Invalid user or password")
			render(view:"login", model:[loginCommand:loginCommand])
			return
		}

		if (password == "" | userName == "" && !loginCommand.useGuestLogin) {
			loginCommand.errors.reject("error.auth.invalid.user","Invalid user or password")
			render(view:"login", model:[loginCommand:loginCommand])
			return
		}

		IRODSAccount irodsAccount = IRODSAccount.instance(
				loginCommand.host,
				loginCommand.port,
				userName,
				password,
				"",
				loginCommand.zone,
				resource)

		log.info("built irodsAccount:${irodsAccount}")



		try {
			irodsAccessObjectFactory
					.getUserAO(irodsAccount)
		} catch (JargonException e) {
			log.error("unable to authenticate, JargonException", e)

			if (e.getCause() == null) {
				if (e.getMessage().indexOf("-826000") > -1) {
					log.warn("invalid user/password")
					loginCommand.errors.reject("error.auth.invalid.user","Invalid user or password")
					success = false
				} else {
					log.error("authentication service exception", e)

					loginCommand.errors.reject("error.auth.invalid.user","Unable to authenticate")
					success = false
				}
			} else if (e.getCause() instanceof UnknownHostException) {
				log.warn("cause is invalid host")
				loginCommand.errors.reject("error.auth.invalid.host","Unknown host")
				success = false
			} else if (e.getCause().getMessage().indexOf("refused") > -1) {
				log.error("cause is refused or invalid port")
				loginCommand.errors.reject("error.auth.connection.refused","Connection refused")
				success = false
			} else {
				log.error("authentication service exception", e)
				log.error("exception in upload transfer", e)
				response.sendError(500,e.message)
				return
			}
		}

		if (!success) {
			log.warn("unsuccessful, render the login again")
			render(view:"login", model:[loginCommand:loginCommand])
			return
		}
		session["SPRING_SECURITY_CONTEXT"] = irodsAccount
		redirect(controller:"home")
	}

	def logout = {
		session["SPRING_SECURITY_CONTEXT"] = null
		redirect(action:"login")
	}
}
class LoginCommand {
	boolean useGuestLogin
	boolean usePresets
	String user
	String password
	String host
	String zone
	int port
	String defaultStorageResource

	static constraints = {
		host(blank:false)
		zone(blank:false)
		port( min:1, max:Integer.MAX_VALUE)
	}
}
