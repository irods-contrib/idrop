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
                 /* var cache = {
                  // If url is '' (no fragment), display this div's content.
                  '': $('.bbq-default')
                  };*/


                   // Bind an event to window.onhashchange that, when the history state changes,
                    // gets the url from the hash and displays either our cached content or fetches
                    // new content to be displayed.
                 /*   $(window).bind( 'hashchange', function(e) {
			  
                                  processStateChange( $.bbq.getState());
			   
                    });*/

                    jQuery.i18n.properties({
                              name:'messages', 
                              path:context + '/js/bundles/', 
                              mode:'both'
                          });
		  
		
          });
	
  </script>
</head>
<body style="height:100%;padding-top: 60px;">
 <div id="wrap">
   <g:render template="/common/topbar"/>
   <div id="defaultDialogDiv"><!-- default for general jquery dialogs --></div>
   <div class="container-fluid">
		<g:layoutBody />
	
	</div>
	<div id="push"><!-- for sticky footers --></div> 
  </div>
  <footer id="footer"><g:render template="/common/footer" /></footer>    
	  
</body>
</html>