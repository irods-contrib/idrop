class UrlMappings {

	static mappings = {
		
		"/login"(resource:"login")
		
		
		"/$controller/$action?/$id?"{ constraints { // apply constraints here
			} }

		"500"(controller: "error")

//		"/"(uri:"/index.html")
	}
}
