package org.irods.mydrop.service

import javax.servlet.http.HttpSession

import org.irods.jargon.hive.service.VocabularyService
import org.irods.mydrop.hive.HiveState
import org.irods.mydrop.hive.VocabularySelection
import org.springframework.web.context.request.RequestContextHolder
import org.unc.hive.client.ConceptProxy

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

	/**
	 * Find the current selected vocabulary, if none is set, pick the first selected vocabulary as current
	 * @return current vocabulary, or blank if none can be decided
	 */
	public String getCurrentVocabularySelection() {
		log.info("getTopLevelConceptProxyForVocabulary")
		def hiveState = retrieveHiveState()
		def current = hiveState.currentVocabulary

		if (current == "") {
			log.info("no current set, look at selected vocabs and pick first one")
			if (hiveState.selectedVocabularies.size() > 0) {
				current = hiveState.selectedVocabularies[0]
			}
		}

		log.info("picked current as:${current}")
		return current
	}

	/**
	 * Get the top level set of concepts for the given vocabulary, this resets the hiveState to the top of the
	 * given vocabulary 
	 * @param vocabularyName <code>String</code> with the vocabulary name.  If blank, the current vocabulary in the
	 * hive state will be used
	 * @return
	 */
	public ConceptProxy getTopLevelConceptProxyForVocabulary(final String vocabularyName) {
		log.info("getTopLevelConceptProxyForVocabulary")

		def current = ""

		if (vocabularyName == null || vocabularyName == "") {
			log.info("no vocab selected, try and find current in hiveState")
			current = getCurrentVocabularySelection()
			log.info("found ${current}")
		} else {
			current = vocabularyName
		}

		if (current == "") {
			return null
		}

		// have a current vocab, get a list of concept proxies for the vocab under a default concept proxy
		def hiveState = retrieveHiveState()
		hiveState.currentConceptLabel = ""
		hiveState.currentConceptURI = ""
		hiveState.currentVocabulary = current
		log.info("getting top concept proxy for vocabulary:${current}, will set hiveState to top of this vocab")
		return vocabularyService.getConceptProxyForTopOfVocabulary(vocabularyName, "", true)

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

	/**
	 * See if any vocabularies are currently selected
	 * @return
	 */
	public boolean areVocabulariesSelected() {
		boolean isAnyVocabularySelected = false
		log.info("getting vocab names")
		List<VocabularySelection> vocabularySelections = retrieveVocabularySelectionListing()
		vocabularySelections.each {
			if (it.selected==true) {
				isAnyVocabularySelected=true
			}
		}
		return isAnyVocabularySelected
	}


	/**
	 * Retrieve a list of all vocabularies in a form that indicates the selected state of each vocabulary.
	 * <p/>
	 * Calling this method will also provision the complete list of available vocabularies from hive if they 
	 * are not currently persisted in the HiveState
	 * @return
	 */
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
