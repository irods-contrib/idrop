<div id="bannercontainer">
	<!--  image banner -->
</div>
<g:ifAuthenticated>
	<div id="headerSearchBox" class="ui-widget-header fg-toolbar">

		<div id="searchMenu" class="fg-buttonset fg-buttonset-multi" style="float:left">
			<input id="searchTerm" type="text"
				name="searchTerm" />

			<button type="button" id="search"
				class="ui-state-default ui-corner-all" value="search"
				onclick="search()")><g:message code="text.search" /></button><span>as a<g:select name="searchType" id="searchType" from="${['file', 'tag']} " /></span>
		
		<span> or </span><button type="button" id="selectTreePath"
				class="ui-state-default ui-corner-all" value="search"
				onclick="selectTreePath()")><g:message code="text.find.path.in.tree" /></button>
		</div>

		<div id="toggleMenu" class="fg-buttonset fg-buttonset-single" style="float:right">

			<button type="button" id="toggleMenuButton"
				class="ui-state-default ui-corner-all" value="showMenu"
				onclick="showMenu()")><g:message code="text.show.menu" /></button>
				
	
			<button type="button" id="logoutButton"
				class="ui-state-default ui-corner-all" value="logout"
				onclick="logout()")><g:message code="text.logout" /></button>

		</div>

	</div>
</g:ifAuthenticated>

<script type="text/javascript">
var menuShown = true;
function showMenu() {
	
	if (menuShown) {
		$("#secondaryDiv").hide('slow');
		$("#secondaryDiv").width="0%";
		menuShown = false;
		$("#mainDiv").width="100%";
		$("#mainDivCol1").width="100%";
		$("#mainDivCol1").removeClass();
		dataLayout.resizeAll();
	} else {
		$("#secondaryDiv").show('slow');
		$("#mainDiv").width="80%";
		$("#mainDivCol1").width="80%";
		$("#secondaryDiv").width="20%";
		$("#mainDivCol1").addClass("yui-u first");
		refreshTagCloud();
		dataLayout.resizeAll();
		menuShown = true;
	}
}

</script>



