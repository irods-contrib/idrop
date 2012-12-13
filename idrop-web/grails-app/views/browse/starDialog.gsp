<div id="starDialog">
	
	 <div class="modal-header">
    		<h3><g:message code="heading.star.dialog"/></h3>
  	</div>
	
	 <div class="modal-body">
	
			<fieldset id="verticalForm">
				<label for="fileName"><g:message code="text.name" />:</label>
				${absPath}
				
				<label for="description"><g:message code="text.description" />:</label>
				<g:textArea name="description" value="" rows="5" cols="50"/>
				
				<g:hiddenField name="absPath" id="absPath" value = "${absPath}" />	
			</fieldset>
		</div>
		
		<div class="modal-footer">
			<div id="starDialogToolbar">
						<div id="starDialogMenu" class="pull-right">
							<button type="button" id="starFileButton"
								 value="update"
								onclick="submitStarDialog()")><g:message code="default.button.update.label" /></button>
							<button type="button" id="cancelStarButton"
								 value="cancelStar"
								onclick="closeStarDialog()")><g:message code="text.cancel" /></button>
						</div>
					</div>
			</div>
		</div>
</div>
<script>

	$(function() {
		$("#starDialog").dialog({width:500, modal:true});
	});

</script>
