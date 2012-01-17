<div id="aclDialogDiv" class="roundedContainer" style="display:block;clear:both;width:98%;height:350px;position:relative;">
	
	
			<div id="sharingLeft" style="float:left;width:60%;height:100%;position:relative;display:block;">
			<fieldset id="verticalForm">
				<g:hiddenField name="create" value="${create}" />
				<label for="absPath"><g:message code="text.path" />:</label>
				<g:textArea name="absPath" value="${absPath}" readonly="true" />
				<br /> <label for="userName"><g:message code="text.user" />:</label>
				<g:textField id="userName" name="userName" value="${userName}" />
				<button type="button" id="lookUpUserNames"
							class="ui-state-default ui-corner-all" value="lookUpUserNames"
							onclick="lookUpUserNameFromACLDialogClicked()")><label for="userName"><g:message code="text.search" /></button>
				<br /> <label for="acl"><g:message code="text.share.type" />:</label>
				<g:select name="acl" from="${userPermissionEnum}" />
				<br />
				<div id="detailsDialogToolbar" class="fg-toolbar ui-widget-header">
					<div id="detailsDialogMenu" class="fg-buttonset fg-buttonset-multi"
						style="float: left, clear :   both; width: 90%;">
						<button type="button" id="updateAclDetailButton"
							class="ui-state-default ui-corner-all" value="addAcl"
							onclick="submitAclDialog()")><g:message code="default.button.save.label" /></button>
						<button type="button" id="cancelAddAclButton"
							class="ui-state-default ui-corner-all" value="cancelAdd"
							onclick="closeAclAddDialog()")><g:message code="default.button.cancel.label" /></button>
					</div>
				</div>
			</fieldset>
			</div>
			<div id="sharingRight" style="display:block;float:right;position:relative;overflow:auto;height:100%;width=40%;">
			<!-- div for sharing pick list -->
			</div>

</div>

<script type="text/javascript">

/**
 * Handle click on search from ACL dialog to build user list
 */
function lookUpUserNameFromACLDialogClicked() {
	 var userName = $("#userName").val();
	 searchUsers(1, userName, "#sharingRight");
}

</script>