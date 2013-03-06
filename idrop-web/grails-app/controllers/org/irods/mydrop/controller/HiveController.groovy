package org.irods.mydrop.controller

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.hive.service.VocabularyService
import org.unc.hive.client.ConceptProxy


class HiveController {

	IRODSAccessObjectFactory irodsAccessObjectFactory
	IRODSAccount irodsAccount
	VocabularyService vocabularyService

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
	 * Sbow initial HIVE view, which should reflect the selected set of vocabularies, and show a list of all vocabularies 
	 * @return
	 */
	def index() {
		log.info("getting vocab names")
		List<String> vocabs = vocabularyService.getAllVocabularyNames()
		render(view:"vocabList", model:[vocabs:vocabs])
	}

	/**
	 * Show the concept browser given the selected vocabularies
	 * @return
	 */
	def conceptBrowser() {
		log.info("conceptBrowser")
		log.info(params)
		def selected = params['selectedVocab']
		def vocabs = []

		if (!selected) {
			log.info("no vocabs to display")
			render(view:"conceptBrowser", model:[vocabs:vocabs])
			return
		}

		if (selected instanceof Object[] || selected instanceof List) {
			log.info "is array"
			vocabs = selected
		} else {
			vocabs.add(selected)
		}
		
		render(view:"conceptBrowser", model:[vocabs:vocabs])
	}


		def showTreeForSelectedVocabularies(){
			log.info("getting first set of concepts")
			List<ConceptProxy> subTopConcept = vocabularyService.getSubTopConcept(params['selectedVocab'].toString().toLowerCase() , "A" , true)
			//params['selectedVocab']
			int sizeOfSubTopConcept = subTopConcept.size()
			List<String> listOfPreferedLabels = new ArrayList<String>()
			for (ConceptProxy concept : subTopConcept){
				listOfPreferedLabels.add(concept.preLabel)
			}
			render(view:"vocabListing", model:[listOfPreferedLabels:listOfPreferedLabels])
		}



		def deleteSelectedVocabularies = {
			log.info("deleteSelectedVocabularies")

			log.info("params: ${params}")

			def vocabulariesToDelete = params['selectedVocab']

			// if nothing selected, just jump out and return a message
			if (!ticketsToDelete) {
				log.info("no vocabularies to delete")
				render "OK"
				return
			}

			log.info("vocabularies: ${vocabulariesToDelete}")
			if (vocabulariesToDelete instanceof Object[]) {
				log.debug "is array"
				vocabulariesToDelete.each{
					log.info "vocabulariesToDelete: ${it}"
					//ticketAdminService.deleteTicket(it)
					//log.info("deleted:${it}")
				}
			} else {
				log.debug "not array"
				log.info "deleting: ${vocabulariesToDelete}..."

			}

			render "OK"
		}





	}
