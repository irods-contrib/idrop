<h2>
	<g:message code="heading.metadata" />
</h2>
<div id="detailsTopSection" class="box">

	<div id="detailsToolbar" class="fg-toolbar ui-widget-header">
		<div id="detailsMenu" class="fg-buttonset fg-buttonset-multi"
			style="float: left, clear :   both;">
			<button type="button" id="addMetadataButton"
				class="ui-state-default ui-corner-all" value="addMetadata"
				onclick="prepareMetadataDialog()")>
				<g:message code="default.button.create.label" />
			</button>
			<button type="button" id="deleteMetadataButton"
				class="ui-state-default ui-corner-all" value="deleteMetadata"
				onclick="deleteMetadata()")>
				<g:message code="default.button.delete.label" />
			</button>
			<button type="button" id="reloadAclButton"
				class="ui-state-default ui-corner-all" value="reloadMetadata"
				onclick="reloadMetadataDetailsTable()")>
				<g:message code="default.button.reload.label" />
			</button>
		</div>
	</div>
	<g:render template="/common/panelmessages" />

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
	
	$(function() {
		hideAllToolbars();
		reloadMetadataDetailsTable();
	});

	
	</script>