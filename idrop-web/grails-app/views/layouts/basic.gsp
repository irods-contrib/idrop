<!DOCTYPE html>
<html lang="en">
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title><g:layoutTitle default="iDrop-web - iRODS Cloud Browser" /></title>
 <link rel="stylesheet" href="${resource(dir:'css',file:'bootstrap.css')}" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'jqcloud.css')}" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'layout-default-latest.css')}" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'jquery.fileupload-ui.css')}" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'superfish.css')}" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'jquery.gritter.css')}" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'overcast/jquery-ui-1.9.0.custom.css')}" />

    <link rel="shortcut icon"
          href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
  <g:layoutHead />
  <g:javascript library="jquery-1.7.2.min" />
 <g:javascript library="jquery-ui-1.8.7.custom.min" />
  <g:javascript library="bootstrap.min" />
  
  <g:javascript library="jquery.i18n.properties-min-1.0.9" />
  <g:javascript library="mydrop/lingo_common" />
  <g:javascript library="mydrop/main" />
  <g:javascript library="jquery-ui-13" />
  <g:javascript library="jquery.blockUI" />
  <g:javascript library="jquery.ba-bbq.min" />
  <g:javascript library="jquery-ui-13" />
  <g:javascript library="jquery.tools.min" />
  <g:javascript library="jquery.gritter.min" />
  <g:javascript library="jquery.opacityrollover" />
<!--  preserve the application context as a js variable for use in AJAX callbacks -->
<script type="text/javascript">
	context = "${request.contextPath}";

	// hack for now
	
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
		  
		  // Since the event is only triggered when the hash changes, we need to trigger
		  // the event now, to handle the hash the page may have loaded with.
		 // $(window).trigger( 'hashchange' );
		
	});
	
</script>
</head>
<body>
<div id="bannercontainer">
	<!--  image banner -->
</div>
<div id="bd" style="height:100%;">
<div id="defaultDialogDiv"><!-- default for general jquery dialogs --></div>
<div id="yui-main" style="height:100%;">
<div class="yui-b" style="height:100%;">
<div id="mainDiv" class="yui-ge" style="height:100%;">
<div id="mainDivCol1" class="yui-u first" style="height:100%;"><!-- PUT MAIN COLUMN 1 CODE HERE -->
 <g:layoutBody />
</div>

</div>
</div>
</div>
</div>

</div>
<div id="ft"><!-- PUT FOOTER CODE HERE -->
<g:render template="/common/footer" />
</div>
</body>
</html>