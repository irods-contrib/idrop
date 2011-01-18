<head>
<meta name="layout" content="main" /> 
<g:javascript library="mydrop/home" />
</head>
<script>
	$(function() {
		$( "#tabs" ).tabs();
	});
	</script>
<div id="tabs">
	<ul>
		<li><a href="#quickView">Quick View</a></li>
		<li><a href="#search">Search</a></li> 
		<li><g:link controller='browse' action='index'>Browse</g:link></li>
	</ul>
	<div id="quickView">
	<div class="objectContainer">
	<div class="objectContainerGrouping"></div>
	<div class="objectContainerDetails">
	<span class="objectHeader">A data object</span>
	<div class="objectDescription">This is a really nice data object with a good deal of bytes dedicated to showing many interesting things. For one thing, it's blue, and 
	there are lots of fiddley bits that you can inspect that can really help understand all about various things.   There is also enough text
	fill many lines with all sorts of appealing extra information</div>
	</div>
	<div class="objectContainerTools">
		<span class="objectContainerActions"><a href="edit" class="objectAction">EDIT</a> - <a href="viewInTree" class="objectAction">VIEW</a> - <a href="share" class="objectAction">SHARE</a></span>
		<span class="objectContainerTags">tag1 tag2:detail tag3 hello</span>
		
	</div>
	
	</div>
	
	
	<div class="objectContainer">
	<div class="objectContainerGrouping"></div>
	<div class="objectContainerDetails">
	<span class="objectHeader">A data object</span>
	<div class="objectDescription">This is a really nice data object with a good deal of bytes dedicated to showing many interesting things. For one thing, it's blue, and 
	there are lots of fiddley bits that you can inspect that can really help understand all about various things.   There is also enough text
	fill many lines with all sorts of appealing extra information</div>
	</div>
	<div class="objectContainerTools">
		<span class="objectContainerActions"><a href="edit" class="objectAction">EDIT</a> - <a href="viewInTree" class="objectAction">VIEW</a> - <a href="share" class="objectAction">SHARE</a></span>
		<span class="objectContainerTags">tag1 tag2:detail tag3 hello</span>
		
	</div>
	
	</div>
	
	
	</div>
	<div id="search">
		<p>Morbi tincidunt, dui sit amet facilisis feugiat, odio metus gravida ante, ut pharetra massa metus id nunc. Duis scelerisque molestie turpis. Sed fringilla, massa eget luctus malesuada, metus eros molestie lectus, ut tempus eros massa ut dolor. Aenean aliquet fringilla sem. Suspendisse sed ligula in ligula suscipit aliquam. Praesent in eros vestibulum mi adipiscing adipiscing. Morbi facilisis. Curabitur ornare consequat nunc. Aenean vel metus. Ut posuere viverra nulla. Aliquam erat volutpat. Pellentesque convallis. Maecenas feugiat, tellus pellentesque pretium posuere, felis lorem euismod felis, eu ornare leo nisi vel felis. Mauris consectetur tortor et purus.</p>
	</div>
	
</div>

