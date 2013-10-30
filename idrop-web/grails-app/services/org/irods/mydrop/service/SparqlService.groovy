package org.irods.mydrop.service

import java.io.FileNotFoundException;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.hive.irods.IRODSHiveService
import org.irods.jargon.hive.irods.exception.IRODSHiveException;
import org.irods.jargon.hive.query.HiveQuery
import org.irods.jargon.hive.query.HiveQueryVocabularyItem
import org.irods.jargon.hive.query.HiveQueryVocabularyItem.ConnectorTypeEnum
import org.irods.jargon.hive.query.VocabularyItemSearchType
import org.irods.jargon.hive.service.VocabularyService;
import org.irods.mydrop.hive.HiveQueryState
import org.irods.mydrop.hive.VocabularySelection;
import org.springframework.web.context.request.RequestContextHolder
import org.unc.hive.client.ConceptProxy

class SparqlService {
	
	VocabularyService vocabularyService
	IRODSAccessObjectFactory irodsAccessObjectFactory
	HiveQuery hiveQuery

	static transactional = false
	static scope = "session"
	static HIVE_QUERY_STATE = "hiveQueryState"
	
	public HiveQueryState retrieveHiveQueryState() {
		HiveQueryState hiveQueryState = getSession()[HIVE_QUERY_STATE]
		if (!hiveQueryState) {
			hiveQueryState = new HiveQueryState()
			getSession()[HIVE_QUERY_STATE] = hiveQueryState
		}
		
		return hiveQueryState
	}
	
	public storeHiveQueryState(HiveQueryState hiveQueryState) {
		getSession()[HIVE_QUERY_STATE] = hiveQueryState
	}
	
	private HttpSession getSession() {
		return RequestContextHolder.currentRequestAttributes().getSession()
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
			HiveQueryState hiveQueryState = this.retrieveHiveQueryState()
			log.info("hiveQueryState vocabularies: ${hiveQueryState.vocabularies}")
			if(hiveQueryState.vocabularies.size() == 0) {
				log.info("attempting to retrieve vocabs")
				hiveQueryState.vocabularies = vocabularyService.getAllVocabularyNames()
				
				log.info("retrieved all vocabs:${hiveQueryState.vocabularies}")
			}
			hiveQueryState.vocabularies.each{
				//check to see if it is in the hive state selected vocabularies table
				def vocabularySelection = new VocabularySelection()
				if (hiveQueryState.selectedVocabularies.contains(it)) {
					vocabularySelection.selected = true
				}

				// add an entry to VocabularySelection for all vocabs and set selected true for all vocabs

				vocabularySelection.vocabularyName = it
				//vocabularySelection.selected = true
				vocabularySelections.add(vocabularySelection)
			}
		}
		
