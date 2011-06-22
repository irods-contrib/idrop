<div id="aclDialogDiv" class="roundedContainer">
	<div id="aclDialogMessageArea" style="width:90%;">
		<!--  message area for acl dialogs -->
	</div>
	<div style="clear: both;">
		<g:form controller="sharing" action="updateAcl" method="post"
			name="aclUpdateForm">
			<fieldset id="verticalForm">
				<g:hiddenField name="isCreate" value="${isCreate}" />
				<label for="absPath">Path:</label>
				<g:textArea name="absPath" value="${absPath}" readonly="true" />
				<br /> <label for="userName">User Name:</label>
				<g:textField id="userName" name="userName" value="${userName}" />
				<br /> <label for="acl">Share Type:</label>
				<g:select name="acl" from="${userPermissionEnum}" />
				<br />
				<div id="detailsDialogToolbar" class="fg-toolbar ui-widget-header">
					<div id="detailsDialogMenu" class="fg-buttonset fg-buttonset-multi"
						style="float: left, clear :   both; width: 90%;">
						<button type="button" id="updateAclDetailButton"
							class="ui-state-default ui-corner-all" value="addAcl"
							onclick="submitAclDialog()")>Save</button>
						<button type="button" id="cancelAddAclButton"
							class="ui-state-default ui-corner-all" value="cancelAdd"
							onclick="closeAclAddDialog()")>Cancel</button>
					</div>
				</div>
			</fieldset>
		</g:form>
	</div>
</div>
s