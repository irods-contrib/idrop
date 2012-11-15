<h4><g:message code="text.metadata" /></h4>

<g:hiddenField name='metadataDetailsAbsPath' id='metadataDetailsAbsPath' value='${absPath}'/>

	<!-- div for metadata table -->
</div>


<div id="metadataMessageArea">
	<!--  -->
</div>

<div id="metadataDialogArea">
	<!--  area for generating dialogs -->
</div>

<div id="metadataDetailsArea">

	<div class="alert alert-info">
			<g:message code="heading.metadata" />
	</div>

 
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
	<div id="metadataTableDiv"><!--  table goes here --></div>
</div>

<script type="text/javascript">

	var origData = "";

	var path = $("#metadataDetailsAbsPath").val();
	if (path == null) {
		path = baseAbsPath;
	}
	
	
	$(function() {
		
		reloadMetadataDetailsTable(path);
	});

	
	</script>