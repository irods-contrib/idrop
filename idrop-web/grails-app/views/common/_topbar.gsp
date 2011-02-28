<div id="bannercontainer"><!--  image banner --></div>
<div id="headerSearchBox">

<g:ifAuthenticated>
<fieldset class=""><label for="">Search Term:</label> <input
	id="searchTerm" type="text" name="searchTerm" />

<button type="button" id="search" value="search" onclick="search()")>Search</button> as a 

<g:select name="searchType" id="searchType" from="${['file', 'tag']}" noSelection="['file':'']" />

</fieldset>
</g:ifAuthenticated>

</div>



