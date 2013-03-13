package org.irods.mydrop.service

import javax.servlet.http.HttpSession

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.exception.JargonException
import org.irods.jargon.core.pub.CollectionAndDataObjectListAndSearchAO
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.pub.domain.ObjStat
import org.irods.jargon.core.query.MetaDataAndDomainData.MetadataDomain
import org.irods.jargon.hive.irods.HiveVocabularyEntry
import org.irods.jargon.hive.irods.IRODSHiveService
import org.irods.jargon.hive.irods.IRODSHiveServiceImpl
import org.irods.jargon.hive.irods.exception.IRODSHiveException
import org.irods.jargon.hive.service.VocabularyService
import org.irods.mydrop.hive.HiveState
import org.irods.mydrop.hive.VocabularySelection
import org.springframework.web.context.request.RequestContextHolder
import org.unc.hive.client.ConceptProxy


class HiveService {

	VocabularyService vocabularyService
	IRODSAccessObjectFactory irodsAccessObjectFactory

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
	 * Add the given term as iRODS metadata, returning the <code>ConceptProxy</code> that represents the new term
	 * @param uri <code>String</code> with the URI for the vocabulary term
	 * @param irodsAbsolutePath <code>String</code> with the absolute path to the iRODS file or collection
	 * @param vocabulary <code>String</code> with the vocabulary name that contains the given term
	 * @param irodsAccount {@link IRODSAccount} representing the server connection
	 * @return {@link ConceptProxy} with information on the term just added to iRODS
	 * @throws FileNotFoundException
	 * @throws IRODSHiveException
	 */
	public ConceptProxy applyVocabularyTerm(final String uri, final String irodsAbsolutePath, final String vocabulary, final String comment, final IRODSAccount irodsAccount)
	throws FileNotFoundException, IRODSHiveException {

		log.info("applyVocabularyTerm()")

		if (uri == null || uri == "") {
			throw new IllegalArgumentException("null or empty uri")
		}

		if (irodsAccount == null) {
			throw new IllegalArgumentException("null irodsAccount")
		}

		if (irodsAbsolutePath == null || irodsAbsolutePath == "") {
			throw new IllegalArgumentException("null or empty irodsAbsolutePath")
		}

		if (vocabulary == null || vocabulary == "") {
			throw new IllegalArgumentException("null or empty vocabulary")
		}

		if (comment == null ) {
			throw new IllegalArgumentException("null comment")
		}

		try {

			ConceptProxy conceptProxy = getConceptByUri(uri, irodsAbsolutePath, irodsAccount)

			if (conceptProxy.selected) {
				log.info("shows already selected, ignore")
				return
			}

			IRODSHiveService irodsHiveService = new IRODSHiveServiceImpl(irodsAccessObjectFactory, irodsAccount)
			CollectionAndDataObjectListAndSearchAO collectionAndDataObjectListAndSearchAO = irodsAccessObjectFactory.getCollectionAndDataObjectListAndSearchAO(irodsAccount)
			ObjStat objStat = collectionAndDataObjectListAndSearchAO.retrieveObjectStatForPath(irodsAbsolutePath)
			HiveVocabularyEntry hiveVocabularyEntry = new HiveVocabularyEntry()
			hiveVocabularyEntry.domainObjectUniqueName = irodsAbsolutePath
			hiveVocabularyEntry.preferredLabel = conceptProxy.preLabel
			hiveVocabularyEntry.termURI = conceptProxy.URI
			hiveVocabularyEntry.vocabularyName = conceptProxy.origin.toLowerCase()
			hiveVocabularyEntry.comment = comment

			if (objStat.isSomeTypeOfCollection()) {
				hiveVocabularyEntry.setMetadataDomain(MetadataDomain.COLLECTION)
			} else {
				hiveVocabularyEntry.setMetadataDomain(MetadataDomain.DATA)
			}

			log.info("getting ready to add vocabulary entry:${hiveVocabularyEntry}")
			irodsHiveService.saveOrUpdateVocabularyTerm(hiveVocabularyEntry)
			log.info("added vocabulary entry")

			conceptProxy.selected = true
			return conceptProxy
		} catch (FileNotFoundException fnf) {
			log.error("file not found exception for path");
			throw fnf
		} catch (JargonException je) {
			log.error("Jargon exeption occurred saving metadata", je)
			throw new IRODSHiveException("exception in HIVE processing", je)
		}
	}