		def size = vocabularySelections.size
		log.info("vocabularySelections: ${size}")
		return vocabularySelections
	}
	
	/**
	 * Find the current selected vocabulary, if none is set, pick the first selected vocabulary as current
	 * @return current vocabulary, or blank if none can be decided
	 */
	public String getCurrentVocabularySelection() {
		log.info("getTopLevelConceptProxyForVocabulary")
		def hiveQueryState = retrieveHiveQueryState()
		def current = hiveQueryState.currentVocabulary

		if (current == "") {
			log.info("no current set, look at selected vocabs and pick first one")
			if (hiveQueryState.vocabularies.size() > 0) {
				current = hiveQueryState.vocabularies[0]
			}
		}

		log.info("picked current as:${current}")
		return current
	}
	
	/**
	 * See if any vocabularies are currently selected
	 * @return
	 */
	public boolean areVocabulariesSelected() {
		boolean isAnyVocabularySelected = false
		log.info("if any vocabularies are selected ")
		List<VocabularySelection> vocabularySelections = retrieveVocabularySelectionListing()
		vocabularySelections.each {
			if (it.selected==true) {
				isAnyVocabularySelected=true
			}
		}
		return isAnyVocabularySelected
	}
	
	/**
	 * Pivot the concept browser with the given URI as the new 'current'
	 * @param uri
	 * @return
	 */
	public ConceptProxy getConceptByUri(final String uri, final IRODSAccount irodsAccount) {
		log.info("getConceptByUri")
		
		if(uri == null || uri == "") {
			throw new IllegalArgumentException("null or empty uri")
		}
		
		if(irodsAccount == null) {
			throw new IllegalArgumentException("null irodsAccount")
		}
		
		int poundIdx = uri.indexOf('#')
		if(poundIdx == -1) {
			throw new IllegalArgumentException("not able to split namespace and local part from uri")
		}
		
		def namespace = uri.substring(0, poundIdx + 1)
		def localPart = uri.substring(poundIdx + 1).trim()
		log.info("namespace: ${namespace} local: ${localPart}")
		
		ConceptProxy proxy = vocabularyService.getConceptByURI(namespace, localPart)
		log.info("vacabulary name of term is ${proxy.origin}")
		
		def hiveQueryState = retrieveHiveQueryState()
		hiveQueryState.currentConceptLabel = proxy.preLabel
		hiveQueryState.currentConceptURI = proxy.URI
		log.info("have proxy, current lable is now ${proxy.preLabel} at uri: ${proxy.URI}")
		return proxy
	}
	
	/**
	 * Get the top level set of concepts for the given vocabulary, this resets the hiveQueryState to the top of the
	 * given vocabulary
	 * @param vocabularyName <code>String</code> with the vocabulary name.  If blank, the current vocabulary in the
	 * hive query state will be used
	 * @return
	 */
	public ConceptProxy getTopLevelConceptProxyForVocabulary(final String vocabularyName, final IRODSAccount irodsAccount, final indexLetter) {
		log.info("getTopLevelConceptProxyForVocabulary")
		
		if (irodsAccount == null) {
			throw new IllegalArgumentException("null irodsAccount")
		}
		
		def current = ""
		
		if(vocabularyName == null || vocabularyName == "") {
			log.info("no given vocab names, try and find current in hiveQueryState")
			current = getCurrentVocabularySelection()
			log.info("found ${current}")
		} else {
			current = vocabularyName
		}
		
		if(current == "") {
			log.info("no current vocabulary name")
			return
		}
		
		// have a current vocab, get a list of concept proxies for the vocab under a default concept proxy
		def hiveQueryState = retrieveHiveQueryState()
		hiveQueryState.currentConceptLabel = ""
		hiveQueryState.currentConceptURI = ""
		hiveQueryState.currentVocabulary = current
		log.info("getting top concept proxy for vocabulary:${current}, will set hiveQueryState to top of this vocab")
		def conceptProxy = vocabularyService.getConceptProxyForTopOfVocabulary(vocabularyName, indexLetter, true)
		return conceptProxy
	}
	
	public void selectVocabularies(String[] vocabularyNames) {
		log.info("selectVocabularies")

		if (vocabularyNames == null) {
			throw new IllegalArgumentException("null vocabularyNames")
		}

		synchronized(this) {
			def hiveQueryState = retrieveHiveQueryState()
			hiveQueryState.selectedVocabularies = vocabularyNames
			hiveQueryState.currentVocabulary = vocabularyNames[0]
			log.info("currentVocabulary: " + hiveQueryState.currentVocabulary)
			// later be smart about clearing selected vocab and current term
		}
	}
	
	/**
	 * Associated the pickup term to the HiveQuery class
	 */
	public void addTermToQuery(final String vocabularyName, final String uri, final String preLabel) {
		log.info("addTermToQuery()")
		
		if(vocabularyName == null || vocabularyName == "") {
			throw new IllegalArgumentException("null or empty vocabulary name")
		}
		
		if(uri == null || uri == "") {
			throw new IllegalArgumentException("null or empty uri")
		}
		
		if(preLabel == null || preLabel == "") {
			throw new IllegalArgumentException("null or empty preferred label")
		}
		
		def hiveQueryVocabItem = new HiveQueryVocabularyItem()
		hiveQueryVocabItem.setVocabularyName(vocabularyName)
		hiveQueryVocabItem.setVocabularyTermURI(uri)
		hiveQueryVocabItem.setPreferredLabel(preLabel)
		
		log.info("add the term to hive query...")
		def hiveQueryState = retrieveHiveQueryState()
		
		if(hiveQueryState.hiveQuery == null) {
			log.info("hiveQueryState.hiveQuery is null, new a HiveQuery")
			hiveQueryState.hiveQuery = new HiveQuery()
			hiveQuery = hiveQueryState.hiveQuery
		}
		
		hiveQueryState.hiveQuery.hiveQueryVocabularyItems.add(hiveQueryVocabItem)
		
		def vocabItems_size = hiveQueryState.hiveQuery.hiveQueryVocabularyItems.size()
		log.info("size of hiveQueryVocabularyItems: ${vocabItems_size}")
	
	} 
	
	public int getQueryVocabularyItemListSize() {
		log.info("getQueryVocabularyItemListSize()")
		
		def hiveQueryState = retrieveHiveQueryState()
		
		if(hiveQuery == null) {
			log.info("hiveQueryState.hiveQuery is null, new a HiveQuery")
			//hiveQueryState.hiveQuery = new HiveQuery()
			hiveQuery = new HiveQuery()
			hiveQueryState.hiveQuery = hiveQuery
			
			return 0
		}
		
		def hiveQueryVocabularyItemList = new ArrayList<HiveQueryVocabularyItem>()
		hiveQueryVocabularyItemList = hiveQueryState.hiveQuery.hiveQueryVocabularyItems
		
		def vocabItems_size = hiveQueryVocabularyItemList.size()
		
		log.info("size of hiveQueryVocabularyItems: ${vocabItems_size}")
		return vocabItems_size
	}
	
	
	public List<HiveQueryVocabularyItem> retrieveHiveQueryVocabularyItemList() {
		log.info("getHiveQueryVocabularyItemList()")
		
		synchronized(this) {
		
			HiveQueryState hiveQueryState = this.retrieveHiveQueryState()
			def hiveQueryVocabularyItemList = new ArrayList<HiveQueryVocabularyItem>()
			
			if(hiveQueryState.hiveQuery == null) {
				log.info("hiveQuery is null")
				
				hiveQuery = new HiveQuery()
				hiveQueryState.hiveQuery = hiveQuery
				
				return hiveQueryVocabularyItemList
			} else{
				log.info("hiveQuery is not null")			
				
				hiveQueryState.hiveQuery.hiveQueryVocabularyItems.each{
					//check to see if it is in the hive state selected vocabularies table
					def hqVocabItem = new HiveQueryVocabularyItem()
					
					hqVocabItem =it
					hiveQueryVocabularyItemList.add(hqVocabItem)
					
			}
		}
			return hiveQueryVocabularyItemList
	}
	
	}
	
	public void addSearchTypes(String[] types, String uri) {
		log.info("addSearchTypes()")
		
		if (types == null || types.size() == 0) {
			throw new IllegalArgumentException("null types")
		}
		
		if(uri == null || uri == "") {
			throw new IllegalArgumentException("null or empty uri")
		} 
		
		def typeList = new ArrayList<VocabularyItemSearchType>()
		def findQueryItem = false
		
		synchronized(this) {
			def hiveQueryState = this.retrieveHiveQueryState()
			
			if(hiveQueryState.hiveQuery == null) {
				log.info("hiveQuery is null")
				return;
			}
			
			types.each {
				typeList.add(it)
			}
			
			def typeListSize = typeList.size()
			log.info("size of type list: ${typeListSize}")
			
			hiveQueryState.hiveQuery.hiveQueryVocabularyItems.each {
				if(it.vocabularyTermURI == uri) {
					it.searchTypes = typeList
					findQueryItem = true
				} 
			}
			
			if(!findQueryItem) {
				throw new RuntimeException("cannot find the correct hiveQueryVocabularyItem for setting the search types")
			}

		}
	}
	
	public void addConnector(String connector, String uri) {
		log.info("addConnector()")
		
		if(uri == null || uri == "") {
			throw new IllegalArgumentException("null or empty uri")
		}
		
		if (connector == null) {
			throw new IllegalArgumentException("null connector")
		}
		
		if(connector == "") {
			connector = "AND"
		}
		
		def findQueryItem = false
		
		synchronized(this) {
			def hiveQueryState = this.retrieveHiveQueryState()
			
			if(hiveQueryState.hiveQuery == null) {
				log.info("hiveQuery is null")
				return;
			}

			hiveQueryState.hiveQuery.hiveQueryVocabularyItems.each {
				if(it.vocabularyTermURI == uri) {
					if(connector == "" || connector == "AND") {
						it.connectorType = ConnectorTypeEnum.AND
					}
					
					if(connector == "OR") {
						it.connectorType = ConnectorTypeEnum.OR
					}
					findQueryItem = true
				}
			}
			
			if(!findQueryItem) {
				throw new RuntimeException("cannot find the correct hiveQueryVocabularyItem for setting the connector")
			}

		}
		
	}
	
	public void deleteQueryTerm (int index) {
		log.info("deleteQueryTerm()")
		
//		if(uri == null || uri == "") {
//			throw new IllegalArgumentException("null or empty uri")
//		}
		
//		if(index == null || index == "") {
//			throw new IllegalArgumentException("null or empty index")
//		}
		
		if(index == 0) {
			log.info("index == 0")
		}
		
		synchronized(this) {
			def hiveQueryState = this.retrieveHiveQueryState()
			
			if(hiveQueryState.hiveQuery == null) {
				log.info("hiveQuery is null")
				return;
			}
			
			if(index + 1 > getQueryVocabularyItemListSize()) {
				throw new RuntimeException("cannot find the correct hiveQueryVocabularyItem to delete")
			}

			hiveQueryState.hiveQuery.hiveQueryVocabularyItems.remove(index)
		}
		
	}
	
	public void editExistingTerm(final String vocabularyName, final String uri, final String preLabel, int index) {
		log.info("editExistingTerm()")
		
		if(vocabularyName == null || vocabularyName == "") {
			throw new IllegalArgumentException("null or empty vocabulary name")
		}
		
		if(uri == null || uri == "") {
			throw new IllegalArgumentException("null or empty uri")
		}
		
		if(preLabel == null || preLabel == "") {
			throw new IllegalArgumentException("null or empty preferred label")
		}
		
		if(index == null) {
			throw new IllegalArgumentException("null index")
		}
		
		synchronized(this) {
			def hiveQueryState = this.retrieveHiveQueryState()
			
			if(hiveQueryState.hiveQuery == null) {
				log.info("hiveQuery is null")
				return;
			}
			
			def hiveQueryVocabItem = new HiveQueryVocabularyItem()
			hiveQueryVocabItem.setVocabularyName(vocabularyName)
			hiveQueryVocabItem.setVocabularyTermURI(uri)
			hiveQueryVocabItem.setPreferredLabel(preLabel)
			
			
			hiveQueryState.hiveQuery.hiveQueryVocabularyItems[index] = hiveQueryVocabItem
			//hiveQueryState.hiveQuery.hiveQueryVocabularyItems.putAt(index, hiveQueryVocabItem)
			
			def hiveQueryItem = hiveQueryState.hiveQuery.hiveQueryVocabularyItems.get(index);
			def Label = hiveQueryItem.preferredLabel
			
			log.info("new preLabel is ${Label}")
		}
		
	}
	

	
		
}