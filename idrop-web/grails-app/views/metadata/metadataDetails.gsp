
<g:hiddenField name='metadataDetailsAbsPath' id='metadataDetailsAbsPath'
	value='${absPath}' />

<!-- div for metadata table -->
<div></div>


<div id="metadataMessageArea">
	<!--  -->
</div>

<div id="metadataDialogArea">
	<!--  area for generating dialogs -->
</div>

<div id="metadataDetailsArea" class="container-fluid">

	<div class="row">
		<div class="span12">
			<h4>
				<g:message code="text.metadata" />
			</h4>
		</div>
	</div>
	<div class="row alert alert-info">
		<div class="span10">
			<g:message code="heading.metadata" />
		</div>
	</div>

	<div id="detailsToolbar" class="row well">
		<div id="detailsMenu" class="span12 btn-group">
			<button type="button" id="addMetadataButton" value="addMetadata"
				onclick="prepareMetadataDialog()")>
				<g:message code="default.button.create.label" />
			</button>
			<button type="button" id="deleteMetadataButton"
				value="deleteMetadata" onclick="deleteMetadata()")>
				<g:message code="default.button.delete.label" />
			</button>
			<button type="button" id="reloadAclButton" value="reloadMetadata"
				onclick="reloadMetadataDetailsTable()")>
				<g:message code="default.button.reload.label" />
			</button>
		</div>
	</div>
	<div class="row">
		<div id="metadataTableDiv" class="span12">
			<!--  table goes here -->
		</div>
	</div>
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