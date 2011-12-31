<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title><g:layoutTitle default="myDrop - iRODS Personal Cloud" /></title>
<link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" />
<link rel="stylesheet" href="${resource(dir:'css',file:'base.css')}" />
<link rel="stylesheet" href="${resource(dir:'css',file:'style.css')}" />
<link rel="stylesheet" href="${resource(dir:'css',file:'jqcloud.css')}" />
<link rel="stylesheet" href="${resource(dir:'css',file:'layout-default-latest.css')}" />
<link rel="stylesheet" href="${resource(dir:'css',file:'jquery.fileupload-ui.css')}" />
<link rel="stylesheet" href="${resource(dir:'css',file:'galleriffic-2.css')}" />
<link rel="stylesheet" href="${resource(dir:'css',file:'superfish.css')}" />
<link rel="stylesheet" href="${resource(dir:'css',file:'jquery.gritter.css')}" />
<link rel="stylesheet"
	href="${resource(dir:'css',file:'reset-fonts-grids.css')}" />
<link rel="stylesheet" href="${resource(dir:'css',file:'overcast/jquery-ui-1.8.7.custom.css')}" />

<link rel="shortcut icon"
	href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
<g:layoutHead />
<g:javascript library="jquery-1.6.1" />
<g:javascript library="jquery-ui-1.8.7.custom.min" />
 <g:javascript library="jquery.hotkeys" />
 <g:javascript library="jquery.jstree" />
<g:javascript library="jquery.jeditable.mini" />
<g:javascript library="jquery.dataTables.min" />
<g:javascript library="jqcloud-0.1.6" />
<g:javascript library="jquery.fileupload-ui" />
<g:javascript library="jquery.fileupload" />
<g:javascript library="jquery.media" />
<g:javascript library="mydrop/lingo_common" />
<g:javascript library="mydrop/main" />
<g:javascript library="jquery-ui-13" />
<g:javascript library="jquery.layout.min-1.3.0" />
<g:javascript library="jquery-ui-13" />
<g:javascript library="jquery.tools.min" />
<g:javascript library="mydrop/shopping_cart" />
<g:javascript library="mydrop/user" />
<g:javascript library="jquery.galleriffic" />
<g:javascript library="jquery.gritter.min" />
<g:javascript library="jquery.opacityrollover" />
<g:javascript library="superfish" />
<!--  preserve the application context as a js variable for use in AJAX callbacks -->
<script type="text/javascript">
	context = "${request.contextPath}";
	scheme = "${request.scheme}";
	host = "${request.localName}";
	port = "${request.localPort}";
</script>

 
</head>
<body>
<div id="hd"><!-- PUT MASTHEAD CODE HERE -->
<g:render template="/common/topbar"/>
<g:render template="/common/messages"/>
</div>
<div id="bd" style="height:100%;">
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