package org.irods.mydrop.hive


/**
 * State information about the status of HIVE (selected vocabularies in use, etc)
 * @author Mike Conway - DICE (www.irods.org)
 *
 */
class HiveState {

	/**
	 * all vocabularies in HIVE
	 */
	String[] vocabularies = []
	
	/**
	 * all vocabularies in HIVE that have been selected as being of interest 
	 */
	String[] selectedVocabularies = []
	
	/**
	 * Current vocabulary in view
	 */
	String currentVocabulary = ""
	
	/**
	 * Current concept that is being worked with
	 */
	String currentConceptLabel = ""
	String currentConceptURI = ""
	
}
