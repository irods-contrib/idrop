<div id="metadataDialogDiv" class="roundedContainer">
	<div id="metadataDialogMessageArea" style="width:90%;">
		<g:renderErrors bean="${cmd}" />
	</div>
	<div style="clear: both;">
		<g:form controller="metadata" action="addMetadata" method="post"
			name="metadataUpdateForm">
			<fieldset id="verticalForm">
				<g:hiddenField name="isCreate" value="${isCreate}" />
				<label for="absPath">Path:</label>
				<g:textArea name="absPath" value="${absPath}" readonly="true" />
				
				<br /> <label for="attribute">Attribute:</label>
				<g:textField name="attribute" />
				<br />
				<label for="value">Value:</label>
				<g:textField name="value" />
				<br />
				<label for="unit">Unit:</label>
				<g:textField name="unit" />
				<br />
				<div id="detailsDialogToolbar" class="fg-toolbar ui-widget-header">
					<div id="detailsDialogMenu" class="fg-buttonset fg-buttonset-multi"
						style="float: left, clear :   both; width: 90%;">
						<button type="button" id="updateMetadataDetailButton"
							class="ui-state-default ui-corner-all" value="addMetadata"
							onclick="submitMetadataDialog()")>Save</button>
						<button type="button" id="cancelAddMetadataButton"
							class="ui-state-default ui-corner-all" value="cancelAdd"
							onclick="closeMetadataAddDialog()")>Cancel</button>
					</div>
				</div>
			</fieldset>
		</g:form>
	</div>
</div>
