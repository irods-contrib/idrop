class UrlMappings {

	static mappings = {

		"/login"(resource:"login")

		"/collection/$virtualCollection"(resource:"collection")

		"/collection"(resource:"collection")


		"/virtualCollection/$name"(resource:"virtualCollection")

		"/download"(resource:"download")

		"/$controller/$action?/$id?"{ constraints {
				// apply constraints here
			} }

		"500"(controller: "error")

		//		"/"(uri:"/index.html")
	}
}
