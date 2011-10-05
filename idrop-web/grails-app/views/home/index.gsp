<head>
<meta name="layout" content="main" />
<g:javascript library="mydrop/home" />
<g:javascript library="mydrop/search" />
<g:javascript library="mydrop/metadata" />

</head>

<div id="tabs">
	<ul>
		<li><a href="#quickView"><g:message code="text.home" /></a></li>
		<li><a href="#search"><g:message code="text.search" /></a></li>
		<li><a href="#browse"><g:message code="text.browse" /></a></li>
	</ul>
	<div id="quickView">
		<div class="objectContainer">

			<div class="objectContainerDetails">
				<span class="objectHeader">A data object</span>
				<div class="objectDescription">This is a really nice data
					object with a good deal of bytes dedicated to showing many
					interesting things. For one thing, it's blue, and there are lots of
					fiddley bits that you can inspect that can really help understand
					all about various things. There is also enough text fill many lines
					with all sorts of appealing extra information</div>
			</div>
			<div class="objectContainerTools">
				<span class="objectContainerActions"><a href="edit"
					class="objectAction"><g:message code="text.edit" /></a> - <a href="viewInTree"
					class="objectAction"><g:message code="text.view" /></a> - <a href="share"
					class="objectAction"><g:message code="text.share" /></a> </span> <span class="objectContainerTags">tag1
					tag2:detail tag3 hello</span>
			</div>

		</div>


		<div class="objectContainer">

			<div class="objectContainerDetails">
				<span class="objectHeader">A data object</span>
				<div class="objectDescription">This is a really nice data
					object with a good deal of bytes dedicated to showing many
					interesting things. For one thing, it's blue, and there are lots of
					fiddley bits that you can inspect that can really help understand
					all about various things. There is also enough text fill many lines
					with all sorts of appealing extra information</div>
			</div>
			<div class="objectContainerTools">
				<span class="objectContainerActions"><a href="edit"
						class="objectAction"><g:message code="text.edit" /></a> - <a href="viewInTree"
					class="objectAction"><g:message code="text.view" /></a> - <a href="share"
					class="objectAction"><g:message code="text.share" /></a> </span> <span class="objectContainerTags">tag1
					tag2:detail tag3 hello</span>
			</div>

		</div>


	</div>
	<div id="search">
		<div class="wrapper">

			<div id="searchView">
				<!--  this will be filled in with the search results table -->
				<div id="searchTableDiv">
					<!--  search table display di -->
				</div>
			</div>
		</div>
	</div>

	<div id="browse">
		<div id="browser" class="wrapper" style="width: 100%">
			<div id="browseToolbar" class=""
				style="height: 30px; position: relative; display: block; width: auto;">
				<div id="browseToolbarSubBox" class="ui-widget-header fg-toolbar">

					<div id="browseMenu" class="fg-buttonset fg-buttonset-multi"
						style="float: left">
						<g:message code="text.display.option" />:
						<g:select name="browseDisplayOption" id="browseDisplayOption"
							from="${['info', 'sharing', 'metadata']}"
							noSelection="${['details':'details']}" onChange="setBrowseMode()" />
					</div>

					
				</div>


			</div>
			<g:render template="/common/panelmessages" />

			<div id="dataTreeView" style="width:100%; height:100%">

				<!--  no empty divs -->
				<div id="dataTreeDiv" class="ui-layout-west" style="width:25%%;height:100%;overflow:auto;">
					<!--  no empty divs -->
				</div>

				<div id="infoDivOuter" style="display:block;width:75%;height:100%; position:relative; overflow:hidden;" class="ui-layout-center">
					<div id="infoDiv">
					<h2><g:message code="browse.page.prompt" /></h2>
				</div>
				</div>
			</div>

		</div>

	</div>

</div>

<script type="text/javascript">
var dataLayout;
var globalMessageArea = "#javascript_message_area";
$(document).ready(function() {

/*
	dataLayout = $("#dataTreeView").layout({ 
		applyDefaultStyles: true,
		size: "auto",
		west__minSize: 100,
		west__resizable: true		
		});
		
	*/
	retrieveBrowserFirstView();

	tabs = $( "#tabs" ).tabs({
			
	} );

});

</script>