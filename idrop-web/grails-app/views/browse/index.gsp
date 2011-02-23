<head>
<g:javascript library="jquery.jstree.min" />
</head>
<script>
$(function() {
	retrieveBrowserFirstView();
});
</script>

<div id="browser" class="wrapper">
<div id="dataTreeView"
	style="float: left; position: relative; width: auto; display:inline-block; overflow:auto;"><!--  no empty divs -->
<div id="dataTreeDiv" class="colLeft"><!--  no empty divs --></div>
<div id="infoDiv" class="colRight roundedContainer"><h2>Select a directory or file to see info and tags</h2></div>

</div>
</div>