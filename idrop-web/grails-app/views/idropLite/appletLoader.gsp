<div>
<div id="appletMenu" class="fg-buttonset fg-buttonset-single" style="float:right">
			<button type="button" id="toggleMenuButton"
				class="ui-state-default ui-corner-all" value="closeIdropApplet"
				onclick="closeApplet()")>Close iDrop Lite</button>
</div>
<applet archive="http://localhost:8080/idrop-web/idrop-lite-1.0-SNAPSHOT-jar-with-dependencies.jar" code="org.irods.jargon.idrop.lite.iDropLiteApplet" width="100%" height="100%">
<param name="mode" value="${mode}" />
<param name="host" value="${account.host}" />
<param name="port" value="${account.port}" />
<param name="zone" value="${account.zone}" />
<param name="user" value="${account.userName}" />
<param name="defaultStorageResource" value="${account.defaultStorageResource}" />
<param name="password" value="${password}" />
<param name="absPath" value="${absPath}" />
</applet>
</div>

<script type="text/javascript">

function closeApplet() {
	//$("#idropLiteArea").hide('slow');
	$("#idropLiteArea").animate({ height: 'hide', opacity: 'hide' }, 'slow');
	//$("#idropLiteArea").width="0%";
	//$("#idropLiteArea").height="0%";
	//$("#idropLiteArea").removeClass();
	$("#toggleBrowseDataDetailsTable").show('slow');
	$("#toggleBrowseDataDetailsTable").height="100%";
	$("#toggleBrowseDataDetailsTable").width="100%";
	dataLayout.resizeAll();
}

</script>