<head>
	<meta name="layout" content="mainNoSidebar" />
	<g:javascript library="mydrop/home" />
	<g:javascript library="mydrop/metadata" />
</head>
<div class="wrapper clearfix"
	style="height: 820px; overflow:hidden;">
	<g:hiddenField name="mode" id="mode" value="${mode}"/>
	<g:hiddenField name="viewStateBrowseOptionVal" id="viewStateBrowseOptionVal" value="${viewState.browseView}"/>
	<g:hiddenField name="presetPath" id="presetPath" value="${viewState.rootPath}"/>
	<g:hiddenField id="viewStateSelectedPath" name="viewStateSelectedPath" value="${viewState.selectedPath}"/>
	<g:render template="/browse/browseTabContent" />
	
</div> 
<script type="text/javascript">
	var dataLayout;
	var tabs;
	$(document).ready(function() {

		$("#topbarBrowser").addClass("active");

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
		browseOptionVal = $("#viewStateBrowseOptionVal").val();
		dataTreePath = $("#presetPath").val();
		var thisSelectedPath = $("#viewStateSelectedPath").val();

		
		if (mode == null || mode=="") {
			retrieveBrowserFirstView("detect","", thisSelectedPath);
		} else {
			retrieveBrowserFirstView(mode, dataTreePath, thisSelectedPath);
		}

		 $(window).bind( 'hashchange', function(e) {
			  
             processStateChange( $.bbq.getState());

		});

	});
</script>