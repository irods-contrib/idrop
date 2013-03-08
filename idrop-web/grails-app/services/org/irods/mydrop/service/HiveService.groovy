package org.irods.mydrop.service

import javax.servlet.http.HttpSession

import org.irods.jargon.hive.service.VocabularyService
import org.irods.mydrop.hive.HiveState
import org.irods.mydrop.hive.VocabularySelection
import org.springframework.web.context.request.RequestContextHolder

class HiveService {

	VocabularyService vocabularyService

	static transactional = false
	static scope = "session"
	static HIVE_STATE = "hiveState"

	/**
	 * Retrieve a <code>VocabularySelection</code> that holds all HIVE vocabularies, and indicates which one is selected
	 * @return
	 */

	public HiveState retrieveHiveState() {
		HiveState hiveState = getSession()[HIVE_STATE]
		if (!hiveState) {
			hiveState = new HiveState()
			getSession()[HIVE_STATE] = hiveState
		}

		return hiveState
	}

	public storeHiveState(HiveState hiveState) {
		getSession()[HIVE_STATE] = hiveState
	}

	public void selectVocabularies(List<String> vocabularyNames) {
		log.info("selectVocabularies")

		if (vocabularyNames == null) {
			throw new IllegalArgumentException("null vocabularyNames")
		}

		synchronized(this) {
			def hiveState = retrieveHiveState()
			hiveState.selectedVocabularies = vocabularyNames
			// later be smart about clearing selected vocab and current term
		}
	}




	public List<VocabularySelection> retrieveVocabularySelectionListing() {
		log.info("retrieveVocabularySelectionListing")

		def vocabularySelections = new ArrayList<VocabularySelection>()
		synchronized(this) {
			HiveState hiveState = this.retrieveHiveState()
			if(hiveState.vocabularies.size() == 0) {
				log.info("attempting to retrieve vocabs")
				hiveState.vocabularies = vocabularyService.getAllVocabularyNames()
				log.info("retrieved all vocabs:${hiveState.vocabularies}")
			}

			hiveState.vocabularies.each{
				//check to see if it is in the hive state selected vocabularies table
				def vocabularySelection = new VocabularySelection()
				if (hiveState.selectedVocabularies.contains(it)) {
					vocabularySelection.selected = true
				}

				// add an entry to VocabularySelection for all vocabs and set boolean if selected

				vocabularySelection.vocabularyName = it
				vocabularySelections.add(vocabularySelection)
			}

		}
		return vocabularySelections
	}

	private HttpSession getSession() {
		return RequestContextHolder.currentRequestAttributes().getSession()
	}
}
