package org.irods.mydrop.service

import org.irods.jargon.hive.service.VocabularyService
import org.irods.mydrop.hive.HiveState
import org.irods.mydrop.hive.VocabularySelection

class HiveService {

	VocabularyService vocabularyService

	static transactional = false
	static HIVE_STATE = "HiveState"

	/**
	 * Retrieve a <code>VocabularySelection</code> that holds all HIVE vocabularies, and indicates which one is selected
	 * @return
	 */

	public HiveState retrieveHiveState() {
		HiveState hiveState = session[HIVE_STATE]
		if (!hiveState) {
			hiveState = new HiveState()
			session[HIVE_STATE] = hiveState
		}

		return hiveState
	}

	public storeHiveState(HiveState hiveState) {
		session[HIVE_STATE] = hiveState
	}

	public List<VocabularySelection> retrieveVocabularySelectionListing() {
		log.info("retrieveVocabularySelectionListing")
		HiveState hiveState = this.retrieveHiveState()

		if(hiveState.vocabularies.size == 0) {
			log.info("attempting to retrieve vocabs")
			hiveState.vocabularies = vocabularyService.allVocabularyNames()
			log.info("retrieved all vocabs:${hiveState.vocabularies}")
		}

		def vocabularySelections = new ArrayList<VocabularySelection>()

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

		return vocabularySelections
	}
}
