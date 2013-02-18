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
  
	 <div class="container-fluid">
		<g:layoutBody />
	</div>
	<div id="push"><!-- for sticky footers --></div> 

</div>

<g:render template="/common/footer" />
</div>
</body>
</html>