	/**
	 * Pivot the concept browser with the given URI as the new 'current'
	 * @param uri
	 * @return
	 */
	public ConceptProxy getConceptByUri(final String uri, final String irodsAbsolutePath, final IRODSAccount irodsAccount) {
		log.info("getConceptByUri()")
		if (uri == null || uri == "") {
			throw new IllegalArgumentException("null or empty uri")
		}

		if (irodsAccount == null) {
			throw new IllegalArgumentException("null irodsAccount")
		}

		if (irodsAbsolutePath == null || irodsAbsolutePath == "") {
			throw new IllegalArgumentException("null or empty irodsAbsolutePath")
		}

		//TODO:make this a utility in jargon-hive?
		int poundIdx = uri.indexOf('#')
		if (poundIdx == -1) {
			throw new IllegalArgumentException("not able to split namespace and local part from uri")
		}

		def namespace = uri.substring(0, poundIdx + 1)
		def localPart = uri.substring(poundIdx + 1).trim()
		log.info("namespace: ${namespace} local: ${localPart}")

		ConceptProxy proxy = vocabularyService.getConceptByURI(namespace, localPart)
		augmentConceptProxyWithIRODSInfo(proxy,irodsAbsolutePath, irodsAccount)

		def hiveState = retrieveHiveState()
		hiveState.currentConceptLabel = proxy.preLabel
		hiveState.currentConceptURI = proxy.URI
		log.info("have proxy, current lable is now ${proxy.preLabel} at uri: ${proxy.URI}")
		return proxy

	}

	/**
	 * Get the top level set of concepts for the given vocabulary, this resets the hiveState to the top of the
	 * given vocabulary 
	 * @param vocabularyName <code>String</code> with the vocabulary name.  If blank, the current vocabulary in the
	 * hive state will be used
	 * @return
	 */
	public ConceptProxy getTopLevelConceptProxyForVocabulary(final String vocabularyName, final String irodsAbsolutePath, final IRODSAccount irodsAccount) {
		log.info("getTopLevelConceptProxyForVocabulary")

		if (irodsAccount == null) {
			throw new IllegalArgumentException("null irodsAccount")
		}

		if (irodsAbsolutePath == null || irodsAbsolutePath == "") {
			throw new IllegalArgumentException("null or empty irodsAbsolutePath")
		}


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
		def conceptProxy = vocabularyService.getConceptProxyForTopOfVocabulary(vocabularyName, "", true)
		augmentConceptProxyWithIRODSInfo(conceptProxy,irodsAbsolutePath, irodsAccount)
		return conceptProxy

	}

	void augmentConceptProxyWithIRODSInfo(final ConceptProxy conceptProxy, final String irodsAbsolutePath, final IRODSAccount irodsAccount) throws FileNotFoundException, IRODSHiveException {
		log.info("augmentConceptProxyWithIRODSInfo")

		if (!conceptProxy) {
			throw new IllegalArgumentException("missing conceptProxy")
		}

		if (!irodsAccount) {
			throw new IllegalArgumentException("missing irodsAccount")
		}

		log.info("conceptProxy:${conceptProxy}")

		IRODSHiveService irodsHiveService = new IRODSHiveServiceImpl(irodsAccessObjectFactory, irodsAccount)

		if (conceptProxy.URI) {
			log.info("have uri, looking up iRODS data ${conceptProxy.URI}")
			def hiveVocabularyEntry = irodsHiveService.findHiveVocabularyEntryForPathAndURI(irodsAbsolutePath, conceptProxy.URI)
			if (hiveVocabularyEntry) {
				log.info("set selected, found entry:${hiveVocabularyEntry}")
				conceptProxy.selected = true
			}
		}

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
