<html>
<head>
<title><g:layoutTitle default="myDrop - iRODS Personal Cloud" /></title>
<link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" />
<link rel="stylesheet" href="${resource(dir:'css',file:'base.css')}" />
<link rel="stylesheet" href="${resource(dir:'css',file:'style.css')}" />
<link rel="stylesheet" href="${resource(dir:'css',file:'jqcloud.css')}" />
<link rel="stylesheet"
	href="${resource(dir:'css',file:'reset-fonts-grids.css')}" />
<link rel="stylesheet" href="${resource(dir:'css',file:'overcast/jquery-ui-1.8.7.custom.css')}" />

<link rel="shortcut icon"
	href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
<g:layoutHead />
<g:javascript library="jquery-1.4.4.min" />
<g:javascript library="jquery-ui-1.8.7.custom.min" />
<g:javascript library="jquery.jstree.min" />
<g:javascript library="jquery.dataTables.min" />
<g:javascript library="jqcloud-0.1.5.min" />
<g:javascript library="mydrop/lingo_common" />
<g:javascript library="mydrop/main" />
<g:javascript library="jquery-ui-13" />


<!--  preserve the application context as a js variable for use in AJAX callbacks -->
<script type="text/javascript">
	context = "${request.contextPath}";
</script>
</head>
<body>
<div id="hd"><!-- PUT MASTHEAD CODE HERE -->
<g:render template="/common/topbar"/>
<g:render template="/common/messages"/>
</div>
<div id="bd">
<div id="yui-main">
<div class="yui-b">
<div id="mainDiv" class="yui-ge">
<div id="mainDivCol1" class="yui-u first"><!-- PUT MAIN COLUMN 1 CODE HERE -->
<div id="spinner" class="spinner" style="display: none;"><img
	src="${resource(dir:'images',file:'spinner.gif')}"
	alt="${message(code:'spinner.alt',default:'Loading...')}" />
</div>
 <g:layoutBody />
</div>
<div id="secondaryDiv" class="yui-u"><!-- PUT MAIN COLUMN 2 CODE HERE -->
<g:ifAuthenticated>
<g:render template="/common/secondarymain"/>
</g:ifAuthenticated>

</div>
</div>
</div>
</div>
<div class="yui-b"><!-- PUT SECONDARY COLUMN CODE HERE --></div>
</div>
<div id="ft"><!-- PUT FOOTER CODE HERE -->
<g:render template="/common/footer" />
</div>
</body>
</html>