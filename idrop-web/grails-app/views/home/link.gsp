<head>
<meta name="layout" content="basic" />
</head>
<g:hiddenField name="absPath" id="absPath" value="${absPath}"/>
<div style="height:100px;margin-left:auto; margin-right:auto;">
<h1 style="text-align:center;"><g:message code="heading.loading.for.link" /></h1>
</div>
<div style="clear:both;height:100px;">
<center><img  src="${createLinkTo(dir: 'images', file: 'ajax-loader-bar.gif')}" alt="Loading..."/></center>
</div>
</div>
<script type="text/javascript">
	
	$(document).ready(function() {
		baseAbsPath = $("#absPath").val();
		if (baseAbsPath == null) {
			return false;
		}
		baseAbsPath = escape(baseAbsPath);
		//alert(baseAbsPath);
		window.location.href = context + "/browse/index?mode=path&absPath=" + baseAbsPath;
	});
</script>