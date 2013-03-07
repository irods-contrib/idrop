package org.irods.mydrop.service

import org.irods.mydrop.hive.HiveState;

/**
 * Maintain state of the HIVE browser tools
 * 
 * @author Mike Conway - DICE (www.irods.org)
 *
 */
class HiveStateService {

	static transactional = false
	static HIVE_STATE = "HiveState"

	/**
	 * Get the HiveState and create one in session if not currently available
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
}
