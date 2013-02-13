<div id="newFolderDialog">

	 <div class="modal-header">
    		<h3><g:message code="heading.new.folder.dialog"/></h3>
  	</div>
  	
  	 <div class="modal-body">
    	<label for="fileName"><g:message code="text.new.folder" />:<g:textArea name="fileName" id="fileName" value="${fileName}"  /></label>	
		<g:hiddenField name="absPath" id="newFolderDialogAbsPath" value = "${absPath }" />				
  	</div>

	<div class="modal-footer">
		<button type="button" id="updateNewFolderButton"
			 value="update"
			onclick="submitNewFolderDialog()")><g:message code="default.button.update.label" /></button>
		<button type="button" id="cancelRenameButton"
			 value="cancelAdd"
			onclick="closeNewFolderDialog()")><g:message code="text.cancel" /></button>
  	</div>
	
</div>

<script>

	$(function() {
		$("#newFolderDialog").dialog(
				{
					"modal":true,
					"width":"500px"
				}
		);
	});

	

</script>
