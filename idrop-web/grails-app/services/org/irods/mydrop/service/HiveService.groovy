package org.irods.mydrop.service

import org.irods.jargon.hive.service.VocabularyService
import org.irods.mydrop.hive.HiveState
import org.irods.mydrop.hive.VocabularySelection

class HiveService {

	HiveStateService hiveStateService
	VocabularyService vocabularyService

	static transactional = false

	/**
	 * Retrieve a <code>VocabularySelection</code> that holds all HIVE vocabularies, and indicates which one is selected
	 * @return
	 */
	public List<VocabularySelection> retrieveVocabularySelectionListing() {
		log.info("retrieveVocabularySelectionListing")
		HiveState hiveState = hiveStateService.retrieveHiveState()

		if(hiveState.vocabularies.size == 0) {
			log.info("attempting to retrieve vocabs")
			hiveState.vocabularies = vocabularyService.allVocabularyNames()
			log.info("retrieved all vocabs:${hiveState.vocabularies}")
		}

		List<VocabularySelection> vocabularySelections = new ArrayList<VocabularySelection>()

		vocabularies.each{
			// see if the vocabulary term is selected by looking at HiveState selected vocabularies


		}
	}
}
