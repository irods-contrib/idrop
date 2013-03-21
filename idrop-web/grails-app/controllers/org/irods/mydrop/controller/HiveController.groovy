package org.irods.mydrop.controller

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.hive.service.VocabularyService
import org.irods.mydrop.service.HiveService

import edu.unc.ils.mrc.hive.HiveException


class HiveController {

	IRODSAccessObjectFactory irodsAccessObjectFactory
	IRODSAccount irodsAccount
	VocabularyService vocabularyService
	HiveService hiveService
	
	static allowedMethods = [applyHiveTerm:'POST']

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
		
		def absPath = params['absPath']
		if (absPath == null) {
			log.error "no absPath in request"
			def message = message(code:"error.no.path.provided")
			response.sendError(500,message)
		}

		log.info "absPath: ${absPath}"
		
		def selected = params['selectedVocab']
		// TODO: list versus object

		if (selected instanceof Object[]) {
			// ok
		} else {
			selected = [selected]
		}


		hiveService.selectVocabularies(selected)

		forward(action:"index", model:[absPath:absPath])


	}


	/**
	 * Sbow initial HIVE view, which should reflect the selected set of vocabularies, and show a list of all vocabularies 
	 * @return
	 */
	def index() {
		log.info("index")
		
		def absPath = params['absPath']
		if (absPath == null) {
			log.error "no absPath in request"
			def message = message(code:"error.no.path.provided")
			response.sendError(500,message)
			return
		}

		log.info "absPath: ${absPath}"


		def vocabularies = hiveService.retrieveVocabularySelectionListing()
		def hiveState = hiveService.retrieveHiveState()
		if (hiveState.vocabularies.size() == 0) {
			log.info("no HIVE vocabularies configured")
			//TODO: create this view
			render(view:"noVocabularies")
			return
		}

		if (hiveService.areVocabulariesSelected()==false) {
			render(view:"vocabSelectionList", model:[vocabs:vocabularies,absPath:absPath])
		} else {
			forward(action:"conceptBrowser", model:[absPath:absPath,hiveState:hiveState,vocabs:vocabularies])
		}
	}
	
	/**
	 * Set the concept browser to display the top level of the given vocabulary
	 * @return
	 */
	def resetConceptBrowser() {
		log.info("resetConceptBrowser")
		log.info(params)
		
		def absPath = params['absPath']
		if (absPath == null) {
			log.error "no absPath in request"
			def message = message(code:"error.no.path.provided")
			response.sendError(500,message)
		}

		log.info "absPath: ${absPath}"
		
		def vocabulary = params['vocabulary']
		def hiveState = hiveService.retrieveHiveState()
		
		if (!vocabulary) {
			vocabulary = hiveState.currentVocabulary
		}
		
		if (!vocabulary) {
			log.error("no vocabulary is selected or possible to select")
				response.sendError(500, message(code:"error.no.vocabulary.selected" ))
				return
		}
		
		hiveService.getTopLevelConceptProxyForVocabulary(vocabulary, absPath, irodsAccount)
		forward(action:"conceptBrowser",model:[absPath:absPath])
	
		
	}
	
	/**
	 * Build hive update dialog based on the provided path and vocabulary information
	 * @return
	 */
	def hiveUpdateDialog() {
		log.info("hiveUpdateDialog")
		log.info(params)
		
		def absPath = params['absPath']
		if (absPath == null || absPath == "") {
			log.error "no absPath in request"
			def message = message(code:"error.no.path.provided")
			response.sendError(500,message)
			return
		}

		log.info "absPath: ${absPath}"

		def targetUri = params['uri']
		if (targetUri == null || targetUri == "") {
			log.error "no targetUri in request"
			def message = message(code:"error.no.uri.provided")
			response.sendError(500,message)
			return
		}

		log.info "targetUri: ${targetUri}"
		
		def vocabulary = params['vocabulary']
		if (vocabulary == null || vocabulary == "") {
			log.error "no vocabulary in request"
			def message = message(code:"error.no.vocabulary.provided")
			response.sendError(500,message)
			return
		}
		

		log.info "vocabulary: ${vocabulary}"
		try {
			def conceptProxy = hiveService.getConceptByUri(targetUri, absPath, irodsAccount)
			log.info("got concept proxy:${conceptProxy}")
			render(view:"hiveDetailsDialog", model:[conceptProxy:conceptProxy, absPath:absPath])
		} catch (HiveException he) {
			log.error("hive exception getting concept proxy",he)
			response.sendError(500,he.message)
		}
	}
	
	/**
	 * Add a term to iRODS
	 * @return
	 */
	def applyHiveTerm() {
		log.info("applyHiveTerm")
		log.info(params)
		
		def absPath = params['absPath']
		if (absPath == null || absPath == "") {
			log.error "no absPath in request"
			def message = message(code:"error.no.path.provided")
			response.sendError(500,message)
			return
		}

		log.info "absPath: ${absPath}"

		def targetUri = params['uri']
		if (targetUri == null || targetUri == "") {
			log.error "no targetUri in request"
			def message = message(code:"error.no.uri.provided")
			response.sendError(500,message)
			return
		}

		log.info "targetUri: ${targetUri}"
		
		def vocabulary = params['vocabulary']
		if (vocabulary == null || vocabulary == "") {
			log.error "no vocabulary in request"
			def message = message(code:"error.no.vocabulary.provided")
			response.sendError(500,message)
			return
		}

		log.info "vocabulary: ${vocabulary}"
		
		
		def comment = params['comment']
		if (vocabulary == null) {
			comment = "";
		}
		
		log.info("adding hive vocbulary term")
		def conceptProxy = hiveService.applyVocabularyTerm(targetUri, absPath, vocabulary, comment, irodsAccount)
		log.info("term added, new concept proxy:${conceptProxy}")
		def hiveState = hiveService.retrieveHiveState()
		render(view:"conceptBrowser", model:[hiveState:hiveState,vocabularySelections:hiveService.retrieveVocabularySelectionListing(), conceptProxy:conceptProxy, absPath:absPath])

	}
	
	/**
	 * Show the concept browser given the selected vocabularies
	 * @return
	 */
	def conceptBrowser() {
		log.info("conceptBrowser")
		log.info(params)
		
		def absPath = params['absPath']
		if (absPath == null) {
			log.error "no absPath in request"
			def message = message(code:"error.no.path.provided")
			response.sendError(500,message)
		}

		log.info "absPath: ${absPath}"

		def indexLetter = params['indexLetter']
		def targetUri = params['targetURI']
		/*def vocabularies = params['vocabularies']
		 */

		if (!indexLetter) {
			indexLetter = 'A'
		}

		if (!targetUri) {
			targetUri = ""
		}

		def hiveState = hiveService.retrieveHiveState()
		def conceptProxy
		
		log.info("getting concept proxy for display...")
		
		if (targetUri) {
			log.info("have target uri, make this the current:${targetUri}")
			conceptProxy = hiveService.getConceptByUri(targetUri, absPath, irodsAccount)
		} else if (hiveState.currentConceptURI) {
			log.info("have a current uri, redisplay this information:${hiveState.currentConceptURI}")
			conceptProxy = hiveService.getConceptByUri(hiveState.currentConceptURI,  absPath, irodsAccount)
		} else {
			// no current or desired uri, select the top level of the current vocabulary
			def currentVocab = hiveService.getCurrentVocabularySelection()
			if(!currentVocab) {
				log.error("no vocabulary is selected or possible to select")
				response.sendError(500, message(code:"error.no.vocabulary.selected" ))
				return
			}
			log.info("getting top level for:${currentVocab}")
			conceptProxy = hiveService.getTopLevelConceptProxyForVocabulary(currentVocab, absPath, irodsAccount)
			
		}
		
		
		

		render(view:"conceptBrowser", model:[hiveState:hiveState,vocabularySelections:hiveService.retrieveVocabularySelectionListing(), conceptProxy:conceptProxy, absPath:absPath])
	}

	def searchConcept(){
		log.info("searchConcept")
		log.info(params.searchedConcept)
		
		def searchResult
		def hiveState = hiveService.retrieveHiveState()
		def listOfOpenedVocabularies = hiveState.selectedVocabularies
		def searchedConcept = params['searchedConcept'] //I have to name the concept which is being searched as param searchedConcept
		if (!searchedConcept) {
			log.info("nothing to search for!")
		}
		else {
			searchResult = hiveService.searchConcept(searchedConcept , listOfOpenedVocabularies) 
			if (!searchConcept){
				log.info("nothing was found for the searched term")
			}
			render (view:"conceptSearch", model:[searchResult:searchResult])
		}	
			
	}
	
}

