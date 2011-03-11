<div id="bannercontainer"><!--  image banner --></div>
<div id="headerSearchBox">
<g:ifAuthenticated>
<fieldset class=""><label for="">Search Term:</label> <input
	id="searchTerm" type="text" name="searchTerm" />

<button type="button" id="search" value="search" onclick="search()")>Search</button> as a 

<g:select name="searchType" id="searchType" from="${['file', 'tag']}" noSelection="['file':'']" />

</fieldset>

<div id="toggleMenu">

<button type="button" id="toggleMenuButton" value="showMenu" onclick="showMenu()")>Show/Hide Menu</button>
</div>


</g:ifAuthenticated>

</div>

<script type="text/javascript">
<!--
var menuShown = true;
function showMenu() {
	
	if (menuShown) {
		$("#secondaryDiv").hide('slow');
		$("#secondaryDiv").width="0%";
		menuShown = false;
		$("#mainDiv").width="100%";
		$("#mainDivCol1").width="100%";
	} else {
		$("#secondaryDiv").show('slow');
		$("#mainDiv").width="80%";
		$("#mainDivCol1").width="80%";
		$("#secondaryDiv").width="20%";
		menuShown = true;
	}
}
//-->
</script>



