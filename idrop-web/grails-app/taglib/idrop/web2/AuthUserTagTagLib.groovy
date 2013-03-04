package idrop.web2

import org.irods.jargon.core.connection.IRODSAccount;

class AuthUserTagTagLib {
	def accountInfo = {attrs, body ->
		IRODSAccount irodsAccount = (IRODSAccount) session["SPRING_SECURITY_CONTEXT"]
		if (irodsAccount != null) {
			out << irodsAccount.userName + ":" + irodsAccount.zone
		}
	} 

}
