<head>
	<meta name="layout" content="mainNoSidebar" />
	<g:javascript library="mydrop/home" />
	<g:javascript library="mydrop/metadata" />
</head>
<div class="wrapper clearfix"
	style="height: 820px; overflow:hidden;">
	<g:hiddenField name="mode" id="mode" value="${mode}"/>
	<g:hiddenField name="presetPath" id="presetPath" value="${path}"/>
	<g:render template="/browse/browseTabContent" />
	
</div> 
<script type="text/javascript">
	var dataLayout;
	var tabs;
	$(document).ready(function() {

		$.ajaxSetup({
			cache : false
		});

		$("#infoDiv").resize();

		dataLayout = $("#dataTreeView").layout({
			applyDefaultStyles : false,
			size : "auto",
			west__minSize : 150,
			west__resizable : true
		});

		var mode = $("#mode").val();
		var startPath = $("#presetPath").val();
		
		if (mode == null || mode=="") {
			retrieveBrowserFirstView("detect","");
		} else {
			retrieveBrowserFirstView(mode, startPath);
		}

	});
</script>