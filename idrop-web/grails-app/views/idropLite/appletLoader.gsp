<div id="appletMenu" class="pull-right">
			<button type="button" id="toggleMenuButton"
				value="closeIdropApplet"
				onclick="closeApplet()")>Close iDrop Lite</button>
</div>
<div id="appletLoadDiv">

<!--  area for idrop lite to load -->


</div>

<script type="text/javascript">

function closeApplet() {
	$("#idropLiteArea").animate({ height: 'hide', opacity: 'hide' }, 'slow');
	$("#toggleBrowseDataDetailsTable").show('slow');
	$("#toggleBrowseDataDetailsTable").height="100%";
	$("#toggleBrowseDataDetailsTable").width="100%";
	dataLayout.resizeAll();
}

</script>
