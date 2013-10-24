package org.irods.mydrop.controller

import grails.plugins.rest.client.RestBuilder

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.hive.service.VocabularyService;
import org.irods.mydrop.controller.utils.ViewNameAndModelValues
import org.irods.mydrop.service.HiveService;
import org.irods.mydrop.service.SparqlService;


class SparqlQueryController {
	//IRODSAccessObjectFactory irodsAccessObjectFactory
	IRODSAccount irodsAccount
	def grailsApplication
	
	IRODSAccessObjectFactory irodsAccessObjectFactory
	
	VocabularyService vocabularyService
	HiveService hiveService
	SparqlService sparqlService


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
		log.info("index()")
		
		def vocabularies = sparqlService.retrieveVocabularySelectionListing()
		def currentVocab = sparqlService.getCurrentVocabularySelection()
		def hiveQueryState = sparqlService.retrieveHiveQueryState()
		
		//def hiveQueryVocabItemList = sparqlService.retrieveHiveQueryVocabularyItemList()
		def hiveQueryVocabularyItemList = sparqlService.retrieveHiveQueryVocabularyItemList()
		def hiveQueryVocabItemListSize = sparqlService.getQueryVocabularyItemListSize()
		
		render(view:"index", model:[queryVocabList: hiveQueryVocabularyItemList,queryVocabListSize:hiveQueryVocabItemListSize])
		//forward(action:"showConceptBrowser", model:[hiveQueryState:hiveQueryState,currentVocab:currentVocab,vocabs:vocabularies])
	
	}
	
	def searchPanel() {
		log.info("searchPanel()")
		
		def vocabularies = sparqlService.retrieveVocabularySelectionListing()
		def currentVocab = sparqlService.getCurrentVocabularySelection()
		def hiveQueryState = sparqlService.retrieveHiveQueryState()
		
		//def hiveQueryVocabItemList = sparqlService.retrieveHiveQueryVocabularyItemList()
		def hiveQueryVocabularyItemList = sparqlService.retrieveHiveQueryVocabularyItemList()
		def hiveQueryVocabItemListSize = sparqlService.getQueryVocabularyItemListSize()
		
		render(view:"_hiveQuery", model:[queryVocabList: hiveQueryVocabularyItemList,queryVocabListSize:hiveQueryVocabItemListSize])
		//forward(action:"showConceptBrowser", model:[hiveQueryState:hiveQueryState,currentVocab:currentVocab,vocabs:vocabularies])
		
	}

	/**
	 * Do a canned search by the given vocabulary term
	 * @return
	 */
	def searchByRelatedTerm() {
		log.info("searchByRelatedTerm")
		String uri = params['uri']
		if (uri == null) {
			log.error "no uri in request"
			def message = message(code:"error.no.uri.provided")
			response.sendError(500,message)
			return
		}

		log.info "uri: ${uri}"

		def context = grailsApplication.config.hive.query.context

		int idx = uri.indexOf("#")
		if (idx == -1) {
			throw new Exception("unable to parse URI")
		}



		def reqString = context + "preparedQuery/allForRelatedVocabularyTerm?vocabUri=" + uri.substring(0,idx) + "&termId=" + uri.substring(idx + 1)
		log.info("request will be:${reqString}")

		def RestBuilder rest = new RestBuilder()
		def resp = rest.get(reqString)

		render resp.body

	}

	/**
	 * Do a canned search by the given vocabulary term
	 * @return
	 */
	def searchByTerm() {
		log.info("searchByTerm")
		String uri = params['uri']
		if (uri == null) {
			log.error "no uri in request"
			def message = message(code:"error.no.uri.provided")
			response.sendError(500,message)
			return
		}

		log.info "uri: ${uri}"

		def context = grailsApplication.config.hive.query.context

		int idx = uri.indexOf("#")
		if (idx == -1) {
			throw new Exception("unable to parse URI")
		}



		def reqString = context + "preparedQuery/allForVocabularyTerm?vocabUri=" + uri.substring(0,idx) + "&termId=" + uri.substring(idx + 1)
		log.info("request will be:${reqString}")


		def RestBuilder rest = new RestBuilder()
		//def resp = rest.get(context + "preparedQuery/allForVocabularyTerm?vocabUri=http://www.fao.org/aos/agrovoc&termId=c_3206")
		def resp = rest.get(reqString)

		render resp.body

	}

	/**
	 * Do a canned search by the given vocabulary term
	 * @return
	 */
	def searchSparql() {
		log.info("searchSparql")
		String query = params['query']
		if (query == null) {
			log.error "no query in request"
			def message = message(code:"error.no.uri.provided")
			response.sendError(500,message)
			return
		}

		log.info "query: ${query}"

		def context = grailsApplication.config.hive.query.context

		def reqString = context + "sparql"
		log.info("request will be:${reqString}")

		def RestBuilder rest = new RestBuilder()
		def resp = rest.post(reqString) { body(query)}

		render resp.body

	}
	
	/**
	 * Show a concept browser for user to pick up term
	 * @return
	 */
	
	def showConceptBrowser() {
		log.info "showConceptBrowser()"
		log.info(params)
		
		Integer index = params.int('index')
		
		if(index == 0) {
			log.info("index == 0")
		}
		
		log.info "index: ${index}"
		
		def vocabularies = sparqlService.retrieveVocabularySelectionListing()
		def currentVocab = sparqlService.getCurrentVocabularySelection()
		def hiveQueryState = sparqlService.retrieveHiveQueryState()
		
		log.info "hiveQueryState and its selected vocabularies: ${vocabularies}"
		
		if (hiveQueryState.vocabularies.size() == 0) {
			log.info "no hive vocabulary is configered"
			//TODO: create this view
			render(view:"noVocabularies", model:[index:index])
			return
		}
		
		if(sparqlService.areVocabulariesSelected() == false) {
			log.info "no vocabulary is selected "
			//TODO: create the view (vocabSelectionList.gsp)
			render(view:"vocabSelectionList", model:[vocabs:vocabularies, index:index])
		} else {
			//render (view:"conceptBrowserWindow", model:[hiveQueryState:hiveQueryState, vocabs:vocabularies, currentVocab:currentVocab])
			forward(action:"conceptBrowser", model:[hiveQueryState:hiveQueryState, vocabs:vocabularies, currentVocab:currentVocab, index:index])
			forward(action:"conceptBrowserPivotView", model:[index:index])
		}
		
	}
	
	/**
	 * Show the full concept browser view
	 * @return
	 */
	def conceptBrowser() {
		log.info("conceptBrowserFull()")
		log.info(params)
		
		def indexLetter = params['indexLetter']
		def targetUri = params['targetUri']
		
		def mav = conceptBrowserViewBuilder(true, indexLetter, targetUri)
		render (view:mav.view, model:mav.model)
	}
	
	def conceptBrowserPivotView() {
		log.info("conceptBrowserPivotView()")
		log.info(params)

		def indexLetter = params['indexLetter']
		def targetUri = params['targetURI']
		
		
		def mav = conceptBrowserViewBuilder(false, indexLetter, targetUri)
		if (params['index'] != null) {
			def index = params['index']
			mav.model.put("index", index)
		}
		
		render (view:mav.view, model:mav.model)
	}
	
	/**
	 * Show the concept browser given the selected vocabularies
	 * @return
	 */
	private ViewNameAndModelValues conceptBrowserViewBuilder(full, indexLetter, targetUri) {
		log.info("conceptBrowserBuilder()")
		
		def hiveQueryState = sparqlService.retrieveHiveQueryState()
		def conceptProxy
		
		if(!indexLetter) {
			indexLetter = "A"
		}
		
		if(!targetUri) {
			targetUri = ""
		}
		
		log.info("getting concept proxy for display...")
		
		if(targetUri) {
			log.info("have target uri, make this the current:${targetUri}")
			conceptProxy = sparqlService.getConceptByUri(targetUri, irodsAccount)
		} else if(hiveQueryState.currentConceptURI) {
			log.info("have a current uri, redisplay this information:${hiveQueryState.currentConceptURI}")
			conceptProxy = sparqlService.getConceptByUri(hiveQueryState.currentConceptURI, irodsAccount)
		} else {
			// no current or desired uri, select the top level of the current vocabulary
			def currentVocab = sparqlService.getCurrentVocabularySelection()
			if(!currentVocab) {
				log.error("no vocabulary is selected or possible to select")
				response.sendError(500, message(code:"error.no.vocabulary.selected" ))
				return
			}
			log.info("getting top level for:${currentVocab}")
			conceptProxy = sparqlService.getTopLevelConceptProxyForVocabulary(currentVocab, irodsAccount, indexLetter)
		}
		
		ViewNameAndModelValues modelAndView = new ViewNameAndModelValues()
		def modelMap = [hiveQueryState:hiveQueryState, vocabularySelections:sparqlService.retrieveVocabularySelectionListing(), conceptProxy:conceptProxy]
		modelAndView.model = modelMap
		
		if(full) {
			modelAndView.view = "conceptBrowserWindow"
		} else {
			modelAndView.view = "conceptBrowserTermsOnly"
		}
		log.info("concept proxy:${conceptProxy}")
		log.info("skosCode:${conceptProxy.skosCode}")
		
		return modelAndView	
	}
	
	/**
	 * Update with the selected vocabularies
	 * @return
	 */
	def selectVocabularies() {
		log.info("selectVocabularies()")
		log.info(params)

		def selected = params['selectedVocab']
		// TODO: list versus object
		
		log.info "vocab list: ${selected}"

		if (selected instanceof Object[]) {
			// ok
			log.info "yes, selected is instance of Object[]"
		} else {
//			selected = [selected]
			log.info "selected = [selected]"
		}


		sparqlService.selectVocabularies(selected)
		def hiveQueryState = sparqlService.retrieveHiveQueryState()

		forward(action:"showConceptBrowser")


	}
	
	/**
	 * Set the concept browser to display the top level of the given vocabulary
	 * @return
	 */
	def resetConceptBrowser() {
		log.info("resetConceptBrowser")
		log.info(params)

		def vocabulary = params['vocabulary']
		log.info "vocabulary selected: ${vocabulary}"
		def hiveQueryState = sparqlService.retrieveHiveQueryState()
		def vocab = hiveQueryState.currentVocabulary
		log.info "currentVocabulary: ${vocabulary}"

		if (!vocabulary) {
			vocabulary = hiveQueryState.currentVocabulary
			
		}
		
		def index = params['index']

//		if (!vocabulary) {
//			log.error("no vocabulary is selected or possible to select")
//			response.sendError(500, message(code:"error.no.vocabulary.selected" ))
//			return
//		}
		def indexLetter = 'A'
		sparqlService.getTopLevelConceptProxyForVocabulary(vocabulary,irodsAccount,indexLetter)
		forward(action:"conceptBrowserPivotView",model:[index:index])
		forward(action:"conceptBrowser", model:[currentVocab:vocab, index:index])

	}
	
	/**
	 * pickup a term to form the sparql query
	 * @return
	 */
	def pickupHiveTerm() {
		log.info("picupHiveTerm()")
		log.info(params)
		
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
		
		def preLabel = params['preLabel']
		if(preLabel == null || preLabel == "") {
			log.error "no preLabel in request"
			def message = message(code:"error.no.prelabel.provided")
			response.sendError(500,message)
			return
		}
		log.info "preLabel: ${preLabel}"
		
		Integer i = params.int('index')
		
		log.info("index: ${i}")
		
		def size = sparqlService.getQueryVocabularyItemListSize()
		log.info "size of query vocabulary item list: ${size}"
		def div_id = "term_" + size
		log.info "div_id: ${div_id}"
		
		if (i == null) {
			log.info "add new term, picking up Hive term..."
			log.info "add term to hive query..."
			sparqlService.addTermToQuery(vocabulary, targetUri, preLabel)		
		
		} else if (i < size) {
			log.info ("edit existing term in query...")
			
			sparqlService.editExistingTerm(vocabulary, targetUri, preLabel, i)
		}
		
			log.info "getting the concept proxy for the picked term "
			def conceptProxy = sparqlService.getConceptByUri(targetUri, irodsAccount)
			def hiveQueryState = sparqlService.retrieveHiveQueryState()
						
			forward(action:"searchPanel", model:[preLabel: preLabel, div_id:div_id])
		
		
		
	}
	
	def addMoreRows() {
		log.info("addMoreRows()")
		log.info(params)
		
		def String divId = params['div_id']
//		if(divId == null || divId == "") {
//			log.error "no id"
//			def message = message(code:"error.no.id.provided")
//			response.sendError(500,message)
//			return
//		}
		
		log.info "id: ${divId}"
		
		//TODO: divId = HiveQueryItem.size + 1
		render(template:"selectUnit", model: [divId:divId])
		}
	
	def submitRow() {
		log.info("submitRow()")
		log.info(params)
		
		def types = params['types[]']
		def termUri = params['uri']
		def connector = params['cnt']
		log.info("${types}")
		
		if (types instanceof Object[]) {
			log.info ("types is instance of []")
			sparqlService.addSearchTypes(types, termUri)
		} else {
			log.info ("types is not instance of [], change it to []")
			def type_list = new String[1]
			type_list[0] = types
			log.info ("type_list: ${type_list}")
			
			if (type_list instanceof Object[]) {
				log.info("type_list becomes []")
				sparqlService.addSearchTypes(type_list, termUri)
			} else {
			log.info("type_list did not become []")
			}
			
		}
		
		log.info ("adding search types ${types} to term: ${termUri}")
		log.info ("adding connectors: ${connector} to term")
		
		sparqlService.addConnector(connector, termUri)
		
		forward(action:"searchPanel")
		
	}
	
	def deleteSearchTerm() {
		log.info ("deleteSearchTerm()")
		log.info (params)
		
		//def termUri = params['uri']
		//def index = params['index']
		
		Integer index = params.int('index')
		
		if(index == 0) {
			log.info("index == 0")
		}
		
		sparqlService.deleteQueryTerm(index)
		
		forward(action:"searchPanel")
		//render(view: "_hiveQuery")
	}
	
	def showEditablePanel() {
		
		log.info("editRow()")
		log.info(params)
		
		def index = params['index']
		
		log.info("${index}")
		
		render(template:"editableRow", model:[index:index])
		
	}
	
	
	
	
	

}
