<script>
	$(function() {
		$("#secondaryTabs").tabs();
		lcSendValueAndCallbackHtmlAfterErrorCheck("/tags/tagCloud", "#tagCloudDiv",
				"#tagCloudDiv", null);
	});
</script>
<div id="secondaryTabs">
<ul>
	<li><a href="#tabs-1">Tags</a></li>
	<li><a href="#tabs-2">People</a></li>
	
</ul>
<div id="tabs-1">
<div id="tagCloudDiv" class="scroll">

<!--  tag cloud div is ajax loaded -->

</div>
</div>
<div id="tabs-2">
<ul>
	<li>Mike Conway</li>
	<li>Terrell Russell</li>
	<li>Art Vandalay</li>
</ul>
</div>