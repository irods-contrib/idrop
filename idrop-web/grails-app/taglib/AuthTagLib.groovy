
import org.springframework.security.core.context.SecurityContextHolder 

class AuthTagLib {
	
	private boolean isAuthenticated() {
		//auth = session.SPRING_SECURITY_CONTEXT?.authentication?.authenticated
		def auth = session["SPRING_SECURITY_CONTEXT"]
		//log.info("auth is: ${auth}")
		return auth != null
	 }
	
	
	def ifAuthenticated = { attrs, body ->
		if (isAuthenticated()) {
		   out << body()
		}
	 }
	 
	 def ifNotAuthenticated = { attrs, body ->
		if (!isAuthenticated()) {
		   out << body()
		}
	 }

}
