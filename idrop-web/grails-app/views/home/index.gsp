<head>
<meta name="layout" content="main" />
<g:javascript library="mydrop/home" />
<g:javascript library="mydrop/search" />

</head>

<div id="tabs">
	<ul>
		<li><a href="#quickView">Quick View</a>
		</li>
		<li><a href="#search">Search</a>
		</li>
		<li><a href="#browse">Browse</a>
		</li>
	</ul>
	<div id="quickView">
		<div class="objectContainer">
			<div class="objectContainerGrouping"></div>
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
					class="objectAction">EDIT</a> - <a href="viewInTree"
					class="objectAction">VIEW</a> - <a href="share"
					class="objectAction">SHARE</a>
				</span> <span class="objectContainerTags">tag1 tag2:detail tag3
					hello</span>
			</div>

		</div>


		<div class="objectContainer">
			<div class="objectContainerGrouping"></div>
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
					class="objectAction">EDIT</a> - <a href="viewInTree"
					class="objectAction">VIEW</a> - <a href="share"
					class="objectAction">SHARE</a>
				</span> <span class="objectContainerTags">tag1 tag2:detail tag3
					hello</span>
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
			<div id="dataTreeView" class="">
				<!--  no empty divs -->
				<div id="dataTreeDiv" class="ui-layout-west">
					<!--  no empty divs -->
				</div>
				
				<div id="infoDiv" class="ui-layout-center">
					<h2>Select a directory or file to see info and tags</h2>
				</div>
			</div>

		</div>

	</div>

</div>

<script type="text/javascript">
var dataLayout;
$(document).ready(function() {
	dataLayout = $("#dataTreeView").layout({ 
		applyDefaultStyles: true,
		west__minSize: 100,
		west__resizable: true,
		
		});
	$( "#tabs" ).tabs();
	retrieveBrowserFirstView();
});

</script>