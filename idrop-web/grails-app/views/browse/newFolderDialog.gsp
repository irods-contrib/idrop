<div id="newFolderDialog" class="roundedContainer">
	<h2><g:message code="heading.new.folder.dialog"/></h2>
	<div id="newFolderDialogMessageArea" style="width:90%;">
		<!--  no empty divs -->
	</div>
	
			<fieldset id="verticalForm">
			
				<label for="fileName"><g:message code="text.new.folder" />:</label>
				<g:textArea name="fileName" id="fileName" value="${fileName}"  />
				<g:hiddenField name="absPath" id="newFolderDialogAbsPath" value = "${absPath }" />
				<br />
				<div id="newFolderDialogToolbar" class="fg-toolbar ui-widget-header">
					<div id="newFolderDialogMenu" class="fg-buttonset fg-buttonset-multi"
						style="float: left, clear :   both; width: 90%;">
						<button type="button" id="updateNewFolderButton"
							class="ui-state-default ui-corner-all" value="update"
							onclick="submitNewFolderDialog()")><g:message code="default.button.update.label" /></button>
						<button type="button" id="cancelRenameButton"
							class="ui-state-default ui-corner-all" value="cancelAdd"
							onclick="closeNewFolderDialog()")><g:message code="text.cancel" /></button>
					</div>
				</div>
			</fieldset>
	</div>

<script>

	$(function() {
		$("#newFolderDialog").dialog({width:500, modal:true});
	});

	

</script>
