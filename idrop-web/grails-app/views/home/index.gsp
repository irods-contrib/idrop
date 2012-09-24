<head>
<meta name="layout" content="main" />
<g:javascript library="mydrop/home" />
<g:javascript library="mydrop/search" />
<g:javascript library="mydrop/metadata" />
<g:javascript library="mydrop/profile" />
</head>
<div id="tabs" class="wrapper clearfix"
	style="height: 820px; overflow:hidden;">
	
	<g:hiddenField name="mode" id="mode" value="${mode}"/>
	<g:hiddenField name="presetPath" id="presetPath" value="${path}"/>

	<ul>
		<li><a href="#browseTab"><g:message code="text.browse" /> </a></li>
		<li><a href="#searchTab"><g:message code="text.search" /> </a></li>
		<g:if test="${grailsApplication.config.idrop.config.use.userprofile==true}">
			<li><a href="#profileTab"><g:message code="text.profile" /> </a></li>
		</g:if>
	</ul>
	

	<div id="browseTab" style="height:100%;">
		<g:render template="/browse/browseTabContent" />
	</div><!--  browse tab -->
		
	<div id="searchTab">
	
		<div id="searchDivOuter"
			style="display: block; width: 95%; height: 90%; position: relative; overflow: hidden;"
			class="ui-layout-center">
			<!--  this will be filled in with the search results table -->
			<div id="searchTableDiv"
				style="width: 100%; height: 100%; overflow: auto;">
				<h2>
					<g:message code="heading.no.search.yet" />
				</h2>
			</div> <!--  searchTableDiv -->
		</div> <!--  searchDivOuter -->
	</div> <!--  search tab -->

	<g:if test="${grailsApplication.config.idrop.config.use.userprofile==true}">
		<div id="profileTab" style="height:100%;overflow:hidden;">
			<g:render template="/profile/profileTabContent" />
		</div><!--  profile tab -->
	</g:if>
		

</div> <!--  tabs -->
<script type="text/javascript">
	var dataLayout;
	var tabs;
	var globalMessageArea = "#javascript_message_area";
	$(document).ready(function() {

		$.ajaxSetup({
			cache : false
		});

		menuShown = false;
		$("#secondaryDiv").hide('slow');
		$("#secondaryDiv").width = "0%";
		menuShown = false;
		$("#mainDiv").width = "100%";
		$("#mainDivCol1").width = "100%";
		$("#mainDivCol1").removeClass();
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
		tabs = $("#tabs").tabs({});

		tabs.bind("tabsselect", function(event, ui) {

			var state = {};
			// Get the id of this tab widget.
			//alert(ui.tab.hash);
			state["#tabs"] = ui.tab.hash;
			$.bbq.pushState(state);

		});

	});
</script>