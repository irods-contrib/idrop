class BootStrap {

    def init = { servletContext ->
		
		environments {
			production {
				ctx.setAttribute("idropLiteUrl", "http://iren-web.renci.org/idrop-web/applet")
			}
			development {
				
			}
		}
	
    }
    def destroy = {
    }
}
