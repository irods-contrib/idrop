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

		/**
		The view state in the session keeps the root of the tree, the mode (browse, info, etc), and a path that may be selected in the tree
		These values are passed in by the BrowseController and preservered in gsp fields to be picked up by the javascript
		*/
		
		var mode = $("#mode").val(); // mode of building tree (detect = seek the best root, path = open to the given path, root = set to the root, etc)
		browseOptionVal = $("#viewStateBrowseOptionVal").val(); // browse view, info view, gallery view, etc
		dataTreePath = $("#presetPath").val(); // root of the tree
		var thisSelectedPath = $("#viewStateSelectedPath").val(); // optional path to select
		
		
		if (mode == null || mode=="" || dataTreePath == null || dataTreePath == "") {
			retrieveBrowserFirstView("detect","", thisSelectedPath);  // figure out the best root and try to open the given path in the tree, that value 
		 // is optional
		} else {
			/*
			I have defined a mode and root path for the tree, the selected path to open is optional
			*/
			retrieveBrowserFirstView(mode, dataTreePath, thisSelectedPath);
		}

		 $(window).bind( 'hashchange', function(e) {
			  
             processStateChange( $.bbq.getState());

		});

	});
</script>