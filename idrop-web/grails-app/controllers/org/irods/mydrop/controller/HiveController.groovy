package org.irods.mydrop.controller

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.hive.service.VocabularyService
import org.irods.mydrop.service.HiveService
import org.unc.hive.client.ConceptProxy


class HiveController {

	IRODSAccessObjectFactory irodsAccessObjectFactory
	IRODSAccount irodsAccount
	VocabularyService vocabularyService
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
	 * Update with the selected vocabularies
	 * @return
	 */
	def selectVocabularies() {
		log.info("selectVocabularies")
		log.info(params)
		def selected = params['selectedVocab']
		// TODO: list versus object

		if (selected instanceof Object[]) {
			// ok
		} else {
			selected = [selected]
		}


		hiveService.selectVocabularies(selected)

		forward(action:"index")


	}


	/**
	 * Sbow initial HIVE view, which should reflect the selected set of vocabularies, and show a list of all vocabularies 
	 * @return
	 */
	def index() {
		log.info("index")

		def vocabularies = hiveService.retrieveVocabularySelectionListing()
		def hiveState = hiveService.retrieveHiveState()
		if (hiveState.vocabularies.size() == 0) {
			log.info("no HIVE vocabularies configured")
			//TODO: create this view
			render(view:"noVocabularies")
			return
		}

		if (hiveService.areVocabulariesSelected()==false) {
			render(view:"vocabSelectionList", model:[vocabs:vocabularies])
		} else {
			forward(action:"conceptBrowser", model:[hiveState:hiveState,vocabs:vocabularies])
		}
	}

	/**
	 * Show the concept browser given the selected vocabularies
	 * @return
	 */
	def conceptBrowser() {
		log.info("conceptBrowser")
		log.info(params)

		def indexLetter = params['indexLetter']
		def targetUri = params['targetUri']
		/*def vocabularies = params['vocabularies']
		 */

		if (!indexLetter) {
			indexLetter = 'A'
		}

		if (!targetUri) {
			targetUri = ""
		}

		def hiveState = hiveService.retrieveHiveState()





		render(view:"conceptBrowser", model:[hiveState:hiveState,vocabularySelections:hiveService.retrieveVocabularySelectionListing()])
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

