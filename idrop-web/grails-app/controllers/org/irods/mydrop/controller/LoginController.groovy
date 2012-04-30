package org.irods.mydrop.controller

class LoginController {

	def login = {
		log.info "in login"
		//log.info  "params:${request.parameterMap}"
		log.info "params:${params}"
		log.info("config is:${grailsApplication.config}")
		def presetHost = grailsApplication.config.idrop.config.preset.host
		def presetPort = grailsApplication.config.idrop.config.preset.port
		def presetZone = grailsApplication.config.idrop.config.preset.zone
		def presetResource = grailsApplication.config.idrop.config.preset.resource

		def host = params['host']
		def port = params['port']
		log.info("host:${host}")
		log.info("port:${port}")


		if (presetHost) {
			log.info("in login preset mode for host:${presetHost}")
		}

		if (presetPort) {
			log.info("preset port:${presetPort}")
		}

		if (presetZone) {
			log.info("preset port:${presetZone}")
		}

		if (presetResource) {
			log.info("preset port:${presetResource}")
		}

		response.setHeader("apptimeout","apptimeout")

		render(view:"login", model:[host:params['host'], port:params['port'], presetHost:presetHost,presetPort:presetPort, presetZone:presetZone,presetResource:presetResource])
	}

	def index ={ redirect(action: "login") }

	def logout = {redirect(action: "login")}
}
