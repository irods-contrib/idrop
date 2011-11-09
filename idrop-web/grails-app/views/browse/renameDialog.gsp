<div id="renameDialog" class="roundedContainer">
	
	<div style="clear: both;">
	
	<div id="renameDialogMessageArea" style="width:90%;">
		<!--  no empty divs -->
	</div>
	
			<fieldset id="verticalForm">
			
				<label for="fileName"><g:message code="text.name" />:</label>
				<g:textArea name="fileName" id="fileName" value="${fileName}"  />
				<g:hiddenField name="absPath" id="renameDialogAbsPath" value = "${absPath }" />
				<br />
				<div id="renameDialogToolbar" class="fg-toolbar ui-widget-header">
					<div id="renameDialogMenu" class="fg-buttonset fg-buttonset-multi"
						style="float: left, clear :   both; width: 90%;">
						<button type="button" id="updateRenameButton"
							class="ui-state-default ui-corner-all" value="update"
							onclick="submitRenameDialog()")><g:message code="default.button.update.label" /></button>
						<button type="button" id="cancelRenameButton"
							class="ui-state-default ui-corner-all" value="cancelAdd"
							onclick="closeRenameDialog()")><g:message code="text.cancel" /></button>
					</div>
				</div>
			</fieldset>
	</div>
</div>
<script>

	$(function() {
		$("#renameDialog").dialog({width:500});
	});

	

</script>
