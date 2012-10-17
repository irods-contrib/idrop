<div id="renameDialog">
	
	 <div class="modal-header">
    		<h3><g:message code="heading.rename.dialog"/></h3>
  	</div>
	
	 <div class="modal-body">
	
			<fieldset id="verticalForm">
				<label for="fileName"><g:message code="text.name" />:</label>
				<g:textArea name="fileName" id="fileName" value="${fileName}"  />
				<g:hiddenField name="absPath" id="renameDialogAbsPath" value = "${absPath}" />	
				<g:hiddenField name="parentPath" id="renameDialogParentPath" value = "${parentPath}" />	
			</fieldset>
		</div>
		
		<div class="modal-footer">
			<div id="renameDialogToolbar">
						<div id="renameDialogMenu" class="pull-right">
							<button type="button" id="updateRenameButton"
								 value="update"
								onclick="submitRenameDialog()")><g:message code="default.button.update.label" /></button>
							<button type="button" id="cancelRenameButton"
								 value="cancelAdd"
								onclick="closeRenameDialog()")><g:message code="text.cancel" /></button>
						</div>
					</div>
			</div>
		</div>
</div>
<script>

	$(function() {
		$("#renameDialog").dialog({width:500, modal:true});
	});

	

</script>
