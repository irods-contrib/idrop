package org.irods.mydrop.config

import java.util.concurrent.ConcurrentHashMap

import org.irods.jargon.core.connection.IRODSAccount

class ServerProperties {

	IRODSAccount irodsAccount
	HashMap<String, String> properties = new ConcurrentHashMap<String, String>()
	
}
