package org.irods.mydrop.controller

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.hive.service.VocabularyService
import org.irods.mydrop.service.HiveService
import org.irods.mydrop.service.HiveStateService
import org.unc.hive.client.ConceptProxy


class HiveController {

	IRODSAccessObjectFactory irodsAccessObjectFactory
	IRODSAccount irodsAccount
	VocabularyService vocabularyService
	HiveStateService hiveStateService
	HiveService hiveService

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
		irodsAccessObjectFactory.closeSession()
	}

	/**
	 * Show a selection of available HIVE voabularies
	 * @return
	 */
	def vocabSelection() {
		log.info("vocabSelection")

		log.info("getting vocab names")
		List<String> vocabs = vocabularyService.getAllVocabularyNames()
		render(view:"vocabSelectionList", model:[vocabs:vocabs])
	}


	/**
	 * Sbow initial HIVE view, which should reflect the selected set of vocabularies, and show a list of all vocabularies 
	 * @return
	 */
	def index() {
		log.info("index")

		// if vocabs already selected show concept

		log.info("getting vocab names")
		List<String> vocabs = vocabularyService.getAllVocabularyNames()
		render(view:"vocabSelectionList", model:[vocabs:vocabs])
	}

	/**
	 * Show the concept browser given the selected vocabularies
	 * @return
	 */
	def conceptBrowser() {
		log.info("conceptBrowser")
		log.info(params)

		def selected = params['selectedVocab']
		def indexLetter = params['indexLetter']
		def parentTerm = params['parentTerm']
		/*def vocabularies = params['vocabularies']
		 */

		if (!indexLetter) {
			indexLetter = 'A'
		}

		if (!parentTerm) {
			parentTerm = ""
		}

		if (!selected) {
			response.sendError(500, message(code:"default.null.message",args:"${ ['selected'] }" ))
		}

		/*
		 if (!vocabularies) {
		 response.sendError(500, message(code:"default.null.message",args:"${ ['vocabularies'] }" ))
		 }
		 List<ConceptProxy> concepts
		 if (!parentTerm) {
		 concepts = vocabularyService.getSubTopConcept(selected.toString().toLowerCase() , indexLetter , true)
		 } else {
		 // get the child concepts for the parent term provided
		 }
		 */
		//params['selectedVocab']

		//render(view:"conceptBrowser", model:[concepts:concepts])
		render(view:"conceptBrowser", model:[vocabs:selected])
	}


	def showTermsInVocabulary(){
		log.info("showTermsInVocabulary")
		log.info("params:${params}")

		def selectedVocab = params['selectedVocab'].toString().toLowerCase()
		def parentTerm = params['parentTerm']
		def indexLetter = params['indexLetter']

		if (!indexLetter) {
			indexLetter = 'A'
		}

		if (!selectedVocab) {
			response.sendError(500, message(code:"default.null.message",args:"${ ['selectedVocab'] }" ))
		}

		/*
		 * This works now for top level concepts
		 */

		List<ConceptProxy> concepts

		if (!parentTerm) {

			concepts = vocabularyService.getSubTopConcept(params['selectedVocab'].toString().toLowerCase() , indexLetter , true)
		} else {

			// get the child concepts for the parent term provided
		}

		//params['selectedVocab']

		render(view:"vocabTermsListing", model:[concepts:concepts])
	}
}

