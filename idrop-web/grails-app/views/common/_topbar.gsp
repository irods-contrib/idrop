<div id="bannercontainer">
	<!--  image banner -->
</div>
<g:ifAuthenticated>
	<div id="headerSearchBox" class="ui-widget-header fg-toolbar">

		<div id="searchMenu" class="fg-buttonset fg-buttonset-multi" style="float:left">
			<label for="">Search Term:</label> <input id="searchTerm" type="text"
				name="searchTerm" />

			<button type="button" id="search"
				class="ui-state-default ui-corner-all" value="search"
				onclick="search()")>Search</button><span>as a<g:select name="searchType" id="searchType" from="${['file', 'tag']}" /></span>
		</div>

		<div id="toggleMenu" class="fg-buttonset fg-buttonset-single" style="float:right">

			<button type="button" id="toggleMenuButton"
				class="ui-state-default ui-corner-all" value="showMenu"
				onclick="showMenu()")>Show/Hide Menu</button>

			<button type="button" id="logoutButton"
				class="ui-state-default ui-corner-all" value="logout"
				onclick="logout()")>Logout</button>

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
		//$("#dataTreeView").layout.resizeContent("center");
		//$("#infoDiv").width="100%";
	} else {
		$("#secondaryDiv").show('slow');
		$("#mainDiv").width="80%";
		$("#mainDivCol1").width="80%";
		$("#secondaryDiv").width="20%";
		$("#mainDivCol1").addClass("yui-u first");
		dataLayout.resizeAll();
		
		//$("#dataTreeView").layout.resizeContent("center");
		
		//$("#infoDiv").width="100%";
		menuShown = true;
	}
}

</script>



