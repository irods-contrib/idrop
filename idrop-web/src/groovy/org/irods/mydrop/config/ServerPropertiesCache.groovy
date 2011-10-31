package org.irods.mydrop.config

import java.util.concurrent.ConcurrentHashMap

import org.irods.jargon.core.connection.IRODSAccount

class ServerPropertiesCache implements Serializable {

	HashMap<IRODSAccount, ServerProperties> serverProperties = new ConcurrentHashMap<IRODSAccount, ServerProperties>()
	
	/**
	 * Retrieve the <code>SgerverProperties</code> for the given <code>IRODSAccount</code>.  This will create an entry in the 
	 * cache if not found
	 */
	public ServerProperties getServerProperties(IRODSAccount irodsAccount) {
		ServerProperties thisServerProperties = serverProperties.get(irodsAccount)
		if (!thisServerProperties) {
			thisServerProperties = new ServerProperties()
			thisServerProperties.irodsAccount = irodsAccount
			this.serverProperties.put(irodsAccount, thisServerProperties)
		}
		return thisServerProperties
	}
	
}
