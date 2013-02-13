 <div class="alert alert-info">
 <g:message code="heading.metadata.details" />
</div>
<div id="metadataDialogDiv" >
	<div id="metadataDialogMessageArea" style="width:90%;">
		<g:renderErrors bean="${cmd}" />
	</div>
	<div style="clear: both;">
	
			<fieldset id="verticalForm">
				<g:hiddenField name="isCreate" value="true" />
				<label for="attribute"><g:message code="text.attribute" />:</label>
				<g:textField name="attribute" value="${attribute}"/>
				<br />
				<label for="value"><g:message code="text.value" />:</label>
				<g:textField name="value" value="${value}" />
				<br />
				<label for="unit"><g:message code="text.unit" />:</label>
				<g:textField name="unit" value="${unit}"/>
				<br />
				<div id="detailsDialogToolbar" class="well">
					<div id="detailsDialogMenu" 
						style="float: left, clear :   both; width: 90%;">
						<button type="button" id="updateMetadataDetailButton"
							 value="addMetadata"
							onclick="submitMetadataDialog()")><g:message code="default.button.update.label" /></button>
						<button type="button" id="cancelAddMetadataButton"
							 value="cancelAdd"
							onclick="closeMetadataDialog()")><g:message code="text.cancel" /></button>
					</div>
				</div>
			</fieldset>
	
	</div>
</div>
