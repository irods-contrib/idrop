<!DOCTYPE html>
<html lang="en">
  <head>
  <g:render template="/common/cssAndJs"/>
  <!--  preserve the application context as a js variable for use in AJAX callbacks -->
  <script type="text/javascript">
          context = "${request.contextPath}";
          context = context.replace("/null", "");
          scheme = "${request.scheme}";
          host = "${request.localName}";
          port = "${request.localPort}";

          $(function(){
                  // Keep a mapping of url-to-container for caching purposes.
                  var cache = {
                  // If url is '' (no fragment), display this div's content.
                  '': $('.bbq-default')
                  };


                   // Bind an event to window.onhashchange that, when the history state changes,
                    // gets the url from the hash and displays either our cached content or fetches
                    // new content to be displayed.
                    $(window).bind( 'hashchange', function(e) {
			  
                                  processStateChange( $.bbq.getState());
			   
                    });

                    jQuery.i18n.properties({
                              name:'messages', 
                              path:'js/bundles/', 
                              mode:'both'
                          });
		  
		
          });
	
  </script>
</head>
<body style="height:100%;overflow:visible;">
 
   <g:render template="/common/topbar"/>
   <div id="defaultDialogDiv"><!-- default for general jquery dialogs --></div>
 	<div class="container-fluid">
   
	     <div class="row-fluid">
	          <div id="mainDivCol1" class="span10"><!-- PUT MAIN COLUMN 1 CODE HERE -->
	            <g:layoutBody />
	          </div>
	          <div id="secondaryDiv" class="span2"><!-- PUT MAIN COLUMN 2 CODE HERE -->
		            <g:ifAuthenticated>
		              <g:render template="/common/secondarymain"/>
		            </g:ifAuthenticated>
	      	 </div>
	    </div>

	 	<div class="row-fluid"><!-- PUT FOOTER CODE HERE -->
		 	<div class="span12">
		    	<g:render template="/common/footer" />
		    </div>
	 	 </div>
	 	 
	 </div>
 
</body>
</html>