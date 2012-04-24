<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 //EN">
<html>
<head>
<title><g:layoutTitle default="iDrop-web - iRODS Cloud Browser" /></title>
<link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" />
<link rel="stylesheet" href="${resource(dir:'css',file:'base.css')}" />
<link rel="stylesheet" href="${resource(dir:'css',file:'style.css')}" />
<link rel="stylesheet" href="${resource(dir:'css',file:'jqcloud.css')}" />
<link rel="stylesheet" href="${resource(dir:'css',file:'layout-default-latest.css')}" />
<link rel="stylesheet" href="${resource(dir:'css',file:'jquery.fileupload-ui.css')}" />
<link rel="stylesheet" href="${resource(dir:'css',file:'superfish.css')}" />
<link rel="stylesheet" href="${resource(dir:'css',file:'jquery.gritter.css')}" />
<link rel="stylesheet"
	href="${resource(dir:'css',file:'reset-fonts-grids.css')}" />
<link rel="stylesheet" href="${resource(dir:'css',file:'start/jquery-ui-1.8.18.custom.css')}" />

<link rel="shortcut icon"
	href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
<g:layoutHead />
<g:javascript library="jquery-1.7.1.min" />
<g:javascript library="jquery-ui-1.8.7.custom.min" />
 <g:javascript library="jquery.hotkeys" />
 <g:javascript library="jquery.jstree" />
<g:javascript library="jquery.jeditable.mini" />
<g:javascript library="jquery.dataTables.min" />
<g:javascript library="mydrop/ticket" />
<g:javascript library="jquery.i18n.properties-min-1.0.9" />
<g:javascript library="jqcloud-0.1.6" />
<g:javascript library="jquery.fileupload-ui" />
<g:javascript library="jquery.fileupload" />
<g:javascript library="jquery.media" />
<g:javascript library="mydrop/lingo_common" />
<g:javascript library="mydrop/main" />
<g:javascript library="jquery-ui-13" />
<g:javascript library="jquery.blockUI" />
<g:javascript library="jquery.ba-bbq.min" />
<g:javascript library="jquery.layout.min-1.3.0" />
<g:javascript library="jquery-ui-13" />
<g:javascript library="jquery.tools.min" />
<g:javascript library="mydrop/shopping_cart" />
<g:javascript library="mydrop/user" />
<g:javascript library="galleria-1.2.6" />
<g:javascript library="jquery.gritter.min" />
<g:javascript library="jquery.opacityrollover" />
<g:javascript library="superfish" />
<!--  preserve the application context as a js variable for use in AJAX callbacks -->
<script type="text/javascript">
	context = "${request.contextPath}";
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
<body style="height:100%;">
<div id="hd"><!-- PUT MASTHEAD CODE HERE -->
<g:render template="/common/topbar"/>
<g:render template="/common/messages"/>
</div>
<div id="bd" style="height:100%;">
<div id="defaultDialogDiv"><!-- default for general jquery dialogs --></div>
<div id="yui-main" style="height:100%;">
<div class="yui-b" style="height:100%;">
<div id="mainDiv" class="yui-ge" style="height:100%;">
<div id="mainDivCol1" class="yui-u first" style="height:100%;"><!-- PUT MAIN COLUMN 1 CODE HERE -->
 <g:layoutBody />
</div>
<div id="secondaryDiv" class="yui-u" style="height:100%;"><!-- PUT MAIN COLUMN 2 CODE HERE -->
<g:ifAuthenticated>
<g:render template="/common/secondarymain"/>
</g:ifAuthenticated>

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