package org.irods.mydrop.controller

import grails.plugins.rest.client.RestBuilder

import org.irods.jargon.core.connection.IRODSAccount


class SparqlQueryController {
	//IRODSAccessObjectFactory irodsAccessObjectFactory
	IRODSAccount irodsAccount
	//VocabularyService vocabularyService
	//HiveService hiveService
	def grailsApplication


	/**
	 * Interceptor grabs IRODSAccount from the SecurityContextHolder
	 */
	def beforeInterceptor = [action:this.&auth]

	def auth() {

		if(!session["SPRING_SECURITY_CONTEXT"]) {
			redirect(controller:"login", action:"login")
			return false
		}
		irodsAccount = session["SPRING_SECURITY_CONTEXT"]
	}


	def afterInterceptor = {
		log.debug("closing the session")
		//irodsAccessObjectFactory.closeSession()
	}

	public SparqlQueryController() {
	}

	def index() {
	}

	/**
	 * Do a canned search by the given vocabulary term
	 * @return
	 */
	def searchByTerm() {
		log.info("searchByTerm")
		def uri = params['uri']
		if (uri == null) {
			log.error "no uri in request"
			def message = message(code:"error.no.uri.provided")
			response.sendError(500,message)
			return
		}

		log.info "uri: ${uri}"

		def context = grailsApplication.config.hive.query.context

		def RestBuilder rest = new RestBuilder()
		def resp = rest.get(context + "preparedQuery/allForVocabularyTerm?vocabUri=http://www.fao.org/aos/agrovoc&termId=c_6399")
		render resp.body

	}
}
