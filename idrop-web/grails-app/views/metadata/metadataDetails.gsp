 <h3><a ><g:message code="text.metadata" /></a></h3>
<div id="detailsTopSection">

	<div id="detailsToolbar" class="well">
		<div id="detailsMenu" 
			style="float: left, clear :   both;">
			<button type="button" id="addMetadataButton"
				value="addMetadata"
				onclick="prepareMetadataDialog()")>
				<g:message code="default.button.create.label" />
			</button>
			<button type="button" id="deleteMetadataButton"
				value="deleteMetadata"
				onclick="deleteMetadata()")>
				<g:message code="default.button.delete.label" />
			</button>
			<button type="button" id="reloadAclButton"
				 value="reloadMetadata"
				onclick="reloadMetadataDetailsTable()")>
				<g:message code="default.button.reload.label" />
			</button>
		</div>
	</div>
	<g:hiddenField name='metadataDetailsAbsPath' id='metadataDetailsAbsPath' value='${absPath}'/>

	<div id="metadataMessageArea">
		<!--  -->
	</div>

	<div id="metadataDialogArea">
		<!--  area for generating dialogs -->
	</div>

	<div id="metadataTableDiv">
		<!-- div for metadata table -->
	</div>
</div>

<script type="text/javascript">

	var origData = "";

	var path = $("#metadataDetailsAbsPath").val();
	if (path == null) {
		path = baseAbsPath;
	}
	reloadAclTable(path);
	
	$(function() {
		
		reloadMetadataDetailsTable(path);
	});

	
	</script>