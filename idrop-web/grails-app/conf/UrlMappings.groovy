class UrlMappings {

	static mappings = {

		"/login/authenticate/*"(controller:"login"){
			action = [GET:"login", POST:"authenticate"]
		}

		//"/login/authenticate/normalLogin"(controller:"login", action:'login')

		"/$controller/$action?/$id?"{ constraints {
				// apply constraints here
			} }

		"/file/**" (controller:"file", action:"index"){

		}

		// FIXME: temp hack
		"/home/null/file/**" (controller:"file", action:"index"){

		}

		"/$controller/$action?"{ constraints {
				// apply constraints here
			} }

		"/"(controller:"home", action:"index")

		"500"(view:'/error')
	}
}
